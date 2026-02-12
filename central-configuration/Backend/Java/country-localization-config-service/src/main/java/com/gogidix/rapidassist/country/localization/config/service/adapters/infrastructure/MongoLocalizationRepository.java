package com.gogidix.rapidassist.country.localization.config.service.adapters.infrastructure;

import com.gogidix.rapidassist.country.localization.config.service.domain.model.CountryLocalization;
import com.gogidix.rapidassist.country.localization.config.service.domain.model.LocalizedResource;
import com.gogidix.rapidassist.country.localization.config.service.domain.port.out.LocalizationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
public class MongoLocalizationRepository implements LocalizationRepository {

    private static final Logger logger = LoggerFactory.getLogger(MongoLocalizationRepository.class);
    private static final String COUNTRY_CACHE_PREFIX = "country:";
    private static final String RESOURCE_CACHE_PREFIX = "resource:";
    private static final long CACHE_TTL_HOURS = 24;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // Country localization persistence

    @Override
    public CompletableFuture<CountryLocalization> saveCountry(CountryLocalization country) {
        return CompletableFuture.supplyAsync(() -> {
            CountryDocument doc = CountryDocument.fromDomain(country);
            CountryDocument saved = mongoTemplate.save(doc);
            return saved.toDomain();
        });
    }

    @Override
    public CompletableFuture<Optional<CountryLocalization>> findByCountryCode(String countryCode) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("countryCode").is(countryCode.toUpperCase()));
            CountryDocument doc = mongoTemplate.findOne(query, CountryDocument.class);
            return Optional.ofNullable(doc).map(CountryDocument::toDomain);
        });
    }

    @Override
    public CompletableFuture<List<CountryLocalization>> findAllCountries() {
        return CompletableFuture.supplyAsync(() -> {
            return mongoTemplate.findAll(CountryDocument.class).stream()
                .map(CountryDocument::toDomain).toList();
        });
    }

    @Override
    public CompletableFuture<List<CountryLocalization>> findActiveCountries() {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("active").is(true));
            return mongoTemplate.find(query, CountryDocument.class).stream()
                .map(CountryDocument::toDomain).toList();
        });
    }

    @Override
    public CompletableFuture<List<CountryLocalization>> findByMeasurementSystem(CountryLocalization.MeasurementSystem system) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("measurementSystem").is(system));
            return mongoTemplate.find(query, CountryDocument.class).stream()
                .map(CountryDocument::toDomain).toList();
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteCountry(String countryCode) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("countryCode").is(countryCode.toUpperCase()));
            return mongoTemplate.remove(query, CountryDocument.class).getDeletedCount() > 0;
        });
    }

    // Resource persistence

    @Override
    public CompletableFuture<LocalizedResource> saveResource(LocalizedResource resource) {
        return CompletableFuture.supplyAsync(() -> {
            ResourceDocument doc = ResourceDocument.fromDomain(resource);
            ResourceDocument saved = mongoTemplate.save(doc);
            return saved.toDomain();
        });
    }

    @Override
    public CompletableFuture<Optional<LocalizedResource>> findResourceByKey(String resourceKey, String countryCode) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("resourceKey").is(resourceKey));
            query.addCriteria(Criteria.where("countryCode").is(countryCode));
            ResourceDocument doc = mongoTemplate.findOne(query, ResourceDocument.class);
            return Optional.ofNullable(doc).map(ResourceDocument::toDomain);
        });
    }

    @Override
    public CompletableFuture<List<LocalizedResource>> findResourcesByCountry(String countryCode) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("countryCode").is(countryCode));
            return mongoTemplate.find(query, ResourceDocument.class).stream()
                .map(ResourceDocument::toDomain).toList();
        });
    }

    @Override
    public CompletableFuture<List<LocalizedResource>> findResourcesByType(String resourceType) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("resourceType").is(resourceType));
            return mongoTemplate.find(query, ResourceDocument.class).stream()
                .map(ResourceDocument::toDomain).toList();
        });
    }

    @Override
    public CompletableFuture<List<LocalizedResource>> findAllResources() {
        return CompletableFuture.supplyAsync(() -> {
            return mongoTemplate.findAll(ResourceDocument.class).stream()
                .map(ResourceDocument::toDomain).toList();
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteResource(String resourceId) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("id").is(resourceId));
            return mongoTemplate.remove(query, ResourceDocument.class).getDeletedCount() > 0;
        });
    }

    // Cache operations

    @Override
    public CompletableFuture<Void> cacheCountry(CountryLocalization country) {
        return CompletableFuture.runAsync(() -> {
            String key = COUNTRY_CACHE_PREFIX + country.countryCode();
            redisTemplate.opsForValue().set(key, country, CACHE_TTL_HOURS, TimeUnit.HOURS);
        });
    }

    @Override
    public CompletableFuture<Optional<CountryLocalization>> getCachedCountry(String countryCode) {
        return CompletableFuture.supplyAsync(() -> {
            String key = COUNTRY_CACHE_PREFIX + countryCode.toUpperCase();
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached instanceof CountryLocalization) {
                return Optional.of((CountryLocalization) cached);
            }
            return Optional.empty();
        });
    }

    @Override
    public CompletableFuture<Void> cacheResource(LocalizedResource resource) {
        return CompletableFuture.runAsync(() -> {
            String key = RESOURCE_CACHE_PREFIX + resource.countryCode() + ":" + resource.resourceKey();
            redisTemplate.opsForValue().set(key, resource, CACHE_TTL_HOURS, TimeUnit.HOURS);
        });
    }

    @Override
    public CompletableFuture<Optional<LocalizedResource>> getCachedResource(String resourceKey, String countryCode) {
        return CompletableFuture.supplyAsync(() -> {
            String key = RESOURCE_CACHE_PREFIX + countryCode + ":" + resourceKey;
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached instanceof LocalizedResource) {
                return Optional.of((LocalizedResource) cached);
            }
            return Optional.empty();
        });
    }

    @Override
    public CompletableFuture<Void> evictCountry(String countryCode) {
        return CompletableFuture.runAsync(() -> {
            String key = COUNTRY_CACHE_PREFIX + countryCode.toUpperCase();
            redisTemplate.delete(key);
        });
    }

    @Override
    public CompletableFuture<Void> evictResource(String resourceKey, String countryCode) {
        return CompletableFuture.runAsync(() -> {
            String key = RESOURCE_CACHE_PREFIX + countryCode + ":" + resourceKey;
            redisTemplate.delete(key);
        });
    }

    @Override
    public CompletableFuture<Void> evictAllCountryResources(String countryCode) {
        return CompletableFuture.runAsync(() -> {
            Set<String> keys = redisTemplate.keys(RESOURCE_CACHE_PREFIX + countryCode + ":*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        });
    }

    // MongoDB Documents

    @org.springframework.data.mongodb.core.mapping.Document(collection = "country_localizations")
    public static class CountryDocument {
        private String id;
        private String tenantId;
        private String countryCode;
        private String countryName;
        private CountryLocalization.LocaleConfig locale;
        private CountryLocalization.CurrencyConfig currency;
        private CountryLocalization.DateTimeConfig dateTime;
        private CountryLocalization.AddressFormat addressFormat;
        private CountryLocalization.PhoneFormat phoneFormat;
        private CountryLocalization.EmergencyServices emergencyServices;
        private CountryLocalization.LegalRequirements legalRequirements;
        private CountryLocalization.MeasurementSystem measurementSystem;
        private boolean active;
        private String createdBy;
        private java.time.Instant createdAt;
        private String updatedBy;
        private java.time.Instant updatedAt;
        private Integer version;

        public static CountryDocument fromDomain(CountryLocalization country) {
            CountryDocument doc = new CountryDocument();
            doc.id = country.id();
            doc.tenantId = country.tenantId();
            doc.countryCode = country.countryCode();
            doc.countryName = country.countryName();
            doc.locale = country.locale();
            doc.currency = country.currency();
            doc.dateTime = country.dateTime();
            doc.addressFormat = country.addressFormat();
            doc.phoneFormat = country.phoneFormat();
            doc.emergencyServices = country.emergencyServices();
            doc.legalRequirements = country.legalRequirements();
            doc.measurementSystem = country.measurementSystem();
            doc.active = country.active();
            doc.createdBy = country.createdBy();
            doc.createdAt = country.createdAt();
            doc.updatedBy = country.updatedBy();
            doc.updatedAt = country.updatedAt();
            doc.version = country.version();
            return doc;
        }

        public CountryLocalization toDomain() {
            return CountryLocalization.builder()
                .id(id)
                .tenantId(tenantId)
                .countryCode(countryCode)
                .countryName(countryName)
                .locale(locale)
                .currency(currency)
                .dateTime(dateTime)
                .addressFormat(addressFormat)
                .phoneFormat(phoneFormat)
                .emergencyServices(emergencyServices)
                .legalRequirements(legalRequirements)
                .measurementSystem(measurementSystem)
                .active(active)
                .createdBy(createdBy)
                .createdAt(createdAt)
                .updatedBy(updatedBy)
                .updatedAt(updatedAt)
                .version(version)
                .build();
        }

        @org.springframework.data.annotation.Id
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getTenantId() { return tenantId; }
        public void setTenantId(String tenantId) { this.tenantId = tenantId; }
        public String getCountryCode() { return countryCode; }
        public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
        public String getCountryName() { return countryName; }
        public void setCountryName(String countryName) { this.countryName = countryName; }
        public CountryLocalization.LocaleConfig getLocale() { return locale; }
        public void setLocale(CountryLocalization.LocaleConfig locale) { this.locale = locale; }
        public CountryLocalization.CurrencyConfig getCurrency() { return currency; }
        public void setCurrency(CountryLocalization.CurrencyConfig currency) { this.currency = currency; }
        public CountryLocalization.DateTimeConfig getDateTime() { return dateTime; }
        public void setDateTime(CountryLocalization.DateTimeConfig dateTime) { this.dateTime = dateTime; }
        public CountryLocalization.AddressFormat getAddressFormat() { return addressFormat; }
        public void setAddressFormat(CountryLocalization.AddressFormat addressFormat) { this.addressFormat = addressFormat; }
        public CountryLocalization.PhoneFormat getPhoneFormat() { return phoneFormat; }
        public void setPhoneFormat(CountryLocalization.PhoneFormat phoneFormat) { this.phoneFormat = phoneFormat; }
        public CountryLocalization.EmergencyServices getEmergencyServices() { return emergencyServices; }
        public void setEmergencyServices(CountryLocalization.EmergencyServices emergencyServices) { this.emergencyServices = emergencyServices; }
        public CountryLocalization.LegalRequirements getLegalRequirements() { return legalRequirements; }
        public void setLegalRequirements(CountryLocalization.LegalRequirements legalRequirements) { this.legalRequirements = legalRequirements; }
        public CountryLocalization.MeasurementSystem getMeasurementSystem() { return measurementSystem; }
        public void setMeasurementSystem(CountryLocalization.MeasurementSystem measurementSystem) { this.measurementSystem = measurementSystem; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        public String getCreatedBy() { return createdBy; }
        public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
        public java.time.Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(java.time.Instant createdAt) { this.createdAt = createdAt; }
        public String getUpdatedBy() { return updatedBy; }
        public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
        public java.time.Instant getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(java.time.Instant updatedAt) { this.updatedAt = updatedAt; }
        public Integer getVersion() { return version; }
        public void setVersion(Integer version) { this.version = version; }
    }

    @org.springframework.data.mongodb.core.mapping.Document(collection = "localized_resources")
    public static class ResourceDocument {
        private String id;
        private String resourceKey;
        private String countryCode;
        private String resourceType;
        private Map<String, String> translations;
        private String defaultValue;
        private String context;
        private boolean active;
        private String createdBy;
        private java.time.Instant createdAt;
        private String updatedBy;
        private java.time.Instant updatedAt;
        private Integer version;

        public static ResourceDocument fromDomain(LocalizedResource resource) {
            ResourceDocument doc = new ResourceDocument();
            doc.id = resource.id();
            doc.resourceKey = resource.resourceKey();
            doc.countryCode = resource.countryCode();
            doc.resourceType = resource.resourceType();
            doc.translations = resource.translations();
            doc.defaultValue = resource.defaultValue();
            doc.context = resource.context();
            doc.active = resource.active();
            doc.createdBy = resource.createdBy();
            doc.createdAt = resource.createdAt();
            doc.updatedBy = resource.updatedBy();
            doc.updatedAt = resource.updatedAt();
            doc.version = resource.version();
            return doc;
        }

        public LocalizedResource toDomain() {
            return LocalizedResource.builder()
                .id(id)
                .resourceKey(resourceKey)
                .countryCode(countryCode)
                .resourceType(resourceType)
                .translations(translations)
                .defaultValue(defaultValue)
                .context(context)
                .active(active)
                .createdBy(createdBy)
                .createdAt(createdAt)
                .updatedBy(updatedBy)
                .updatedAt(updatedAt)
                .version(version)
                .build();
        }

        @org.springframework.data.annotation.Id
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getResourceKey() { return resourceKey; }
        public void setResourceKey(String resourceKey) { this.resourceKey = resourceKey; }
        public String getCountryCode() { return countryCode; }
        public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
        public String getResourceType() { return resourceType; }
        public void setResourceType(String resourceType) { this.resourceType = resourceType; }
        public Map<String, String> getTranslations() { return translations; }
        public void setTranslations(Map<String, String> translations) { this.translations = translations; }
        public String getDefaultValue() { return defaultValue; }
        public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }
        public String getContext() { return context; }
        public void setContext(String context) { this.context = context; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        public String getCreatedBy() { return createdBy; }
        public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
        public java.time.Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(java.time.Instant createdAt) { this.createdAt = createdAt; }
        public String getUpdatedBy() { return updatedBy; }
        public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
        public java.time.Instant getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(java.time.Instant updatedAt) { this.updatedAt = updatedAt; }
        public Integer getVersion() { return version; }
        public void setVersion(Integer version) { this.version = version; }
    }
}

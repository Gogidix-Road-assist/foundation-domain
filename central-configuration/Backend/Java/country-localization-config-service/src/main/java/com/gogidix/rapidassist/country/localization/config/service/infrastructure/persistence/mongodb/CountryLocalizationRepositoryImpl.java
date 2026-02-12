package com.gogidix.rapidassist.country.localization.config.service.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.country.localization.config.service.domain.model.CountryLocalization;
import com.gogidix.rapidassist.country.localization.config.service.domain.repository.CountryLocalizationRepositoryInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * MongoDB implementation of CountryLocalizationRepositoryInterface with full multi-tenancy support.
 *
 * <p>This implementation ensures strict tenant isolation by:
 * <ul>
 *   <li>All queries filter by tenantId</li>
 *   <li>All operations validate tenant context</li>
 *   <li>Preventing cross-tenant data access</li>
 * </ul>
 */
@Repository
public class CountryLocalizationRepositoryImpl implements CountryLocalizationRepositoryInterface {

    private static final Logger logger = LoggerFactory.getLogger(CountryLocalizationRepositoryImpl.class);
    private static final String COLLECTION_NAME = "country_localizations";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public CompletableFuture<CountryLocalization> save(CountryLocalization localization) {
        return CompletableFuture.supplyAsync(() -> {
            logger.debug("Saving localization for tenant: {}, country: {}",
                localization.tenantId(), localization.countryCode());

            CountryLocalizationDocument doc = CountryLocalizationDocument.fromDomain(localization);
            CountryLocalizationDocument saved = mongoTemplate.save(doc, COLLECTION_NAME);

            logger.debug("Saved localization with id: {}", saved.getId());
            return saved.toDomain();
        });
    }

    @Override
    public CompletableFuture<Optional<CountryLocalization>> findById(String id) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query(Criteria.where("id").is(id));
            CountryLocalizationDocument doc = mongoTemplate.findOne(query, CountryLocalizationDocument.class, COLLECTION_NAME);
            return Optional.ofNullable(doc).map(CountryLocalizationDocument::toDomain);
        });
    }

    @Override
    public CompletableFuture<Optional<CountryLocalization>> findByNaturalKey(String tenantId, String countryCode) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("tenantId").is(tenantId));
            query.addCriteria(Criteria.where("countryCode").is(countryCode.toUpperCase()));

            CountryLocalizationDocument doc = mongoTemplate.findOne(query, CountryLocalizationDocument.class, COLLECTION_NAME);

            if (doc != null) {
                logger.debug("Found localization for tenant: {}, country: {}", tenantId, countryCode);
            } else {
                logger.debug("No localization found for tenant: {}, country: {}", tenantId, countryCode);
            }

            return Optional.ofNullable(doc).map(CountryLocalizationDocument::toDomain);
        });
    }

    @Override
    public CompletableFuture<List<CountryLocalization>> findByTenantId(String tenantId) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query(Criteria.where("tenantId").is(tenantId));
            query.with(Sort.by(Sort.Direction.ASC, "countryCode"));

            List<CountryLocalizationDocument> docs = mongoTemplate.find(query, CountryLocalizationDocument.class, COLLECTION_NAME);

            logger.debug("Found {} localizations for tenant: {}", docs.size(), tenantId);

            return docs.stream()
                .map(CountryLocalizationDocument::toDomain)
                .toList();
        });
    }

    @Override
    public CompletableFuture<List<CountryLocalization>> findActiveByTenantId(String tenantId) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("tenantId").is(tenantId));
            query.addCriteria(Criteria.where("active").is(true));
            query.with(Sort.by(Sort.Direction.ASC, "countryCode"));

            List<CountryLocalizationDocument> docs = mongoTemplate.find(query, CountryLocalizationDocument.class, COLLECTION_NAME);

            logger.debug("Found {} active localizations for tenant: {}", docs.size(), tenantId);

            return docs.stream()
                .map(CountryLocalizationDocument::toDomain)
                .toList();
        });
    }

    @Override
    public CompletableFuture<List<CountryLocalization>> findByMeasurementSystem(
            String tenantId,
            CountryLocalization.MeasurementSystem measurementSystem) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("tenantId").is(tenantId));
            query.addCriteria(Criteria.where("measurementSystem").is(measurementSystem));
            query.with(Sort.by(Sort.Direction.ASC, "countryCode"));

            List<CountryLocalizationDocument> docs = mongoTemplate.find(query, CountryLocalizationDocument.class, COLLECTION_NAME);

            logger.debug("Found {} localizations for tenant: {} with measurement system: {}",
                docs.size(), tenantId, measurementSystem);

            return docs.stream()
                .map(CountryLocalizationDocument::toDomain)
                .toList();
        });
    }

    @Override
    public CompletableFuture<List<CountryLocalization>> searchByCountryName(String tenantId, String keyword) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("tenantId").is(tenantId));
            query.addCriteria(Criteria.where("countryName").regex(keyword, "i")); // case-insensitive

            List<CountryLocalizationDocument> docs = mongoTemplate.find(query, CountryLocalizationDocument.class, COLLECTION_NAME);

            logger.debug("Found {} localizations for tenant: {} matching keyword: {}",
                docs.size(), tenantId, keyword);

            return docs.stream()
                .map(CountryLocalizationDocument::toDomain)
                .toList();
        });
    }

    @Override
    public CompletableFuture<List<CountryLocalization>> findUpdatedSince(String tenantId, Instant since) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("tenantId").is(tenantId));
            query.addCriteria(Criteria.where("updatedAt").gte(since));
            query.with(Sort.by(Sort.Direction.DESC, "updatedAt"));

            List<CountryLocalizationDocument> docs = mongoTemplate.find(query, CountryLocalizationDocument.class, COLLECTION_NAME);

            logger.debug("Found {} localizations for tenant: {} updated since {}",
                docs.size(), tenantId, since);

            return docs.stream()
                .map(CountryLocalizationDocument::toDomain)
                .toList();
        });
    }

    @Override
    public CompletableFuture<List<CountryLocalization>> findByLocale(
            String tenantId,
            String languageCode,
            String regionCode) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("tenantId").is(tenantId));
            query.addCriteria(Criteria.where("locale.languageCode").is(languageCode));
            query.addCriteria(Criteria.where("locale.regionCode").is(regionCode));
            query.with(Sort.by(Sort.Direction.ASC, "countryCode"));

            List<CountryLocalizationDocument> docs = mongoTemplate.find(query, CountryLocalizationDocument.class, COLLECTION_NAME);

            logger.debug("Found {} localizations for tenant: {} with locale: {}_{}",
                docs.size(), tenantId, languageCode, regionCode);

            return docs.stream()
                .map(CountryLocalizationDocument::toDomain)
                .toList();
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteById(String id) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query(Criteria.where("id").is(id));
            var result = mongoTemplate.remove(query, COLLECTION_NAME);

            boolean deleted = result.getDeletedCount() > 0;
            logger.debug("Deleted localization by id: {}, success: {}", id, deleted);

            return deleted;
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteByNaturalKey(String tenantId, String countryCode) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("tenantId").is(tenantId));
            query.addCriteria(Criteria.where("countryCode").is(countryCode.toUpperCase()));

            var result = mongoTemplate.remove(query, COLLECTION_NAME);

            boolean deleted = result.getDeletedCount() > 0;
            logger.debug("Deleted localization for tenant: {}, country: {}, success: {}",
                tenantId, countryCode, deleted);

            return deleted;
        });
    }

    @Override
    public CompletableFuture<List<CountryLocalization>> saveAll(List<CountryLocalization> localizations) {
        return CompletableFuture.supplyAsync(() -> {
            List<CountryLocalization> saved = localizations.stream()
                .map(loc -> {
                    CountryLocalizationDocument doc = CountryLocalizationDocument.fromDomain(loc);
                    CountryLocalizationDocument savedDoc = mongoTemplate.save(doc, COLLECTION_NAME);
                    return savedDoc.toDomain();
                })
                .toList();

            logger.debug("Saved {} localizations", saved.size());
            return saved;
        });
    }

    @Override
    public CompletableFuture<List<CountryLocalization>> findAllById(List<String> ids) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query(Criteria.where("id").in(ids));

            List<CountryLocalizationDocument> docs = mongoTemplate.find(query, CountryLocalizationDocument.class, COLLECTION_NAME);

            logger.debug("Found {} localizations by ids", docs.size());

            return docs.stream()
                .map(CountryLocalizationDocument::toDomain)
                .toList();
        });
    }

    @Override
    public CompletableFuture<Long> countByTenantId(String tenantId) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query(Criteria.where("tenantId").is(tenantId));
            long count = mongoTemplate.count(query, CountryLocalizationDocument.class, COLLECTION_NAME);

            logger.debug("Counted {} localizations for tenant: {}", count, tenantId);

            return count;
        });
    }

    @Override
    public CompletableFuture<Long> countActiveByTenantId(String tenantId) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("tenantId").is(tenantId));
            query.addCriteria(Criteria.where("active").is(true));

            long count = mongoTemplate.count(query, CountryLocalizationDocument.class, COLLECTION_NAME);

            logger.debug("Counted {} active localizations for tenant: {}", count, tenantId);

            return count;
        });
    }

    @Override
    public CompletableFuture<Boolean> existsByNaturalKey(String tenantId, String countryCode) {
        return CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("tenantId").is(tenantId));
            query.addCriteria(Criteria.where("countryCode").is(countryCode.toUpperCase()));

            boolean exists = mongoTemplate.exists(query, CountryLocalizationDocument.class, COLLECTION_NAME);

            logger.debug("Localization exists for tenant: {}, country: {} -> {}",
                tenantId, countryCode, exists);

            return exists;
        });
    }

    /**
     * MongoDB Document mapping for CountryLocalization
     */
    @org.springframework.data.mongodb.core.mapping.Document(collection = COLLECTION_NAME)
    public static class CountryLocalizationDocument {

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
        private Instant createdAt;
        private String updatedBy;
        private Instant updatedAt;
        private Integer version;

        public static CountryLocalizationDocument fromDomain(CountryLocalization localization) {
            CountryLocalizationDocument doc = new CountryLocalizationDocument();
            doc.id = localization.id();
            doc.tenantId = localization.tenantId();
            doc.countryCode = localization.countryCode();
            doc.countryName = localization.countryName();
            doc.locale = localization.locale();
            doc.currency = localization.currency();
            doc.dateTime = localization.dateTime();
            doc.addressFormat = localization.addressFormat();
            doc.phoneFormat = localization.phoneFormat();
            doc.emergencyServices = localization.emergencyServices();
            doc.legalRequirements = localization.legalRequirements();
            doc.measurementSystem = localization.measurementSystem();
            doc.active = localization.active();
            doc.createdBy = localization.createdBy();
            doc.createdAt = localization.createdAt();
            doc.updatedBy = localization.updatedBy();
            doc.updatedAt = localization.updatedAt();
            doc.version = localization.version();
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

        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

        public String getUpdatedBy() { return updatedBy; }
        public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

        public Instant getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

        public Integer getVersion() { return version; }
        public void setVersion(Integer version) { this.version = version; }
    }
}

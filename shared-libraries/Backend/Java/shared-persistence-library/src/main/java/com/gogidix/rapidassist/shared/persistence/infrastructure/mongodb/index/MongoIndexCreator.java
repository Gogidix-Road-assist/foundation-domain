package com.gogidix.rapidassist.shared.persistence.infrastructure.mongodb.index;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper component for creating MongoDB indexes to support multi-tenant queries.
 * <p>
 * This class provides utilities to create the necessary indexes for efficient
 * tenant-scoped queries in a multi-tenant MongoDB database. It creates compound
 * indexes that optimize queries filtering by tenant_id and other common fields.
 * </p>
 * <p>
 * <strong>Indexes Created:</strong>
 * </p>
 * <ul>
 *   <li>Compound index on (tenantId, deleted) - for active document queries</li>
 *   <li>Compound index on (tenantId, createdAt) - for time-sorted queries</li>
 *   <li>Compound index on (tenantId, updatedAt) - for sync queries</li>
 *   <li>Single index on tenantId - for tenant existence checks</li>
 * </ul>
 * <p>
 * Usage example:
 * <pre>{@code
 * @Component
 * public class DataInitializer implements ApplicationRunner {
 *     private final MongoIndexCreator indexCreator;
 *
 *     public DataInitializer(MongoIndexCreator indexCreator) {
 *         this.indexCreator = indexCreator;
 *     }
 *
 *     @Override
 *     public void run(ApplicationArguments args) {
 *         // Create indexes for all collections
 *         indexCreator.createTenantIndexes("insurance_policies");
 *         indexCreator.createTenantIndexes("claims");
 *         indexCreator.createTenantIndexes("customers");
 *     }
 * }
 * }</pre>
 * </p>
 * <p>
 * <strong>Note:</strong> While indexes are created via {@code @Indexed} and
 * {@code @CompoundIndex} annotations on {@code TenantAwareDocument}, this
 * component provides explicit index creation for scenarios requiring
 * programmatic control.
 * </p>
 *
 * @see com.gogidix.rapidassist.shared.persistence.infrastructure.mongodb.document.TenantAwareDocument
 */
@Component
public class MongoIndexCreator {

    private static final Logger log = LoggerFactory.getLogger(MongoIndexCreator.class);

    private final MongoTemplate mongoTemplate;

    /**
     * Creates a new MongoIndexCreator instance.
     *
     * @param mongoTemplate the MongoTemplate for database access
     */
    public MongoIndexCreator(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Creates all tenant-related indexes for a specific collection.
     * <p>
     * This method creates:
     * <ul>
     *   <li>Compound index: (tenantId, deleted)</li>
     *   <li>Compound index: (tenantId, createdAt) - descending</li>
     *   <li>Compound index: (tenantId, updatedAt) - descending</li>
     *   <li>Single index: tenantId</li>
     *   <li>Single index: createdAt (descending)</li>
     *   <li>Single index: deleted</li>
     * </ul>
     *
     * @param collectionName the name of the MongoDB collection
     */
    public void createTenantIndexes(String collectionName) {
        log.info("Creating tenant indexes for collection: {}", collectionName);

        MongoCollection<Document> collection = getCollection(collectionName);

        // Create compound index for tenant + deleted (most common query pattern)
        createCompoundIndex(collection, "tenant_deleted_idx",
            List.of(
                Indexes.ascending("tenant_id"),
                Indexes.ascending("deleted")
            )
        );

        // Create compound index for tenant + createdAt (for time-ordered queries)
        createCompoundIndex(collection, "tenant_created_idx",
            List.of(
                Indexes.ascending("tenant_id"),
                Indexes.descending("created_at")
            )
        );

        // Create compound index for tenant + updatedAt (for sync queries)
        createCompoundIndex(collection, "tenant_updated_idx",
            List.of(
                Indexes.ascending("tenant_id"),
                Indexes.descending("updated_at")
            )
        );

        // Create single field indexes
        createSingleIndex(collection, "tenant_id");
        createSingleIndex(collection, "deleted");
        createDescendingIndex(collection, "created_at");
        createDescendingIndex(collection, "updated_at");
        createSingleIndex(collection, "created_by");

        log.info("Successfully created tenant indexes for collection: {}", collectionName);
    }

    /**
     * Creates tenant indexes for multiple collections.
     * <p>
     * This is a convenience method for bulk index creation.
     * </p>
     *
     * @param collectionNames list of collection names to index
     */
    public void createAllTenantIndexes(List<String> collectionNames) {
        log.info("Creating tenant indexes for {} collections", collectionNames.size());
        collectionNames.forEach(this::createTenantIndexes);
        log.info("Completed creating tenant indexes for all collections");
    }

    /**
     * Creates a compound index on the specified collection.
     * <p>
     * Compound indexes are efficient for queries that filter on multiple fields.
     * </p>
     *
     * @param collection   the MongoDB collection
     * @param indexName    the name of the index to create
     * @param indexKeys    the index keys in order
     */
    public void createCompoundIndex(MongoCollection<Document> collection, String indexName, List<Bson> indexKeys) {
        try {
            Bson compoundIndex = Indexes.compoundIndex(indexKeys);
            IndexOptions options = new IndexOptions()
                .name(indexName)
                .background(true);

            collection.createIndex(compoundIndex, options);
            log.debug("Created compound index '{}' on collection: {}", indexName, collection.getNamespace().getCollectionName());
        } catch (Exception e) {
            log.warn("Failed to create compound index '{}' on collection: {} - {}",
                indexName, collection.getNamespace().getCollectionName(), e.getMessage());
        }
    }

    /**
     * Creates a single ascending index on the specified field.
     *
     * @param collection the MongoDB collection
     * @param fieldName  the field name to index
     */
    public void createSingleIndex(MongoCollection<Document> collection, String fieldName) {
        try {
            IndexOptions options = new IndexOptions()
                .name(fieldName + "_asc_idx")
                .background(true);
            collection.createIndex(Indexes.ascending(fieldName), options);
            log.debug("Created ascending index on field '{}' for collection: {}",
                fieldName, collection.getNamespace().getCollectionName());
        } catch (Exception e) {
            log.debug("Failed to create index on field '{}' for collection: {} - {}",
                fieldName, collection.getNamespace().getCollectionName(), e.getMessage());
        }
    }

    /**
     * Creates a single descending index on the specified field.
     * <p>
     * Descending indexes are useful for time-based sorting.
     * </p>
     *
     * @param collection the MongoDB collection
     * @param fieldName  the field name to index
     */
    public void createDescendingIndex(MongoCollection<Document> collection, String fieldName) {
        try {
            IndexOptions options = new IndexOptions()
                .name(fieldName + "_desc_idx")
                .background(true);
            collection.createIndex(Indexes.descending(fieldName), options);
            log.debug("Created descending index on field '{}' for collection: {}",
                fieldName, collection.getNamespace().getCollectionName());
        } catch (Exception e) {
            log.debug("Failed to create descending index on field '{}' for collection: {} - {}",
                fieldName, collection.getNamespace().getCollectionName(), e.getMessage());
        }
    }

    /**
     * Lists all indexes on a collection.
     * <p>
     * Useful for debugging and verifying index creation.
     * </p>
     *
     * @param collectionName the name of the MongoDB collection
     * @return list of documents describing the indexes
     */
    public List<Document> listIndexes(String collectionName) {
        MongoCollection<Document> collection = getCollection(collectionName);
        List<Document> indexes = new ArrayList<>();
        collection.listIndexes().into(indexes);
        return indexes;
    }

    /**
     * Drops a specific index from a collection.
     *
     * @param collectionName the name of the MongoDB collection
     * @param indexName      the name of the index to drop
     */
    public void dropIndex(String collectionName, String indexName) {
        MongoCollection<Document> collection = getCollection(collectionName);
        try {
            collection.dropIndex(indexName);
            log.info("Dropped index '{}' from collection: {}", indexName, collectionName);
        } catch (Exception e) {
            log.warn("Failed to drop index '{}' from collection: {} - {}",
                indexName, collectionName, e.getMessage());
        }
    }

    /**
     * Drops all indexes except the default _id index from a collection.
     *
     * @param collectionName the name of the MongoDB collection
     */
    public void dropAllIndexes(String collectionName) {
        MongoCollection<Document> collection = getCollection(collectionName);
        try {
            collection.dropIndexes();
            log.info("Dropped all indexes from collection: {}", collectionName);
        } catch (Exception e) {
            log.warn("Failed to drop indexes from collection: {} - {}", collectionName, e.getMessage());
        }
    }

    /**
     * Gets a MongoDB collection by name.
     *
     * @param collectionName the name of the collection
     * @return the MongoCollection
     */
    private MongoCollection<Document> getCollection(String collectionName) {
        MongoDatabase database = mongoTemplate.getDb();
        return database.getCollection(collectionName);
    }

    /**
     * Creates a text index on the specified fields.
     * <p>
     * Text indexes enable full-text search capabilities.
     * </p>
     *
     * @param collectionName the name of the MongoDB collection
     * @param fieldNames     the field names to include in the text index
     */
    public void createTextIndex(String collectionName, String... fieldNames) {
        MongoCollection<Document> collection = getCollection(collectionName);
        try {
            Document textIndex = new Document();
            for (String field : fieldNames) {
                textIndex.append(field, "text");
            }

            IndexOptions options = new IndexOptions()
                .name("text_search_idx")
                .background(true)
                .weights(new Document()); // Can specify weights per field

            collection.createIndex(textIndex, options);
            log.info("Created text index on fields {} for collection: {}",
                String.join(", ", fieldNames), collectionName);
        } catch (Exception e) {
            log.warn("Failed to create text index for collection: {} - {}", collectionName, e.getMessage());
        }
    }

    /**
     * Creates a geospatial index on the specified field.
     * <p>
     * Geospatial indexes enable location-based queries.
     * </p>
     *
     * @param collectionName the name of the MongoDB collection
     * @param fieldName      the field name containing geospatial data
     */
    public void createGeospatialIndex(String collectionName, String fieldName) {
        MongoCollection<Document> collection = getCollection(collectionName);
        try {
            IndexOptions options = new IndexOptions()
                .name(fieldName + "_geo_idx")
                .background(true);

            collection.createIndex(Indexes.geo2dsphere(fieldName), options);
            log.info("Created geospatial index on field '{}' for collection: {}", fieldName, collectionName);
        } catch (Exception e) {
            log.warn("Failed to create geospatial index for collection: {} - {}", collectionName, e.getMessage());
        }
    }

    /**
     * Creates a hashed index on the specified field.
     * <p>
     * Hashed indexes are useful for sharding on high-cardinality fields.
     * </p>
     *
     * @param collectionName the name of the MongoDB collection
     * @param fieldName      the field name to hash
     */
    public void createHashedIndex(String collectionName, String fieldName) {
        MongoCollection<Document> collection = getCollection(collectionName);
        try {
            IndexOptions options = new IndexOptions()
                .name(fieldName + "_hashed_idx")
                .background(true);

            collection.createIndex(Indexes.hashed(fieldName), options);
            log.info("Created hashed index on field '{}' for collection: {}", fieldName, collectionName);
        } catch (Exception e) {
            log.warn("Failed to create hashed index for collection: {} - {}", collectionName, e.getMessage());
        }
    }
}

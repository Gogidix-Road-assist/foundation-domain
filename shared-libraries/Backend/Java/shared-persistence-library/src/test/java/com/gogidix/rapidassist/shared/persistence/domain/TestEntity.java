package com.gogidix.rapidassist.shared.persistence.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Test entity for unit testing.
 */
@Document(collection = "test_entities")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class TestEntity extends BaseEntity {
    private String name;
    private String description;
}

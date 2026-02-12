package com.gogidix.rapidassist.config.service.domain.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.Set;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", include = JsonTypeInfo.As.PROPERTY)
public interface ConfigurationSchema {

    @NotNull
    ConfigurationDataType dataType();

    boolean required();

    String description();

    Set<String> allowedValues();

    Map<String, Object> metadata();

    interface PrimitiveSchema extends ConfigurationSchema {
    }

    @JsonTypeName("string")
    record StringSchema(
        @NotNull ConfigurationDataType dataType,
        boolean required,
        String description,
        Set<String> allowedValues,
        Map<String, Object> metadata,
        Integer minLength,
        Integer maxLength,
        String pattern,
        String format
    ) implements PrimitiveSchema {

        public StringSchema {
            if (dataType != ConfigurationDataType.STRING) {
                throw new IllegalArgumentException("Data type must be STRING");
            }
        }

        public static StringSchema of(boolean required) {
            return new StringSchema(
                ConfigurationDataType.STRING, required, null,
                Set.of(), Map.of(), null, null, null, null
            );
        }
    }

    @JsonTypeName("number")
    record NumberSchema(
        @NotNull ConfigurationDataType dataType,
        boolean required,
        String description,
        Set<String> allowedValues,
        Map<String, Object> metadata,
        Double minimum,
        Double maximum,
        Double exclusiveMinimum,
        Double exclusiveMaximum,
        String format
    ) implements PrimitiveSchema {

        public NumberSchema {
            if (dataType != ConfigurationDataType.NUMBER) {
                throw new IllegalArgumentException("Data type must be NUMBER");
            }
        }

        public static NumberSchema of(boolean required) {
            return new NumberSchema(
                ConfigurationDataType.NUMBER, required, null,
                Set.of(), Map.of(), null, null, null, null, null
            );
        }
    }

    @JsonTypeName("boolean")
    record BooleanSchema(
        @NotNull ConfigurationDataType dataType,
        boolean required,
        String description,
        Set<String> allowedValues,
        Map<String, Object> metadata
    ) implements PrimitiveSchema {

        public BooleanSchema {
            if (dataType != ConfigurationDataType.BOOLEAN) {
                throw new IllegalArgumentException("Data type must be BOOLEAN");
            }
        }

        public static BooleanSchema of(boolean required) {
            return new BooleanSchema(
                ConfigurationDataType.BOOLEAN, required, null,
                Set.of(), Map.of()
            );
        }
    }

    @JsonTypeName("array")
    record ArraySchema(
        @NotNull ConfigurationDataType dataType,
        boolean required,
        String description,
        Set<String> allowedValues,
        Map<String, Object> metadata,
        ConfigurationSchema items,
        Integer minItems,
        Integer maxItems,
        boolean uniqueItems
    ) implements ConfigurationSchema {

        public ArraySchema {
            if (dataType != ConfigurationDataType.ARRAY) {
                throw new IllegalArgumentException("Data type must be ARRAY");
            }
        }

        public static ArraySchema of(ConfigurationSchema items, boolean required) {
            return new ArraySchema(
                ConfigurationDataType.ARRAY, required, null,
                Set.of(), Map.of(), items, null, null, false
            );
        }
    }

    @JsonTypeName("object")
    record ObjectSchema(
        @NotNull ConfigurationDataType dataType,
        boolean required,
        String description,
        Set<String> allowedValues,
        Map<String, Object> metadata,
        Map<String, ConfigurationSchema> properties,
        Set<String> requiredProperties,
        boolean additionalProperties
    ) implements ConfigurationSchema {

        public ObjectSchema {
            if (dataType != ConfigurationDataType.OBJECT) {
                throw new IllegalArgumentException("Data type must be OBJECT");
            }
        }

        public static ObjectSchema of(Map<String, ConfigurationSchema> properties, boolean required) {
            return new ObjectSchema(
                ConfigurationDataType.OBJECT, required, null,
                Set.of(), Map.of(), properties, Set.of(), true
            );
        }
    }

    @JsonTypeName("json")
    record JsonSchema(
        @NotNull ConfigurationDataType dataType,
        boolean required,
        String description,
        Set<String> allowedValues,
        Map<String, Object> metadata,
        Map<String, Object> jsonSchema
    ) implements ConfigurationSchema {

        public JsonSchema {
            if (dataType != ConfigurationDataType.JSON) {
                throw new IllegalArgumentException("Data type must be JSON");
            }
        }

        public static JsonSchema of(Map<String, Object> jsonSchema, boolean required) {
            return new JsonSchema(
                ConfigurationDataType.JSON, required, null,
                Set.of(), Map.of(), jsonSchema
            );
        }
    }

    @JsonTypeName("yaml")
    record YamlSchema(
        @NotNull ConfigurationDataType dataType,
        boolean required,
        String description,
        Set<String> allowedValues,
        Map<String, Object> metadata,
        Map<String, Object> yamlSchema
    ) implements ConfigurationSchema {

        public YamlSchema {
            if (dataType != ConfigurationDataType.YAML) {
                throw new IllegalArgumentException("Data type must be YAML");
            }
        }

        public static YamlSchema of(Map<String, Object> yamlSchema, boolean required) {
            return new YamlSchema(
                ConfigurationDataType.YAML, required, null,
                Set.of(), Map.of(), yamlSchema
            );
        }
    }
}
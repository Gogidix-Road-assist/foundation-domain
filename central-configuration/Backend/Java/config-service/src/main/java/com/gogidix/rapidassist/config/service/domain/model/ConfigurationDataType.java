package com.gogidix.rapidassist.config.service.domain.model;

public enum ConfigurationDataType {
    STRING("string"),
    NUMBER("number"),
    BOOLEAN("boolean"),
    ARRAY("array"),
    OBJECT("object"),
    JSON("json"),
    YAML("yaml"),
    INTEGER("integer"),
    DECIMAL("decimal"),
    DATE("date"),
    DATETIME("datetime"),
    DURATION("duration"),
    EMAIL("email"),
    URL("url"),
    ENUM("enum");

    private final String value;

    ConfigurationDataType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean isPrimitive() {
        return this == STRING || this == NUMBER || this == BOOLEAN || this == INTEGER || this == DECIMAL;
    }

    public boolean isComplex() {
        return this == ARRAY || this == OBJECT || this == JSON || this == YAML;
    }

    public boolean isTemporal() {
        return this == DATE || this == DATETIME || this == DURATION;
    }
}

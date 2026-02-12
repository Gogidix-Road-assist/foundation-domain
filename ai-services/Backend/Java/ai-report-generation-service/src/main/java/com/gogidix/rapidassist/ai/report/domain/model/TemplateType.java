package com.gogidix.rapidassist.ai.report.domain.model;

/**
 * Enum representing report template types.
 */
public enum TemplateType {
    FINANCIAL("Financial reports"),
    OPERATIONAL("Operational metrics"),
    SALES("Sales performance"),
    CUSTOMER("Customer analytics"),
    MARKETING("Marketing campaigns"),
    INVENTORY("Inventory reports"),
    HR("Human resources"),
    CUSTOM("Custom template");

    private final String description;

    TemplateType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

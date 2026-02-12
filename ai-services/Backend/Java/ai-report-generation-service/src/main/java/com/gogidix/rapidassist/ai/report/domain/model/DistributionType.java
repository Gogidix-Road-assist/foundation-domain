package com.gogidix.rapidassist.ai.report.domain.model;

/**
 * Enum representing report distribution types.
 */
public enum DistributionType {
    EMAIL("Email delivery"),
    WEBHOOK("Webhook notification"),
    SFTP("SFTP upload"),
    S3("Amazon S3 storage"),
    AZURE_BLOB("Azure Blob Storage"),
    GCS("Google Cloud Storage");

    private final String description;

    DistributionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

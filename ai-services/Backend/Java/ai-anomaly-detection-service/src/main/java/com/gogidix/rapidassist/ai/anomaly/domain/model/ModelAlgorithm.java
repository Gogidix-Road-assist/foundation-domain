package com.gogidix.rapidassist.ai.anomaly.domain.model;

/**
 * Enum representing model algorithms for anomaly detection.
 */
public enum ModelAlgorithm {

    ISOLATION_FOREST("Isolation Forest", "Isolation Forest algorithm"),
    ONE_CLASS_SVM("One-Class SVM", "One-Class Support Vector Machine"),
    LOCAL_OUTLIER_FACTOR("Local Outlier Factor", "LOF algorithm"),
    AUTOENCODER("Autoencoder", "Neural network autoencoder"),
    LSTM_AUTOENCODER("LSTM Autoencoder", "Long Short-Term Memory autoencoder"),
    DBSCAN("DBSCAN", "Density-Based Spatial Clustering"),
    K_MEANS("K-Means", "K-Means clustering"),
    ELLIPTIC_ENVELOPE("Elliptic Envelope", "Elliptic Envelope algorithm"),
    STATISTICAL("Statistical", "Statistical methods"),
    CUSTOM("Custom", "Custom algorithm");

    private final String code;
    private final String description;

    ModelAlgorithm(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}

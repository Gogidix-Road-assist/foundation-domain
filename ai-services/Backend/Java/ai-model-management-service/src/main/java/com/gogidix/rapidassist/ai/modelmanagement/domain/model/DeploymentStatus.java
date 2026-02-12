package com.gogidix.rapidassist.ai.modelmanagement.domain.model;

/**
 * Enumeration representing the deployment status of a model.
 */
public enum DeploymentStatus {
    PENDING,
    DEPLOYING,
    DEPLOYED,
    SCALING,
    UNDEPLOYING,
    FAILED,
    TERMINATED
}

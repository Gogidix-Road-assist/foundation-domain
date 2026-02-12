package com.gogidix.rapidassist.access.control.service.shared.exception;

/**
 * Exception: PermissionDeniedException
 *
 * Thrown when access is explicitly denied.
 */
public class PermissionDeniedException extends AccessControlException {

    private final String subjectId;
    private final String resource;
    private final String action;

    public PermissionDeniedException(String subjectId, String resource, String action, String reason) {
        super(String.format("Permission denied for subject=%s, resource=%s, action=%s: %s",
                subjectId, resource, action, reason), "PERMISSION_DENIED");
        this.subjectId = subjectId;
        this.resource = resource;
        this.action = action;
    }

    public String getSubjectId() { return subjectId; }
    public String getResource() { return resource; }
    public String getAction() { return action; }
}

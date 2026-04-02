package com.gogidix.rapidassist.mfa.service.infrastructure.provider.comprehensive;

import com.gogidix.rapidassist.mfa.service.domain.model.MfaVerificationResult;
import com.gogidix.rapidassist.mfa.service.infrastructure.provider.MfaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Backup code provider for one-time use recovery codes.
 */
public class BackupCodeProvider {

    private static final Logger logger = LoggerFactory.getLogger(BackupCodeProvider.class);

    private final SecureRandom secureRandom = new SecureRandom();

    // In production, backup codes would be stored encrypted in the database
    private final Map<String, BackupCodeSet> backupCodeSets = new HashMap<>();

    /**
     * Generate a set of backup codes
     */
    public String generateBackupCodes() {
        String setId = UUID.randomUUID().toString();
        int count = 10; // Default count
        int codeLength = 8; // Default length
        boolean alphanumeric = true; // Default to alphanumeric

        Set<String> codes = new HashSet<>();

        for (int i = 0; i < count; i++) {
            codes.add(generateBackupCode(codeLength, alphanumeric));
        }

        BackupCodeSet codeSet = new BackupCodeSet(codes, new HashSet<>());
        backupCodeSets.put(setId, codeSet);

        // Return codes as a formatted list
        return formatBackupCodes(new ArrayList<>(codes));
    }

    /**
     * Verify a backup code
     */
    public MfaVerificationResult verify(String backupCodeSetId, String code) {
        BackupCodeSet codeSet = backupCodeSets.get(backupCodeSetId);

        if (codeSet == null) {
            return new MfaVerificationResult(false, "Invalid backup code set");
        }

        if (codeSet.usedCodes.contains(code)) {
            return new MfaVerificationResult(false, "Backup code has already been used");
        }

        if (codeSet.availableCodes.contains(code)) {
            // Code is valid but not marked as used yet
            // Caller should call markCodeUsed after successful verification
            return new MfaVerificationResult(true, null);
        }

        return new MfaVerificationResult(false, "Invalid backup code");
    }

    /**
     * Mark a backup code as used
     */
    public void markCodeUsed(String backupCodeSetId, String code) {
        BackupCodeSet codeSet = backupCodeSets.get(backupCodeSetId);

        if (codeSet != null && codeSet.availableCodes.contains(code)) {
            codeSet.availableCodes.remove(code);
            codeSet.usedCodes.add(code);

            logger.info("Backup code marked as used in set: {}", backupCodeSetId);

            // Check if all codes have been used
            if (codeSet.availableCodes.isEmpty()) {
                logger.warn("All backup codes have been used for set: {}", backupCodeSetId);
            }
        }
    }

    private String generateBackupCode(int length, boolean alphanumeric) {
        StringBuilder code = new StringBuilder();

        if (alphanumeric) {
            String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            for (int i = 0; i < length; i++) {
                code.append(chars.charAt(secureRandom.nextInt(chars.length())));
            }
        } else {
            for (int i = 0; i < length; i++) {
                code.append(secureRandom.nextInt(10));
            }
        }

        return code.toString();
    }

    private String formatBackupCodes(List<String> codes) {
        StringBuilder formatted = new StringBuilder();
        formatted.append("Backup Codes (save these safely):\n");

        for (int i = 0; i < codes.size(); i++) {
            formatted.append(String.format("%d. %s\n", i + 1, codes.get(i)));
        }

        return formatted.toString();
    }

    private static class BackupCodeSet {
        final Set<String> availableCodes;
        final Set<String> usedCodes;

        BackupCodeSet(Set<String> availableCodes, Set<String> usedCodes) {
            this.availableCodes = availableCodes;
            this.usedCodes = usedCodes;
        }
    }
}
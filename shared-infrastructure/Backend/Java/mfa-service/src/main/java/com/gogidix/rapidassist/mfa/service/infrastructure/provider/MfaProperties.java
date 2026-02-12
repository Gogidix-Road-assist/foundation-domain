package com.gogidix.rapidassist.mfa.service.infrastructure.provider;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gogidix.mfa")
public class MfaProperties {

    private Provider provider = new Provider();
    private Totp totp = new Totp();
    private Otp otp = new Otp();
    private BackupCodes backupCodes = new BackupCodes();

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public Totp getTotp() {
        return totp;
    }

    public void setTotp(Totp totp) {
        this.totp = totp;
    }

    public Otp getOtp() {
        return otp;
    }

    public void setOtp(Otp otp) {
        this.otp = otp;
    }

    public BackupCodes getBackupCodes() {
        return backupCodes;
    }

    public void setBackupCodes(BackupCodes backupCodes) {
        this.backupCodes = backupCodes;
    }

    // Helper methods
    public int getOtpExpirationSeconds() {
        return otp.getExpirationSeconds();
    }

    public static class Provider {
        private String type = "comprehensive"; // noop, comprehensive

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public static class Totp {
        private String issuer = "Gogidix RapidAssist";
        private int timeStepSeconds = 30;
        private int codeDigits = 6;
        private String algorithm = "HmacSHA1";

        public String getIssuer() {
            return issuer;
        }

        public void setIssuer(String issuer) {
            this.issuer = issuer;
        }

        public int getTimeStepSeconds() {
            return timeStepSeconds;
        }

        public void setTimeStepSeconds(int timeStepSeconds) {
            this.timeStepSeconds = timeStepSeconds;
        }

        public int getCodeDigits() {
            return codeDigits;
        }

        public void setCodeDigits(int codeDigits) {
            this.codeDigits = codeDigits;
        }

        public String getAlgorithm() {
            return algorithm;
        }

        public void setAlgorithm(String algorithm) {
            this.algorithm = algorithm;
        }
    }

    public static class Otp {
        private int length = 6;
        private int expirationSeconds = 300; // 5 minutes
        private int maxAttempts = 3;
        private String smsTemplate = "Your verification code is: {code}";
        private String emailTemplate = "Your verification code is: {code}";
        private String smsProvider = "twilio"; // twilio, aws-sns, etc.
        private String emailProvider = "smtp"; // smtp, sendgrid, aws-ses, etc.

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public int getExpirationSeconds() {
            return expirationSeconds;
        }

        public void setExpirationSeconds(int expirationSeconds) {
            this.expirationSeconds = expirationSeconds;
        }

        public int getMaxAttempts() {
            return maxAttempts;
        }

        public void setMaxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
        }

        public String getSmsTemplate() {
            return smsTemplate;
        }

        public void setSmsTemplate(String smsTemplate) {
            this.smsTemplate = smsTemplate;
        }

        public String getEmailTemplate() {
            return emailTemplate;
        }

        public void setEmailTemplate(String emailTemplate) {
            this.emailTemplate = emailTemplate;
        }

        public String getSmsProvider() {
            return smsProvider;
        }

        public void setSmsProvider(String smsProvider) {
            this.smsProvider = smsProvider;
        }

        public String getEmailProvider() {
            return emailProvider;
        }

        public void setEmailProvider(String emailProvider) {
            this.emailProvider = emailProvider;
        }
    }

    public static class BackupCodes {
        private int count = 10;
        private int codeLength = 8;
        private boolean alphanumeric = true;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getCodeLength() {
            return codeLength;
        }

        public void setCodeLength(int codeLength) {
            this.codeLength = codeLength;
        }

        public boolean isAlphanumeric() {
            return alphanumeric;
        }

        public void setAlphanumeric(boolean alphanumeric) {
            this.alphanumeric = alphanumeric;
        }
    }
}
package com.gogidix.rapidassist.ai.speech.recognition.domain.model;

/**
 * Enum representing supported language codes for speech recognition.
 */
public enum LanguageCode {

    // English
    en_US("English (United States)"),
    en_GB("English (United Kingdom)"),
    en_AU("English (Australia)"),
    en_CA("English (Canada)"),

    // Spanish
    es_ES("Spanish (Spain)"),
    es_MX("Spanish (Mexico)"),
    es_US("Spanish (United States)"),

    // French
    fr_FR("French (France)"),
    fr_CA("French (Canada)"),

    // German
    de_DE("German (Germany)"),

    // Italian
    it_IT("Italian (Italy)"),

    // Portuguese
    pt_BR("Portuguese (Brazil)"),
    pt_PT("Portuguese (Portugal)"),

    // Chinese
    zh_CN("Chinese (Mandarin, Simplified)"),
    zh_TW("Chinese (Mandarin, Traditional)"),

    // Japanese
    ja_JP("Japanese"),

    // Korean
    ko_KR("Korean"),

    // Arabic
    ar_SA("Arabic (Saudi Arabia)"),

    // Hindi
    hi_IN("Hindi (India)");

    private final String displayName;

    LanguageCode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

package com.gogidix.rapidassist.identity.access.service.adapters.in.web;

import org.springframework.util.StringUtils;

final class HeaderSupport {

    private HeaderSupport() {
    }

    static String pick(String primary, String fallback) {
        if (StringUtils.hasText(primary)) {
            return primary;
        }
        if (StringUtils.hasText(fallback)) {
            return fallback;
        }
        return null;
    }
}

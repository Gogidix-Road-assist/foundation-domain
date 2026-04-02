package com.gogidix.rapidassist.api.gateway.infrastructure.security;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.StringUtils;

import java.util.Collection;

public class AudienceValidator implements OAuth2TokenValidator<Jwt> {

    private final String expectedAudience;

    public AudienceValidator(String expectedAudience) {
        this.expectedAudience = expectedAudience;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        if (!StringUtils.hasText(expectedAudience)) {
            return OAuth2TokenValidatorResult.success();
        }

        Object aud = token.getClaims().get("aud");
        if (aud instanceof String s) {
            return expectedAudience.equals(s)
                    ? OAuth2TokenValidatorResult.success()
                    : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Invalid audience", null));
        }

        if (aud instanceof Collection<?> c) {
            boolean ok = c.stream().map(String::valueOf).anyMatch(expectedAudience::equals);
            return ok
                    ? OAuth2TokenValidatorResult.success()
                    : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Invalid audience", null));
        }

        return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Invalid audience", null));
    }
}

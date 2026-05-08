package com.gogidix.rapidassist.rate.limiting.service.adapters.in.web;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record RateLimitCheckRequest(
        @NotBlank String key,
        @Min(1) long limit,
        @Min(1) long windowSeconds
) {
}

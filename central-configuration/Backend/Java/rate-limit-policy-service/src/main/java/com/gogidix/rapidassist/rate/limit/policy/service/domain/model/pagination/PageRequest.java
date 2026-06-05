package com.gogidix.rapidassist.rate.limit.policy.service.domain.model.pagination;

public record PageRequest(int page, int size) {
    public static PageRequest of(int page, int size) {
        return new PageRequest(page, size);
    }
}

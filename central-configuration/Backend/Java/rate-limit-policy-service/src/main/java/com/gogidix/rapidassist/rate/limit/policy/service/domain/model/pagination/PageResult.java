package com.gogidix.rapidassist.rate.limit.policy.service.domain.model.pagination;

import java.util.List;

public record PageResult<T>(List<T> content, long totalElements, int totalPages, int page, int size) {
    public static <T> PageResult<T> of(List<T> content, long totalElements, int totalPages, int page, int size) {
        return new PageResult<>(content, totalElements, totalPages, page, size);
    }
}

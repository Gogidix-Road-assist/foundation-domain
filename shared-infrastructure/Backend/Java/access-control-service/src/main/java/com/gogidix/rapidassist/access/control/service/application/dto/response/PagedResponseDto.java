package com.gogidix.rapidassist.access.control.service.application.dto.response;

import java.util.List;

/**
 * DTO: PagedResponseDto
 *
 * Generic response DTO for paginated results.
 */
public record PagedResponseDto<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        boolean isEmpty
) {
    public static <T> PagedResponseDto<T> of(List<T> content, int page, int size, long total) {
        int totalPages = (int) Math.ceil((double) total / size);
        return new PagedResponseDto<>(
                content,
                page,
                size,
                total,
                totalPages,
                page == 0,
                page >= totalPages - 1,
                content.isEmpty()
        );
    }

    @SuppressWarnings("unchecked")
    public static <T> PagedResponseDto<T> empty() {
        return new PagedResponseDto<>(
                List.of(),
                0,
                0,
                0,
                0,
                true,
                true,
                true
        );
    }
}

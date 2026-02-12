package com.gogidix.rapidassist.feature.flags.service.application.dto.response;

import java.util.List;

/**
 * Generic paged response DTO.
 *
 * @param <T> the type of data in the page
 */
public record PagedResponseDto<T>(
    List<T> data,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean hasNext,
    boolean hasPrevious
) {
    /**
     * Creates a paged response from data and pagination info.
     *
     * @param data          the list of items
     * @param page          the current page number
     * @param size          the page size
     * @param totalElements the total number of elements
     * @return a new PagedResponseDto
     */
    public static <T> PagedResponseDto<T> of(List<T> data, int page, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        boolean hasNext = page < totalPages - 1;
        boolean hasPrevious = page > 0;

        return new PagedResponseDto<>(
            data,
            page,
            size,
            totalElements,
            totalPages,
            hasNext,
            hasPrevious
        );
    }

    /**
     * Creates an empty paged response.
     *
     * @param page the current page number
     * @param size the page size
     * @return an empty PagedResponseDto
     */
    public static <T> PagedResponseDto<T> empty(int page, int size) {
        return new PagedResponseDto<>(
            List.of(),
            page,
            size,
            0,
            0,
            false,
            false
        );
    }
}

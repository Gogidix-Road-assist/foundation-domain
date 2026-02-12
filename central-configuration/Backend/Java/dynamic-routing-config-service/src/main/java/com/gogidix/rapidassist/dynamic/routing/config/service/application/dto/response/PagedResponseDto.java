package com.gogidix.rapidassist.dynamic.routing.config.service.application.dto.response;

import java.util.List;

/**
 * Generic paged response DTO for API responses.
 *
 * @param <T> the type of content in the page
 */
public record PagedResponseDto<T>(
    List<T> content,
    int pageNumber,
    int pageSize,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last,
    boolean empty
) {
    /**
     * Creates an empty paged response.
     *
     * @param page the page number
     * @param size the page size
     * @return an empty paged response
     */
    public static <T> PagedResponseDto<T> empty(int page, int size) {
        return new PagedResponseDto<>(
            List.of(),
            page,
            size,
            0,
            0,
            true,
            true,
            true
        );
    }

    /**
     * Creates a paged response from content and total count.
     *
     * @param content      the content list
     * @param pageNumber   the page number
     * @param pageSize     the page size
     * @param totalElements the total number of elements
     * @return a paged response
     */
    public static <T> PagedResponseDto<T> of(
            List<T> content,
            int pageNumber,
            int pageSize,
            long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);
        return new PagedResponseDto<>(
            content,
            pageNumber,
            pageSize,
            totalElements,
            totalPages,
            pageNumber == 0,
            pageNumber >= totalPages - 1,
            content.isEmpty()
        );
    }
}

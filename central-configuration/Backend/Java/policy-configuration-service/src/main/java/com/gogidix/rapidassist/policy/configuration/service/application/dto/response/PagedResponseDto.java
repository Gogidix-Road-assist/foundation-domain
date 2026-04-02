package com.gogidix.rapidassist.policy.configuration.service.application.dto.response;

import java.util.List;

/**
 * Generic paged response DTO.
 *
 * <p>This DTO wraps a list of items with pagination metadata.
 *
 * @param <T> the type of items in the page
 */
public record PagedResponseDto<T>(
    List<T> content,
    int currentPage,
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
     * @param <T> the type of items
     * @return an empty paged response
     */
    public static <T> PagedResponseDto<T> emptyPage() {
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

    /**
     * Creates a paged response from content and metadata.
     *
     * @param content       the list of items
     * @param page          the current page number
     * @param size          the page size
     * @param totalElements the total number of elements
     * @param <T>           the type of items
     * @return a paged response
     */
    public static <T> PagedResponseDto<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return new PagedResponseDto<>(
            content,
            page,
            size,
            totalElements,
            totalPages,
            page == 0,
            page >= totalPages - 1,
            content.isEmpty()
        );
    }
}

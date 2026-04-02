package com.gogidix.rapidassist.release.rollout.config.service.application.dto.response;

import java.util.List;

/**
 * Generic paged response DTO for list operations.
 *
 * @param <T> the type of data in the page
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
     * @param pageNumber the page number
     * @param pageSize   the page size
     * @return an empty paged response
     */
    public static <T> PagedResponseDto<T> empty(int pageNumber, int pageSize) {
        return new PagedResponseDto<>(
            List.of(),
            pageNumber,
            pageSize,
            0,
            0,
            true,
            true,
            true
        );
    }

    /**
     * Creates a paged response from a list of content.
     *
     * @param content       the list of items
     * @param pageNumber    the page number
     * @param pageSize      the page size
     * @param totalElements the total number of elements
     * @return a new paged response
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
            totalPages == 0 ? 1 : totalPages,
            pageNumber == 0,
            pageNumber >= totalPages - 1,
            content.isEmpty()
        );
    }
}

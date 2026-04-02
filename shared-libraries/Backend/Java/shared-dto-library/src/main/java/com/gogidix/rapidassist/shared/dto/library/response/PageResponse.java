package com.gogidix.rapidassist.shared.dto.library.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Standard Page Response for paginated data.
 * Provides consistent pagination structure across all services.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private int numberOfElements;
    private boolean first;
    private boolean last;
    private boolean hasNext;
    private boolean hasPrevious;

    /**
     * Creates an empty page response.
     */
    public static <T> PageResponse<T> empty() {
        return PageResponse.<T>builder()
                .content(List.of())
                .pageNumber(0)
                .pageSize(0)
                .totalElements(0)
                .totalPages(0)
                .numberOfElements(0)
                .first(true)
                .last(true)
                .hasNext(false)
                .hasPrevious(false)
                .build();
    }
}

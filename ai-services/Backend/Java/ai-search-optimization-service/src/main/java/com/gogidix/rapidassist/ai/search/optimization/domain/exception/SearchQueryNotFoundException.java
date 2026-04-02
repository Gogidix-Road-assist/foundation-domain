package com.gogidix.rapidassist.ai.search.optimization.domain.exception;

import java.util.UUID;

/**
 * Exception thrown when a search query is not found.
 */
public class SearchQueryNotFoundException extends SearchOptimizationException {

    public SearchQueryNotFoundException(UUID id) {
        super("Search query not found with ID: " + id);
    }

    public SearchQueryNotFoundException(String message) {
        super(message);
    }
}

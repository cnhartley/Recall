package com.recall.retry.exceptions;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import lombok.Getter;

/**
 * 
 */
public class ExhuastedRetriesException extends RuntimeException {

    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = 919151279241090669L;

    @Nonnull
    @Getter
    private List<RetriableException> retryExceptions;

    /**
     * 
     * @param retryExceptions
     */
    public ExhuastedRetriesException(@Nonnull final List<RetriableException> retryExceptions) {
        super(retryExceptions.stream().findFirst().orElse(null));
        this.retryExceptions = new LinkedList<>(retryExceptions);
    }

    /**
     * 
     * @param message
     * @param retryExceptions
     */
    public ExhuastedRetriesException(
            @Nullable final String message,
            @Nonnull final List<RetriableException> retryExceptions) {
        super(message, retryExceptions.stream().findFirst().orElse(null));
        this.retryExceptions = new LinkedList<>(retryExceptions);
    }

}

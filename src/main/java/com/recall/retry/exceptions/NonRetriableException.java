/**
 * 
 */
package com.recall.retry.exceptions;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import lombok.NoArgsConstructor;

/**
 * @author Chris
 *
 */
@ThreadSafe
@NoArgsConstructor
public class NonRetriableException extends RuntimeException {

    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = 7549194133943761269L;

    /**
     * @param message
     */
    public NonRetriableException(@Nullable final String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public NonRetriableException(@Nullable final Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public NonRetriableException(@Nullable final String message, @Nullable final Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

}

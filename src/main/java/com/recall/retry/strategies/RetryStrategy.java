package com.recall.retry.strategies;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableCollection;

public interface RetryStrategy {

    /**
     * Returns the length of time to wait, in milliseconds, based on the retry attempt.
     * 
     * @param retryAttempt the current retry attempt number.
     * @return the length of time to wait, in milliseconds.
     */
    long getWaitTime(int retryAttempt);

    /**
     * Returns the maximum number of retry attempts to make.
     * 
     * @return the maximum number of retry attempts to make.
     */
    int getMaximumRetries();

    /**
     * Returns an immutable collection of the retriable exceptions that this strategy allows.
     * 
     * @return an immutable collection of the retriable exceptions that this strategy allows.
     */
    @Nonnull
    ImmutableCollection<Class<? extends Exception>> getRetriableExceptions();

    /**
     * Checks if the specified exception is an instance of one of this retry strategies retriable exceptions.
     * 
     * @param exception the exception to be checked.
     * @return returns <tt>true</tt> if the exception is an instance of any of this strategies retriable exceptions;
     *         otherwise, returns <tt>false</tt>.
     */
    boolean isRetriableException(@Nullable final Exception exception);

}
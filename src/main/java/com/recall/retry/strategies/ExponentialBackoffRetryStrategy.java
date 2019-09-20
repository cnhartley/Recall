package com.recall.retry.strategies;

import java.time.Instant;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.ImmutableSet;
import com.recall.retry.RetriableCallState;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

/**
 * 
 */
@Value
@Builder
@ThreadSafe
@AllArgsConstructor
@ParametersAreNonnullByDefault
public class ExponentialBackoffRetryStrategy implements RetryStrategy {

    @Nonnull
    private final long maxRetryLapseTime;

    @Nonnull
    private final long initialDelay;

    @Nonnull
    private final int maximumRetries;

    @Nonnull
    @Singular
    private final ImmutableSet<Class<? extends Exception>> retriableExceptions;

    @Override
    public long getWaitTime(@Nonnull final RetriableCallState<?> callState) {
        if (callState.getRetryCount() > this.maximumRetries
                || Instant.now()
                        .isBefore(callState.getStartTime().plusMillis(this.maxRetryLapseTime))) {
            return EXHAUSTED;
        } else {
            return (long) (this.initialDelay * Math.pow(2d, (double) callState.getRetryCount()));
        }
    }

    @Override
    public boolean isRetriableException(Exception exception) {
        return !this.retriableExceptions.isEmpty()
                && this.retriableExceptions.stream().anyMatch(ex -> ex.isInstance(exception));
    }

}

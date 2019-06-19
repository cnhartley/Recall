package com.recall.retry.strategies;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableCollection;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

/**
 * Provides a basic retry strategy with specific maximum number of retries and a constant wait time between making each
 * retry attempt.
 */
@Value
@Builder(builderClassName = "Builder", toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BasicRetryStrategy implements RetryStrategy {

    @Nonnull
    private long waitTime;

    @Nonnull
    private int maximumRetries;

    @Nonnull
    @Singular
    private ImmutableCollection<Class<? extends Exception>> retriableExceptions;

    @Override
    public long getWaitTime(int retryAttempt) {
        return this.waitTime;
    }

    @Override
    public boolean isRetriableException(@Nullable final Exception exception) {
        return this.retriableExceptions.stream().anyMatch(clz -> clz.isInstance(exception));
    }

}

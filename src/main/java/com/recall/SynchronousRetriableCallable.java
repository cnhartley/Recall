package com.recall;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.recall.retry.RetriableCallable;
import com.recall.retry.exceptions.ExhuastedRetriesException;
import com.recall.retry.exceptions.NonRetriableException;
import com.recall.retry.strategies.RetryStrategy;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

/**
 * Provides a synchronous retrying {@link Callable} based on a specified {@link RetryStrategy}.
 * <p>
 * Note: this does use a single thread executor to perform the call in-order to monitor the length of time the call
 * takes and be able to interrupt if necessary.
 * 
 * <pre>
 * final RetryStrategy retryStrategy = ...
 * 
 * final RetriableCallable<ReturnType> caller = SynchronousRetriableCallable.builder()
 *         .retryStrategy(retryStrategy)
 *         .callable(() -> doSomething())
 *         .build();
 * try {
 *     final ReturnType answer = caller.call();
 * }
 * catch (final ExhuastedRetriesException ere) {
 *     // handle when all attempts have been tried but all failed...
 * }
 * catch (final NonRetriableException nre) {
 *     // handle when a non-retriable exception was thrown...
 * }
 * </pre>
 * 
 * @param <V> the result type of method {@link #call()}.
 */
@Value
@Builder(builderClassName = "Builder", toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
@ParametersAreNonnullByDefault
public class SynchronousRetriableCallable<V> extends RetriableCallable<V> {

    @Nonnull
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Nonnull
    @Getter(AccessLevel.PROTECTED)
    private final RetryStrategy retryStrategy;

    @Nonnull
    @Getter(AccessLevel.PROTECTED)
    private final Callable<V> callable;

    @Override
    public V call() throws ExhuastedRetriesException, NonRetriableException {
        return makeCall(() -> synchronousCall());
    }

    private V synchronousCall() throws Exception {
        try {
            return this.executor.submit(this.callable).get(30, TimeUnit.SECONDS);
        } catch (final ExecutionException wrappedEx) {
            throw (Exception) wrappedEx.getCause();
        }
    }

}

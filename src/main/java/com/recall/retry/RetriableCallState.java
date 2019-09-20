package com.recall.retry;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.ThreadSafe;

import com.recall.retry.exceptions.RetriableException;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Provides a state object for making retriable calls. This helps to keep track of the number of retries already
 * processed along with what retriable exception have been thrown on previous attempts.
 * <p>
 * Note: This should not be used outside of the library as its primary use is internal to the various retry callers.
 * 
 * @param <V> the result type of method {@link #call()}.
 */
@ThreadSafe
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@ParametersAreNonnullByDefault
public class RetriableCallState<V> {

    @Nonnull
    private final AtomicInteger retryCount = new AtomicInteger();

    @Nonnull
    private final ConcurrentLinkedQueue<RetriableException> caughtRetriableExceptions = new ConcurrentLinkedQueue<>();

    @Nonnull
    @Getter
    private final Callable<V> callable;

    @Getter
    private final Instant startTime = Instant.now();

    /**
     * Returns the current number of retries this call state has attempted.
     * 
     * @return the current number of retries this call state has attempted.
     */
    public int getRetryCount() {
        return this.retryCount.get();
    }

    /**
     * Returns the current number of retries this call state has attempted and then increment the count by <tt>1</tt>..
     * 
     * @return the current number of retries this call state has attempted.
     */
    public int getRetryCountAndIncrement() {
        return this.retryCount.getAndIncrement();
    }

    /**
     * Appends the specified {@link RetriableException} to the list of caught exception this caller state has seen.
     * 
     * @param rex the {@link RetriableException} to append to this state.
     */
    public void addCaughtRetriableException(@Nonnull final RetriableException rex) {
        this.caughtRetriableExceptions.offer(rex);
    }

    /**
     * Returns a copy of the list of {@link RetriableException}s this caller state has seen. If no exceptions have been
     * seen, this returns an empty list.
     * 
     * @return a copy of the list of {@link RetriableException}s this caller state has seen.
     */
    @Nonnull
    public List<RetriableException> getCaughtRetriableExceptions() {
        return this.caughtRetriableExceptions.stream().collect(Collectors.toList());
    }

}

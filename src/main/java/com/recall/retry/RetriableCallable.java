package com.recall.retry;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.recall.retry.exceptions.ExhuastedRetriesException;
import com.recall.retry.exceptions.NonRetriableException;
import com.recall.retry.exceptions.RetriableException;
import com.recall.retry.strategies.RetryStrategy;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;

/**
 * Provides a {@link Callable} that has a specific strategy for re-attempting calls when certain exceptions are thrown.
 * 
 * @param <V> the result type of method {@link #call()}.
 */
@Log
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ParametersAreNonnullByDefault
public abstract class RetriableCallable<V> implements Callable<V> {

    /**
     * Returns the retry strategy to use with this instance.
     * 
     * @return the retry strategy to use with this instance.
     */
    @Nonnull
    protected abstract RetryStrategy getRetryStrategy();

    /**
     * Returns the {@link Callable} to be executed with retries by this instance.
     * 
     * @return the {@code Callable} to be executed with retries by this instance.
     */
    @Nonnull
    protected abstract Callable<V> getCallable();

    /**
     * Computes a result by making a synchronous call to this instances' <tt>Callable</tt>, retrying if the specified
     * retry strategy permits.
     * 
     * @return the computed result of making the call.
     * 
     * @throws ExhuastedRetriesException if the number of retries has been exhausted. This exception will contain a list
     *             of all retriable exceptions that were caught while attempting to make the call.
     * @throws NonRetriableException if any non-retriable exception, those not specified in the retry strategy, were
     *             thrown while making the call.
     * 
     * @see SynchronousCallable
     */
    @Override
    public abstract V call() throws ExhuastedRetriesException, NonRetriableException;

    protected V makeCall(@Nonnull final Callable<V> caller) throws ExhuastedRetriesException, NonRetriableException {
        final RetriableCallState<V> callState = new RetriableCallState<>(caller);
        return makeCall(callState);
    }

    private V makeCall(@Nonnull final RetriableCallState<V> callState)
            throws ExhuastedRetriesException, NonRetriableException {
        try {
            if (callState.getRetryCount() < getRetryStrategy().getMaximumRetries()) {
                callState.getRetryCountAndIncrement();
                return callState.getCallable().call();
            }
        } catch (final Exception ex) {
            log.info("call() threw exception: " + ex.getClass());
            if (NonRetriableException.class.isInstance(ex)) {
                throw NonRetriableException.class.cast(ex);
            } else if (getRetryStrategy().isRetriableException(ex)) {
                handleRetriableException(callState, ex);
                return makeCall(callState);
            } else {
                log.warning("Unable to retry callable! Exception: " + ex.getMessage());
                throw new NonRetriableException("Unable to retry callable!", ex);
            }
        }
        throw handleExhuastedRetriesException(callState);
    }

    private void handleRetriableException(@Nonnull final RetriableCallState<?> callState, @Nonnull final Exception ex) {
        final RetriableException wrappedEx = new RetriableException(ex, callState.getRetryCount());
        callState.addCaughtRetriableException(wrappedEx);
        log.warning("Retriable exception caught: " + wrappedEx.getMessage());
        pause(callState);
    }

    private ExhuastedRetriesException handleExhuastedRetriesException(@Nonnull final RetriableCallState<?> callState) {
        return new ExhuastedRetriesException(
                "Maximum number of retries reached (max=" + getRetryStrategy().getMaximumRetries() + ")",
                callState.getCaughtRetriableExceptions());
    }

    private void pause(@Nonnull final RetriableCallState<?> callState) {
        long waitTime = getRetryStrategy().getWaitTime(callState);
        try {
            if (waitTime > 0) {
                Thread.sleep(waitTime);
            }
        } catch (final InterruptedException ex) {
            log.warning("Retry callable interrupted due to: " + ex);
        }
    }

}

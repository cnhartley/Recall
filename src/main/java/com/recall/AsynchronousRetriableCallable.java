package com.recall;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

import lombok.Value;

/**
 * 
 * 
 * @param <V>
 */
@Value
public class AsynchronousRetriableCallable<V> implements Callable<Boolean> {

    @Nonnull
    private final ExecutorService executor = Executors.newFixedThreadPool(5);

    @Nonnull
    private final Callable<V> callable;

    @Nonnull
    private final Consumer<V> onSuccess;

    @Nonnull
    private final Consumer<? super Exception> onFailure;

    @Override
    public Boolean call() throws Exception {
        try {
            this.executor.submit(() -> this.makeCall());
            return true;
        } catch (final RejectedExecutionException rejected) {
            return false;
        }
    }

    private void makeCall() {
        try {
            final V answer = this.callable.call();
            this.onSuccess.accept(answer);
        } catch (final Exception ex) {
            this.onFailure.accept(ex);
        }
    }
}

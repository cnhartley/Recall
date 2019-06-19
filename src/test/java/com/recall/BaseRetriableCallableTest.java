package com.recall;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import javax.annotation.Nonnull;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.OngoingStubbing;

import com.recall.retry.RetriableCallable;
import com.recall.retry.exceptions.ExhuastedRetriesException;
import com.recall.retry.exceptions.NonRetriableException;
import com.recall.retry.strategies.RetryStrategy;

import lombok.Getter;

public abstract class BaseRetriableCallableTest {

    @Getter
    @Mock
    private Callable<Object> callable;

    public abstract RetryStrategy getRetryStrategy();

    public abstract RetriableCallable<Object> getRetriableCallable();

    public abstract Object getTestValue();

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSuccessfulCall() throws Exception {
        when(getCallable().call())
                .thenReturn(getTestValue());
        assertEquals(getTestValue(), getRetriableCallable().call());
        verify(getCallable(), times(1)).call();
    }

    private static OngoingStubbing<Object> repeatThenThrows(
            @Nonnull final OngoingStubbing<Object> stub,
            @Nonnull final Class<? extends Throwable> clz,
            int repeat) {
        return repeat > 0
                ? repeatThenThrows(stub.thenThrow(clz), clz, repeat - 1)
                : stub;
    }

    @Test
    public void testSuccessfulCallAfterRetries() throws Exception {
        int maxRetries = getRetryStrategy().getMaximumRetries();
        repeatThenThrows(when(getCallable().call()), TimeoutException.class, maxRetries - 1)
                .thenReturn(getTestValue());
        assertEquals(getTestValue(), getRetriableCallable().call());
        verify(getCallable(), times(maxRetries)).call();
    }

    @Test
    public void testFailedCallAfterRetriesWrappedException() throws Exception {
        int maxRetries = getRetryStrategy().getMaximumRetries();
        repeatThenThrows(when(getCallable().call()), TimeoutException.class, maxRetries - 1)
                .thenThrow(BedException.class);
        try {
            getRetriableCallable().call();
        } catch (final NonRetriableException nre) {
            verify(getCallable(), times(maxRetries)).call();
            assertTrue(BedException.class.isInstance(nre.getCause()));
        }
    }

    @Test
    public void testFailedCallAfterRetries() throws Exception {
        int maxRetries = getRetryStrategy().getMaximumRetries();
        repeatThenThrows(when(getCallable().call()), TimeoutException.class, maxRetries - 1)
                .thenThrow(TestNonRetriableException.class);
        try {
            getRetriableCallable().call();
        } catch (final NonRetriableException nre) {
            verify(getCallable(), times(maxRetries)).call();
            assertTrue(TestNonRetriableException.class.isInstance(nre));
        }
    }

    @Test
    public void testExhuastedRetries() throws Exception {
        when(getCallable().call())
                .thenThrow(TimeoutException.class);
        try {
            getRetriableCallable().call();
        } catch (final ExhuastedRetriesException ere) {
            verify(getCallable(), times(getRetryStrategy().getMaximumRetries())).call();
            assertEquals(getRetryStrategy().getMaximumRetries(), ere.getRetryExceptions().size());
        }
    }

    static class BedException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    static class TestNonRetriableException extends NonRetriableException {
        private static final long serialVersionUID = 1L;
    }

}

package com.recall;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.recall.retry.exceptions.ExhuastedRetriesException;
import com.recall.retry.exceptions.NonRetriableException;
import com.recall.retry.strategies.BasicRetryStrategy;
import com.recall.retry.strategies.RetryStrategy;

public class SynchronousRetriableCallableTest {

    private static final Object TEST_VALUE = "TestStringObject";
    private static int MAXIMUM_RETRIES = 5;
    private static long WAIT_TIME_BETWEEN_RETRIES = 100l;
    private static final RetryStrategy TEST_RETRY_STRATEGY = BasicRetryStrategy.builder()
            .maximumRetries(MAXIMUM_RETRIES)
            .waitTime(WAIT_TIME_BETWEEN_RETRIES)
            .retriableException(TimeoutException.class)
            .build();

    @Mock
    private Callable<Object> callable;

    private SynchronousRetriableCallable<Object> syncCaller;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        this.syncCaller = SynchronousRetriableCallable.builder()
                .retryStrategy(TEST_RETRY_STRATEGY)
                .callable(this.callable)
                .build();
    }

    @Test
    public void testSuccessfulCall() throws Exception {
        Mockito.when(this.callable.call())
                .thenReturn(TEST_VALUE);
        Assert.assertEquals(TEST_VALUE, this.syncCaller.call());
        Mockito.verify(this.callable, Mockito.times(1)).call();
    }

    @Test
    public void testSuccessfulCallAfterRetries() throws Exception {
        Mockito.when(this.callable.call())
                .thenThrow(TimeoutException.class)
                .thenThrow(TimeoutException.class)
                .thenThrow(TimeoutException.class)
                .thenReturn(TEST_VALUE);
        Assert.assertEquals(TEST_VALUE, this.syncCaller.call());
        Mockito.verify(this.callable, Mockito.times(4)).call();
    }

    @Test
    public void testFailedCallAfterRetriesWrappedException() throws Exception {
        Mockito.when(this.callable.call())
                .thenThrow(TimeoutException.class)
                .thenThrow(TimeoutException.class)
                .thenThrow(TimeoutException.class)
                .thenThrow(BedException.class);
        try {
            this.syncCaller.call();
        } catch (final NonRetriableException nre) {
            Mockito.verify(this.callable, Mockito.times(4)).call();
            Assert.assertTrue(BedException.class.isInstance(nre.getCause()));
        }
    }

    @Test
    public void testFailedCallAfterRetries() throws Exception {
        Mockito.when(this.callable.call())
                .thenThrow(TimeoutException.class)
                .thenThrow(TimeoutException.class)
                .thenThrow(TimeoutException.class)
                .thenThrow(TestNonRetriableException.class);
        try {
            this.syncCaller.call();
        } catch (final NonRetriableException nre) {
            Mockito.verify(this.callable, Mockito.times(4)).call();
            Assert.assertTrue(TestNonRetriableException.class.isInstance(nre));
        }
    }

    @Test
    public void testExhuastedRetries() throws Exception {
        Mockito.when(this.callable.call())
                .thenThrow(TimeoutException.class);
        try {
            this.syncCaller.call();
        } catch (final ExhuastedRetriesException ere) {
            Mockito.verify(this.callable, Mockito.times(MAXIMUM_RETRIES)).call();
            Assert.assertEquals(MAXIMUM_RETRIES, ere.getRetryExceptions().size());
        }
    }

    private class BedException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    private class TestNonRetriableException extends NonRetriableException {
        private static final long serialVersionUID = 1L;
    }

}

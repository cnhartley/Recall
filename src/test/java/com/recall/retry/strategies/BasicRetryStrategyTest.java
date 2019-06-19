package com.recall.retry.strategies;

import java.util.concurrent.TimeoutException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class BasicRetryStrategyTest {

    private static int MAXIMUM_RETRIES = 5;
    private static long WAIT_TIME_BETWEEN_RETRIES = 100l;

    private BasicRetryStrategy retryStrategy;

    @Before
    public void before() {
        this.retryStrategy = BasicRetryStrategy.builder()
                .maximumRetries(MAXIMUM_RETRIES)
                .waitTime(WAIT_TIME_BETWEEN_RETRIES)
                .retriableException(TimeoutException.class)
                .build();
    }

    @Test
    public void verifyMaximumRetries() {
        Assert.assertEquals(MAXIMUM_RETRIES, this.retryStrategy.getMaximumRetries());
    }

    @Test
    public void verifyWaitTime() {
        Assert.assertEquals(WAIT_TIME_BETWEEN_RETRIES, this.retryStrategy.getWaitTime());
        Assert.assertEquals(WAIT_TIME_BETWEEN_RETRIES, this.retryStrategy.getWaitTime(0));
        Assert.assertEquals(WAIT_TIME_BETWEEN_RETRIES, this.retryStrategy.getWaitTime(MAXIMUM_RETRIES + 1));
    }

    @Test
    public void verifyRetriableExceptions() {
        Assert.assertEquals(ImmutableList.of(TimeoutException.class), this.retryStrategy.getRetriableExceptions());
    }
    
    @Test
    public void testNoRetriableExceptionsSpecified() {
        final BasicRetryStrategy retryStrategy = BasicRetryStrategy.builder()
                .maximumRetries(MAXIMUM_RETRIES)
                .waitTime(WAIT_TIME_BETWEEN_RETRIES)
                .build();
        Assert.assertFalse(retryStrategy.isRetriableException(new TestTimeoutException()));
    }

    @Test
    public void testIsRetriableException() {
        Assert.assertTrue(this.retryStrategy.isRetriableException(new TestTimeoutException()));
        Assert.assertFalse(this.retryStrategy.isRetriableException(new NullPointerException()));
    }

    private class TestTimeoutException extends TimeoutException {
        private static final long serialVersionUID = -5430717564621776255L;
    }
}

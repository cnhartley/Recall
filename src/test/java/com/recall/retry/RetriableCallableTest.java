package com.recall.retry;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.mockito.MockitoAnnotations;

import com.recall.BaseRetriableCallableTest;
import com.recall.retry.exceptions.ExhuastedRetriesException;
import com.recall.retry.exceptions.NonRetriableException;
import com.recall.retry.strategies.BasicRetryStrategy;
import com.recall.retry.strategies.RetryStrategy;

import lombok.Getter;

public class RetriableCallableTest extends BaseRetriableCallableTest {

    private static int MAXIMUM_RETRIES = 5;
    private static long WAIT_TIME_BETWEEN_RETRIES = 100l;

    @Getter
    private final RetryStrategy retryStrategy = BasicRetryStrategy.builder()
            .maximumRetries(MAXIMUM_RETRIES)
            .waitTime(WAIT_TIME_BETWEEN_RETRIES)
            .retriableException(TimeoutException.class)
            .build();

    @Getter
    private final Object testValue = "TestStringObject";

    @Getter
    private RetriableCallable<Object> retriableCallable;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        this.retriableCallable = new RetriableCallable<Object>() {

            @Override
            protected RetryStrategy getRetryStrategy() {
                return RetriableCallableTest.this.getRetryStrategy();
            }

            @Override
            protected Callable<Object> getCallable() {
                return RetriableCallableTest.this.getCallable();
            }

            @Override
            public Object call() throws ExhuastedRetriesException, NonRetriableException {
                return makeCall(getCallable());
            }

        };
    }

}

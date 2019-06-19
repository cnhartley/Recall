package com.recall.retry;

import java.util.List;
import java.util.concurrent.Callable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.recall.retry.exceptions.RetriableException;

public class RetriableCallStateTest {

    @Mock
    private Callable<Object> mockCallable;

    private RetriableCallState<?> callState;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        this.callState = new RetriableCallState<>(this.mockCallable);
    }

    @Test
    public void testCall() throws Exception {
        final Object testValue = new Object();
        Mockito.when(this.mockCallable.call()).thenReturn(testValue);

        Assert.assertEquals(testValue, this.callState.getCallable().call());
    }

    @Test(expected = RuntimeException.class)
    public void testCallException() throws Exception {
        Mockito.when(this.mockCallable.call()).thenThrow(new RuntimeException());
        this.callState.getCallable().call();
    }

    @Test
    public void testCaughtExceptions() {
        int maxCaughtException = 5;
        while (this.callState.getRetryCount() < maxCaughtException) {
            int count = this.callState.getRetryCountAndIncrement();
            this.callState.addCaughtRetriableException(new RetriableException("Attempt " + count, count));
        }
        Assert.assertEquals(maxCaughtException, this.callState.getRetryCount());
        final List<RetriableException> caught = this.callState.getCaughtRetriableExceptions();
        for (int ndx = 0; ndx < maxCaughtException; ndx++) {
            Assert.assertEquals(ndx, caught.get(ndx).getRetryCount());
        }

    }

}

/**
 * 
 */
package com.recall.retry.exceptions;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 
 */
@ThreadSafe
@NoArgsConstructor
public class RetriableException extends RuntimeException {

    /** use serialVersionUID from JDK 1.0.2 for interoperability */
    private static final long serialVersionUID = 5270153310599523899L;

    /**
     * Count of the number of retries to this point. Thus, for the first time this exception is thrown, the count should
     * be <tt>0</tt>.
     */
    @Getter
    private int retryCount = 0;

    /**
     * The time that this exception was triggered.
     */
    @Getter
    private long when = now();

    /**
     * Constructs a new retriable exception with the specified detail message. The <tt>cause</tt> is not initialized,
     * and may subsequently be initialized by a call to {@link #initCause(Throwable)}.
     * <p>
     * This sets the retry count at <tt>0</tt> and uses the current time for the exception.
     * 
     * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()}
     *            method.
     */
    public RetriableException(@Nullable final String message) {
        this(message, 0, now());
    }

    public RetriableException(@Nullable final String message, long when) {
        this(message, 0, when);
    }

    public RetriableException(@Nullable final String message, int retryCount) {
        this(message, retryCount, now());
    }

    public RetriableException(@Nullable final String message, int retryCount, long when) {
        super(message);
        this.retryCount = retryCount;
        this.when = when;
    }

    /**
     * Constructs a new retriable exception with the specified cause and a detail message of
     * <tt>(cause==null ? null : cause.toString())</tt> (which typically contains the class and detail message of
     * <tt>cause</tt>). This constructor is useful for exceptions that are little more than wrappers for other
     * {@link Throwable}s.
     * <p>
     * This sets the retry count at <tt>0</tt> and uses the current time for the exception.
     *
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method). (A <tt>null</tt>
     *            value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public RetriableException(@Nullable final Throwable cause) {
        this(cause, 0, now());
    }

    public RetriableException(@Nullable final Throwable cause, long when) {
        this(cause, 0, when);
    }

    public RetriableException(@Nullable final Throwable cause, int retryCount) {
        this(cause, retryCount, now());
    }

    public RetriableException(@Nullable final Throwable cause, int retryCount, long when) {
        super(cause);
        this.retryCount = retryCount;
        this.when = when;
    }

    /**
     * Constructs a new retriable exception with the specified detail message and cause.
     * <p>
     * Note that the detail message associated with {@code cause} is <i>not</i> automatically incorporated in this
     * exception's detail message. This sets the retry count at <tt>0</tt> and uses the current time for the exception.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method). (A <tt>null</tt>
     *            value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public RetriableException(@Nullable final String message, @Nullable final Throwable cause) {
        this(message, cause, 0, now());
    }

    public RetriableException(@Nullable final String message, @Nullable final Throwable cause, long when) {
        this(message, cause, 0, when);
    }

    public RetriableException(@Nullable final String message, @Nullable final Throwable cause, int retryCount) {
        this(message, cause, retryCount, now());
    }

    public RetriableException(
            @Nullable final String message,
            @Nullable final Throwable cause,
            int retryCount,
            long when) {
        super(message, cause);
        this.retryCount = retryCount;
        this.when = when;
    }

    private static long now() {
        return System.currentTimeMillis();
    }

}

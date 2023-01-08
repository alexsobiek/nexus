package com.alexsobiek.nexus.util;

import java.util.function.Consumer;

public class Result<S, F> {
    private S success;
    private F fail;
    private String msg;

    /**
     * Sets this result to success
     * @param result Successful result
     * @return Result
     */
    public Result<S, F> success(S result) {
        this.success = result;
        this.fail = null;
        return this;
    }

    /**
     * Sets this result to fail
     * @param result Failed result
     * @return Result
     */
    public Result<S, F> fail(F result) {
        this.fail = result;
        this.success = null;
        return this;
    }

    /**
     * Sets an optional message
     * @param msg Message
     * @return Result
     */
    public Result<S, F> msg(String msg) {
        this.msg = msg;
        return this;
    }

    /**
     * Returns the successful result (if success)
     * @return Successful result
     */
    public S success() {
        return success;
    }

    /**
     * Returns the failed result (if failed)
     * @return Failed result
     */
    public F fail() {
        return fail;
    }

    /**
     * Returns the optional message
     * @return Message
     */
    public String msg() {
        return msg;
    }

    /**
     * Returns true if this result is successful
     * @return True if successful
     */
    public boolean isSuccessful() {
        return success != null;
    }

    /**
     * Returns true if this result is failed
     * @return True if failed
     */
    public boolean hasFailed() {
        return fail != null;
    }

    /**
     * Calls consumer if successful
     * @param consumer Consumer to accept successful result
     * @return Result
     */
    public Result<S, F> ifSuccess(Consumer<S> consumer) {
        if (isSuccessful()) consumer.accept(success);
        return this;
    }

    /**
     * Calls consumer if failed
     * @param consumer Consumer to accept the failed result
     * @return Result
     */
    public Result<S, F> ifFail(Consumer<F> consumer) {
        if (hasFailed()) consumer.accept(fail);
        return this;
    }

    /**
     * Calls success consumer if successful, otherwise calls fail consumer
     * @param successConsumer Consumer to accept successful result
     * @param failConsumer Consumer to accept failed result
     * @return Result
     */
    public Result<S, F> ifSuccessOrFail(Consumer<S> successConsumer, Consumer<F> failConsumer) {
        if (isSuccessful()) successConsumer.accept(success);
        else failConsumer.accept(fail);
        return this;
    }
}
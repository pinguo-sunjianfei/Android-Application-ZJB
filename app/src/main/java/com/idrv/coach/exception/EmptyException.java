package com.idrv.coach.exception;

/**
 * time: 2015/9/11
 * description:数据为空的时候发出的Exception
 *
 * @author sunjianfei
 */
public class EmptyException extends RuntimeException {
    public EmptyException() {
    }

    public EmptyException(String detailMessage) {
        super(detailMessage);
    }

    public EmptyException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public EmptyException(Throwable throwable) {
        super(throwable);
    }
}

package com.idrv.coach.exception;

/**
 * Created by sunjianfei on 14-4-10.
 */
public class DBException extends Exception {

    public DBException() {
        super();
    }

    public DBException(String message) {
        super(message);
    }

    public DBException(Throwable cause) {
        super(cause);
    }

    public DBException(String message, Throwable cause) {
        super(message, cause);
    }

    public interface Message {
        String FAIL_OPEN_DB = "Failed to open database.";
    }
}

package com.cocoiland.pricehistory.exceptions;

public class ESException extends Exception {
    public ESException(String message) {
        super(message);
    }

    public ESException(String message, Throwable cause) {
        super(message, cause);
    }
}

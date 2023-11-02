package com.cocoiland.pricehistory.exceptions;

import lombok.Getter;
import lombok.Setter;

public class ServiceException extends RuntimeException {
    @Getter
    @Setter
    private String message;

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Exception e) {
        super(e);
        this.message = message;

    }
}

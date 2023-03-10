package com.gznznzjsn.orderservice.domain.exception;

public class NotEnoughResourcesException extends RuntimeException {

    public NotEnoughResourcesException(String message) {
        super(message);
    }

}

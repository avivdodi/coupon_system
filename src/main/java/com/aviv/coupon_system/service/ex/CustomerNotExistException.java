package com.aviv.coupon_system.service.ex;

public class CustomerNotExistException extends Exception {
    public CustomerNotExistException(String message) {
        super(message);
    }
}

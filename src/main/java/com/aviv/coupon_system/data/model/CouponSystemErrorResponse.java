package com.aviv.coupon_system.data.model;

import org.springframework.http.HttpStatus;

/*A class that will used as template for sending error messages by CouponSystemAdvice class.*/

public class CouponSystemErrorResponse {

    private HttpStatus status;
    private String message;
    private long timestamp;

    public CouponSystemErrorResponse(HttpStatus status, String message, long timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }

    public static CouponSystemErrorResponse of(HttpStatus status, String message) {
        return new CouponSystemErrorResponse(status, message, System.currentTimeMillis());
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

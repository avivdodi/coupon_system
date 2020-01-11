package com.aviv.coupon_system.service;

public abstract interface AbsService {

    /**
     * The function setting the client id number.
     *
     * @param clientId
     */
    void setId(long clientId);

    /**
     * @return Return the client id.
     */
    long getId();

    /**
     * @return The function returns the client role number in the system.
     */
    int getRole();
}

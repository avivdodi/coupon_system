package com.aviv.coupon_system.rest;

import com.aviv.coupon_system.service.AbsService;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ClientSession {

    private AbsService absService;
    private long lastAccessedMillis;

    public AbsService getAbsService() {
        return absService;
    }

    public void setAbsService(AbsService absService) {
        this.absService = absService;
    }

    public long getLastAccessedMillis() {
        return lastAccessedMillis;
    }

    public void accessed() {
        lastAccessedMillis = System.currentTimeMillis();
    }

}

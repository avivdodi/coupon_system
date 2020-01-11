package com.aviv.coupon_system.service.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Timer;

/*The cleaner below starts the ExpiredSessionCleaner every 1 minute.*/

@Service
public class ClientSessionCleanerTask {

    private static final long SECOND_MILLIS = 1000;
    private static final long NO_DELAY = 0;
    private Timer timer;
    private ExpiredSessionCleaner sessionCleaner;


    @Autowired
    public ClientSessionCleanerTask(ExpiredSessionCleaner sessionCleaner, @Qualifier("timer") Timer timer) {
        this.sessionCleaner = sessionCleaner;
        this.timer = timer;
    }

    @PostConstruct
    public void task() {
        if (sessionCleaner != null) {
            timer.schedule(sessionCleaner, NO_DELAY, SECOND_MILLIS);
        }
    }

}

package com.aviv.coupon_system.service.tasks;

import com.aviv.coupon_system.rest.ClientSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;

/**
 * This component extends TimerTask and takes the existing tokensMap and remove the Key,Value (token String and ClientSession)
 * if the last access was before 30 min.
 */

@Component
public class ExpiredSessionCleaner extends TimerTask {
    private static final long HALF_AN_HOUR_MILLIS = 1000 * 60 * 30;
    private Map<String, ClientSession> tokensMap;

    @Autowired
    public ExpiredSessionCleaner(@Qualifier("tokensMap") Map<String, ClientSession> tokensMap) {
        this.tokensMap = tokensMap;
    }

    @Override
    public void run() {
        removeOldSessions();
    }

    private synchronized void removeOldSessions() {
        Set<Map.Entry<String, ClientSession>> entriesMap = tokensMap.entrySet();
        Iterator<Map.Entry<String, ClientSession>> mapIterator = entriesMap.iterator();

        while (mapIterator.hasNext()) {
            ClientSession client = mapIterator.next().getValue();
            if (System.currentTimeMillis() - client.getLastAccessedMillis() > HALF_AN_HOUR_MILLIS) {
                mapIterator.remove();
            }
        }
    }
}

package com.aviv.coupon_system.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

/*Configuration class that used for supply a single TokensMap to store tokens and clientSessions and Timer to tasks.*/

@Configuration
public class RestConfiguration {

    @Bean(name = "tokensMap")
    public Map<String, ClientSession> tokensMap() {
        return new HashMap<>();
    }

    @Bean(name = "timer")
    public Timer timer() {
        return new Timer();
    }
}

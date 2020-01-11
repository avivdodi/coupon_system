package com.aviv.coupon_system.service.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.Timer;

/*Service that starts the Daily task of remove expired coupons every midnight.*/

@Service
public class CouponCleanerTask {
    private static final long DAY_MILLIS = 1000 * 60 * 60 * 24;
    private DailyTask dailyTask;
    private Timer timer;

    @Autowired
    public CouponCleanerTask(DailyTask dailyTask, @Qualifier("timer") Timer timer) {
        this.dailyTask = dailyTask;
        this.timer = timer;
    }

    @PostConstruct
    public void start() {
        timer.scheduleAtFixedRate(dailyTask, timerToMidnight(), DAY_MILLIS);
    }

    private long timerToMidnight() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return (calendar.getTimeInMillis() - System.currentTimeMillis());
    }
}

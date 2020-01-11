package com.aviv.coupon_system.service.tasks;

import com.aviv.coupon_system.data.db.CouponRepository;
import com.aviv.coupon_system.data.model.Coupon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.TimerTask;

/*This task will be called once a day and will erase an expired coupons.*/

@Component
public class DailyTask extends TimerTask {

    private CouponRepository couponRepository;

    @Autowired
    public DailyTask(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Override
    public void run() {
        removeExpiredCoupons();
    }

    private synchronized void removeExpiredCoupons() {
        List<Coupon> coupons = couponRepository.findAllByEndDateBefore(LocalDate.now());
        for (Coupon coupon : coupons) {
            couponRepository.deleteById(coupon.getId());
        }
    }
}

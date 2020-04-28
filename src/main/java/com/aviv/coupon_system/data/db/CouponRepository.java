package com.aviv.coupon_system.data.db;

import com.aviv.coupon_system.data.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;


public interface CouponRepository extends JpaRepository<Coupon, Long> {

    /*Company functions:*/

    @Query("select c from Coupon c where c.company.id =:companyId")
    List<Coupon> findAllByCompanyId(long companyId);

    @Query("select c from Coupon c where c.company.id =:companyId and c.category =:category")
    List<Coupon> findAllByCompanyIdAndCategory(long companyId, int category);

    @Query("select c from Coupon c where c.company.id =:companyId and c.price <=:price")
    List<Coupon> findAllByCompanyIdAndPriceLessThan(long companyId, double price);

    @Query("select c from Coupon c where c.company.id =:companyId and c.endDate <=:date")
    List<Coupon> findAllByCompanyIdAndDateBefore(long companyId, LocalDate date);

    /*Also used by DailyTask*/
    @Override
    void deleteById(Long id);

    /*Customer functions:*/

    @Query("select c from Customer as cast join cast.coupons as c where cast.id =:customerId")
    List<Coupon> findAllByCustomerId(long customerId);

    @Query("select c from Customer as cast join cast.coupons as c where cast.id =:customerId and c.category =:category")
    List<Coupon> findAllByCustomerIdAndCategory(long customerId, int category);

    @Query("select c from Customer as cast join cast.coupons as c where cast.id =:customerId and c.price <:price")
    List<Coupon> findAllByCustomerIdAndPriceLessThan(long customerId, double price);

    @Query("select c from Customer as cast join cast.coupons as c where cast.id =:customerId and c.endDate <=:date")
    List<Coupon> findCouponsByCustomerIdAndBeforeEndDate(long customerId, LocalDate date);

    @Query("select c from Coupon c where c.amount <1 and c.company.id =:companyId")
    List<Coupon> findZeroAmountCoupons(long companyId);

    @Query("select c from Coupon c where c.amount > 0")
    List<Coupon> findAllAvailableCoupons();


    /*Admin functions:*/
    @Query("select c from Coupon c where c.endDate <=:date")
    List<Coupon> findCouponsBeforeEndDate(LocalDate date);

    @Query("select c from Coupon c where c.amount <1")
    List<Coupon> findEmptyCoupons();

    List<Coupon> findAllByEndDateBefore(LocalDate endDate);

}

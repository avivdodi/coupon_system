package com.aviv.coupon_system.service;

import com.aviv.coupon_system.data.model.Coupon;
import com.aviv.coupon_system.data.model.Customer;
import com.aviv.coupon_system.service.ex.CouponAlreadyPurchasedException;
import com.aviv.coupon_system.service.ex.CouponNotExistException;
import com.aviv.coupon_system.service.ex.UpdateNotAllowedException;
import com.aviv.coupon_system.service.ex.ZeroCouponAmountException;
import org.springframework.core.convert.ConversionFailedException;

import java.time.LocalDate;
import java.util.List;

public interface CustomerService extends AbsService {

    void setId(long customerId);

    long getId();

    int getRole();

    /**
     * @return all the coupons of a specific customer.
     */
    List<Coupon> findAllByCustomerId();

    /**
     * @param category
     * @return all coupons of a customer with specific category.
     */
    List<Coupon> findAllByCustomerIdAndCategory(int category);

    /**
     * @param price
     * @return all coupons of a customer with price that is less than the inserted price.
     */
    List<Coupon> findAllByCustomerIdAndPriceLessThan(double price);

    /**
     * @param date
     * @return all coupons of a customer with end date before the inserted date.
     * @throws ConversionFailedException
     */
    List<Coupon> findCouponsBeforeEndDate(LocalDate date) throws ConversionFailedException;

    /**
     * Function that allowe to a customer to buy a coupon.
     *
     * @param couponId to purchase.
     * @return
     * @throws CouponAlreadyPurchasedException
     * @throws ZeroCouponAmountException
     * @throws CouponNotExistException
     */
    Coupon purchaseCoupon(long couponId) throws CouponAlreadyPurchasedException,
            ZeroCouponAmountException, CouponNotExistException;

    /**
     * Function that customer can update it's own details.
     *
     * @param customer
     * @return
     * @throws UpdateNotAllowedException
     */
    Customer update(Customer customer) throws UpdateNotAllowedException;


}

package com.aviv.coupon_system.service;

import com.aviv.coupon_system.data.model.Company;
import com.aviv.coupon_system.data.model.Coupon;
import com.aviv.coupon_system.service.ex.CouponAlreadyExistsException;
import com.aviv.coupon_system.service.ex.CouponNotExistException;
import com.aviv.coupon_system.service.ex.IllegalOperationException;
import com.aviv.coupon_system.service.ex.UpdateNotAllowedException;
import org.springframework.core.convert.ConversionFailedException;

import java.time.LocalDate;
import java.util.List;

public interface CompanyService extends AbsService {

    void setId(long companyId);

    long getId();

    int getRole();

    /**
     * @return This function returns all the coupons of specific company.
     */
    List<Coupon> findAllByCompanyId();

    /**
     * @param category
     * @return This function returns all the coupons of specific company and category.
     */
    List<Coupon> findAllByCompanyIdAndCategory(int category);

    /**
     * @param price
     * @return This function returns all the coupons of specific company and with the price that under an inserted price.
     */
    List<Coupon> findAllByCompanyIdAndPriceLessThan(double price);

    /**
     * @param date
     * @return This function returns all the coupons of specific company and that end before a given date.
     * @throws ConversionFailedException
     */
    List<Coupon> findAllByCompanyIdAndDateBefore(LocalDate date) throws ConversionFailedException;

    /**
     * This function allow to a company to add a coupon to the DB.
     *
     * @param coupon
     * @return
     * @throws CouponAlreadyExistsException
     * @throws IllegalOperationException
     */
    Coupon addCoupon(Coupon coupon) throws CouponAlreadyExistsException, IllegalOperationException;

    /**
     * Function that remove coupon from DB of the related company.
     *
     * @param id Coupon id to remove.
     * @throws CouponNotExistException
     * @throws IllegalOperationException
     */
    void removeCoupon(long id) throws CouponNotExistException, IllegalOperationException;

    /**
     * Function that let to a company update it's own coupon.
     *
     * @param coupon
     * @return
     * @throws CouponNotExistException
     * @throws IllegalOperationException
     * @throws UpdateNotAllowedException
     */
    Coupon updateCoupon(Coupon coupon) throws CouponNotExistException, IllegalOperationException, UpdateNotAllowedException;

    /**
     * Function which used by a company to update details in the DB.
     *
     * @param company
     * @return
     * @throws UpdateNotAllowedException
     * @throws IllegalOperationException
     */
    Company updateCompany(Company company) throws UpdateNotAllowedException, IllegalOperationException;

    /**
     * @return This function returns the empty coupons of the specific company.
     */
    List<Coupon> findZeroAmountCoupons();

}

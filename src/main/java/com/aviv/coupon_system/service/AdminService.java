package com.aviv.coupon_system.service;

import com.aviv.coupon_system.data.model.Company;
import com.aviv.coupon_system.data.model.Coupon;
import com.aviv.coupon_system.data.model.Customer;
import com.aviv.coupon_system.service.ex.CompanyNotExistException;
import com.aviv.coupon_system.service.ex.CustomerNotExistException;
import com.aviv.coupon_system.service.ex.IllegalOperationException;
import com.aviv.coupon_system.service.ex.UpdateNotAllowedException;
import org.springframework.core.convert.ConversionFailedException;

import java.time.LocalDate;
import java.util.List;

public interface AdminService extends AbsService {

    void setId(long adminId);

    long getId();

    int getRole();

    Company createCompany(Company company) throws IllegalOperationException;

    void removeCompany(long id) throws CompanyNotExistException;

    Company updateCompany(Company company) throws CompanyNotExistException, UpdateNotAllowedException;

    void removeAllCompanies();

    Customer createCustomer(Customer customer) throws IllegalOperationException;

    void removeCustomer(long id) throws CustomerNotExistException;

    Customer updateCustomer(Customer customer) throws CustomerNotExistException, UpdateNotAllowedException;

    void removeAllCustomers();

    /**
     * Function that return all the coupons which ends before specific date.
     *
     * @param date
     * @return
     * @throws ConversionFailedException If there is an error with the inserted date, the ConversionFailedException will throw.
     */
    List<Coupon> findCouponsBeforeEndDate(LocalDate date) throws ConversionFailedException;

    /**
     * This function find coupons with 0 amount.
     *
     * @return List of coupons.
     */
    List<Coupon> findEmptyCoupons();

}

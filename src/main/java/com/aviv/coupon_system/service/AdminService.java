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

    /**
     * Function that create a company.
     *
     * @param company
     * @return Company with the id.
     * @throws IllegalOperationException
     */
    Company createCompany(Company company) throws IllegalOperationException;

    /**
     * Remove a comapny with the inserted id.
     *
     * @param id
     * @throws CompanyNotExistException
     */
    void removeCompany(long id) throws CompanyNotExistException;

    /**
     * Update a company using the inserted company with an id.
     *
     * @param company
     * @return The company that updated in the DB.
     * @throws CompanyNotExistException
     * @throws UpdateNotAllowedException
     */
    Company updateCompany(Company company) throws CompanyNotExistException, UpdateNotAllowedException;

    /**
     * A function that remove all the companies in the DB.
     */
    void removeAllCompanies();

    /**
     * Function that create a customer in the DB.
     *
     * @param customer
     * @return The new customer with id from DB.
     * @throws IllegalOperationException
     */
    Customer createCustomer(Customer customer) throws IllegalOperationException;

    /**
     * Remove a customer by inserted id.
     *
     * @param id
     * @throws CustomerNotExistException
     */
    void removeCustomer(long id) throws CustomerNotExistException;

    /**
     * Update a customer using customer argument with same id.
     *
     * @param customer
     * @return The new customer in DB.
     * @throws CustomerNotExistException
     * @throws UpdateNotAllowedException
     */
    Customer updateCustomer(Customer customer) throws CustomerNotExistException, UpdateNotAllowedException;

    /**
     * Removes all the customer in the DB.
     */
    void removeAllCustomers();

    /**
     * Function that return all the coupons which ends before specific date.
     *
     * @param date
     * @return List of coupons.
     * @throws ConversionFailedException If there is an error with the inserted date, the ConversionFailedException will throw.
     */
    List<Coupon> findCouponsBeforeEndDate(LocalDate date) throws ConversionFailedException;

    /**
     * This function find coupons with 0 amount.
     *
     * @return List of coupons.
     */
    List<Coupon> findEmptyCoupons();

    /**
     * Finds all companies in the DB.
     *
     * @return List of companies.
     */
    List<Company> findAllCompanies();

    /**
     * Find all customers in the DB.
     *
     * @return List of customers.
     */
    List<Customer> findAllCustomers();

}

package com.aviv.coupon_system.service;

import com.aviv.coupon_system.data.db.CompanyRepository;
import com.aviv.coupon_system.data.db.CouponRepository;
import com.aviv.coupon_system.data.db.CustomerRepository;
import com.aviv.coupon_system.data.model.Company;
import com.aviv.coupon_system.data.model.Coupon;
import com.aviv.coupon_system.data.model.Customer;
import com.aviv.coupon_system.data.model.User;
import com.aviv.coupon_system.service.ex.CompanyNotExistException;
import com.aviv.coupon_system.service.ex.CustomerNotExistException;
import com.aviv.coupon_system.service.ex.IllegalOperationException;
import com.aviv.coupon_system.service.ex.UpdateNotAllowedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class AdminServiceImpl implements AdminService {
    private final CustomerRepository customerRepository;
    private final CouponRepository couponRepository;
    private final CompanyRepository companyRepository;
    private long adminId;
    private final int role = -1;


    @Autowired
    public AdminServiceImpl(CustomerRepository customerRepository, CouponRepository couponRepository, CompanyRepository companyRepository) {
        this.customerRepository = customerRepository;
        this.couponRepository = couponRepository;
        this.companyRepository = companyRepository;
    }

    @Override
    public Company createCompany(Company company) throws IllegalOperationException {
        if (companyCheck(company)) {
            throw new IllegalOperationException("Some fields of the inserted company are null.");
        }
        if (companyRepository.findByEmail(company.getUser().getEmail()).isPresent()) {
            throw new IllegalOperationException(
                    String.format("User with email '%s' is already exist.", company.getUser().getEmail()));
        }
        company.setId(0);
        User user = company.getUser();
        user.setId(0);
        user.setClient(company);
        company.setUser(user);
        return companyRepository.save(company);
    }

    @Override
    public void removeCompany(long id) throws CompanyNotExistException {
        Optional<Company> optionalCompany = companyRepository.findById(id);
        if (!optionalCompany.isPresent()) {
            String msg = String.format("Company with id %s is not exist.", id);
            throw new CompanyNotExistException(msg);
        }
        companyRepository.deleteById(id);
    }

    @Override
    public Company updateCompany(Company company) throws CompanyNotExistException, UpdateNotAllowedException {
        if (companyCheck(company)) {
            throw new UpdateNotAllowedException("Some fields of the inserted company are null.");
        }
        Optional<Company> optionalCompany = companyRepository.findById(company.getId());
        if (!optionalCompany.isPresent()) {
            String msg = String.format("Company with id %s is not exist.", company.getId());
            throw new CompanyNotExistException(msg);
        }
        if (!company.getUser().getEmail().equals(optionalCompany.get().getUser().getEmail())) {
            throw new UpdateNotAllowedException("The inserted company email is different from DB company.");
        }
        company.setCoupons(couponRepository.findAllByCompanyId(company.getId()));
        User user = company.getUser();
        user.setId(optionalCompany.get().getUser().getId());
        user.setClient(company);
        company.setUser(user);
        return companyRepository.save(company);
    }

    @Override
    public void removeAllCompanies() {
        companyRepository.deleteAll();
    }

    @Override
    public Customer createCustomer(Customer customer) throws IllegalOperationException {
        if (customerCheck(customer)) {
            throw new IllegalOperationException("The inserted customer is null.");
        }
        if (customerRepository.findByEmail(customer.getUser().getEmail()).isPresent()) {
            throw new IllegalOperationException(
                    String.format("User with email '%s' is already exist.", customer.getUser().getEmail()));
        }
        customer.setId(0);
        User user = customer.getUser();
        user.setId(0);
        user.setClient(customer);
        customer.setUser(user);
        return customerRepository.save(customer);
    }

    @Override
    public void removeCustomer(long id) throws CustomerNotExistException {
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        if (!optionalCustomer.isPresent()) {
            String msg = String.format("Customer with id %s is not exist.", id);
            throw new CustomerNotExistException(msg);
        }
        customerRepository.deleteById(id);
    }

    @Override
    public Customer updateCustomer(Customer customer) throws CustomerNotExistException, UpdateNotAllowedException {
        if (customerCheck(customer)) {
            throw new UpdateNotAllowedException("Some fields of the inserted company are null.");
        }
        Optional<Customer> optionalCustomer = customerRepository.findById(customer.getId());
        if (!optionalCustomer.isPresent()) {
            String msg = String.format("Customer with id %s is not exist.", customer.getId());
            throw new CustomerNotExistException(msg);
        }

        if (!customer.getUser().getEmail().equals(optionalCustomer.get().getUser().getEmail())) {
            throw new UpdateNotAllowedException("The inserted company email is different from DB company.");
        }
        customer.setCoupons(couponRepository.findAllByCustomerId(customer.getId()));
        User user = customer.getUser();
        user.setId(optionalCustomer.get().getUser().getId());
        user.setClient(customer);
        customer.setUser(user);
        return customerRepository.save(customer);
    }

    @Override
    public void removeAllCustomers() {
        customerRepository.deleteAll();
    }

    /**
     * @param date
     * @return
     * @throws ConversionFailedException The role of the LocalDate.parse is to check the date validity, includes February.
     */
    @Override
    public List<Coupon> findCouponsBeforeEndDate(LocalDate date) throws ConversionFailedException {
        LocalDate.parse(date.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return couponRepository.findCouponsBeforeEndDate(date);
    }

    @Override
    public List<Coupon> findEmptyCoupons() {
        return couponRepository.findEmptyCoupons();
    }

    @Override
    public void setId(long adminId) {
        this.adminId = adminId;
    }

    @Override
    public long getId() {
        return adminId;
    }

    @Override
    public int getRole() {
        return role;
    }

    /**
     * This function check if there is null field in the inserted customer.
     *
     * @param customer
     * @return true if there is null field.
     */
    private boolean customerCheck(Customer customer) {
        return Stream.of(customer.getUser().getEmail(), customer.getUser().getPassword(), customer.getFirstName(),
                customer.getLastName()).anyMatch(Objects::isNull);
    }

    /**
     * This function check the validity of the inserted company.
     *
     * @param company
     * @return true if there is null field.
     */
    private boolean companyCheck(Company company) {
        return Stream.of(company.getUser().getEmail(), company.getName(), company.getUser().getPassword())
                .anyMatch(Objects::isNull);
    }
}

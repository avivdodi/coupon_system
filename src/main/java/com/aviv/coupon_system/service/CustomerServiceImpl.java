package com.aviv.coupon_system.service;

import com.aviv.coupon_system.data.db.CouponRepository;
import com.aviv.coupon_system.data.db.CustomerRepository;
import com.aviv.coupon_system.data.model.Coupon;
import com.aviv.coupon_system.data.model.Customer;
import com.aviv.coupon_system.data.model.User;
import com.aviv.coupon_system.service.ex.CouponAlreadyPurchasedException;
import com.aviv.coupon_system.service.ex.CouponNotExistException;
import com.aviv.coupon_system.service.ex.UpdateNotAllowedException;
import com.aviv.coupon_system.service.ex.ZeroCouponAmountException;
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
public class CustomerServiceImpl implements CustomerService {
    private static final int ZERO_AMOUNT = 0;
    private final CustomerRepository customerRepository;
    private final CouponRepository couponRepository;
    private long customerId;
    private final int role = 1;


    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, CouponRepository couponRepository) {
        this.customerRepository = customerRepository;
        this.couponRepository = couponRepository;
    }


    @Override
    public List<Coupon> findAllByCustomerId() {
        return couponRepository.findAllByCustomerId(customerId);
    }

    @Override
    public List<Coupon> findAllByCustomerIdAndCategory(int category) {
        return couponRepository.findAllByCustomerIdAndCategory(customerId, category);
    }

    @Override
    public List<Coupon> findAllByCustomerIdAndPriceLessThan(double price) {
        return couponRepository.findAllByCustomerIdAndPriceLessThan(customerId, price);
    }

    /**
     * This function is used by a customer to purchase a customer. It's checks if the coupon is exist, if the coupon
     * has not bought by the customer, and if there is enough amount.
     *
     * @param couponId to purchase.
     * @return
     * @throws CouponAlreadyPurchasedException
     * @throws ZeroCouponAmountException
     * @throws CouponNotExistException
     */
    @Override
    public Coupon purchaseCoupon(long couponId) throws CouponAlreadyPurchasedException,
            ZeroCouponAmountException, CouponNotExistException {
        Optional<Coupon> optionalCoupon = couponRepository.findById(couponId);
        if (!optionalCoupon.isPresent()) {
            throw new CouponNotExistException();
        }
        List<Coupon> coupons = couponRepository.findAllByCustomerId(customerId);
        Coupon coupon = optionalCoupon.get();
        for (Coupon c : coupons) {
            if (c.getId() == couponId) {
                throw new CouponAlreadyPurchasedException();
            }
        }

        if (coupon.getAmount() <= ZERO_AMOUNT) {
            throw new ZeroCouponAmountException();
        }

        Customer customer = customerRepository.findById(customerId).get();
        coupon.setAmount(coupon.getAmount() - 1);
        coupon.add(customer);
        couponRepository.save(coupon);
        return coupon;
    }

    /**
     * In this moment the system allows a customer to update his email.
     *
     * @param customer
     * @return
     * @throws UpdateNotAllowedException
     */
    @Override
    public Customer update(Customer customer) throws UpdateNotAllowedException {
        customer.setId(customerId);
        if (customerCheck(customer)) {
            throw new UpdateNotAllowedException("The given company cannot be updated. One field or more is missing.");
        }
        Customer dbCustomer = customerRepository.findById(customerId).get();
        User user = customer.getUser();
        user.setClient(customer);
        user.setId(dbCustomer.getUser().getId());
        customer.setUser(user);
        customer.setCoupons(findAllByCustomerId());
        return customerRepository.save(customer);
    }

    @Override
    public List<Coupon> findCouponsBeforeEndDate(LocalDate date) throws ConversionFailedException {
        LocalDate.parse(date.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return couponRepository.findCouponsByCustomerIdAndBeforeEndDate(customerId, date);
    }


    @Override
    public void setId(long customerId) {
        this.customerId = customerId;
    }

    @Override
    public long getId() {
        return this.customerId;
    }

    @Override
    public int getRole() {
        return role;
    }

    /**
     * This function check if there is null field in the inserted customer.
     *
     * @param customer
     * @return True if any field is null.
     */
    private boolean customerCheck(Customer customer) {
        return Stream.of(customer.getUser().getEmail(), customer.getUser().getPassword(), customer.getFirstName(),
                customer.getLastName()).anyMatch(Objects::isNull);
    }
}

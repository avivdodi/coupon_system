package com.aviv.coupon_system.data.db;

import com.aviv.coupon_system.data.model.Customer;
import com.aviv.coupon_system.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query("from User as u where u.email=:email")
    Optional<User> findByEmail(String email);
}

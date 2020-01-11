package com.aviv.coupon_system.data.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * In admin entity, I'm choose to store the email and password in addition to the User field.
 * The reasons is to valid the fields between this entities in the future.
 */
@Entity
@Table(name = "admin")
public class Admin extends Client {

    private String password;
    private String email;
    @OneToOne(cascade = CascadeType.ALL)
    private User user;

    public Admin() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

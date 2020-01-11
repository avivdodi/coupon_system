package com.aviv.coupon_system.service;

import com.aviv.coupon_system.data.db.CompanyRepository;
import com.aviv.coupon_system.data.db.CouponRepository;
import com.aviv.coupon_system.data.model.Company;
import com.aviv.coupon_system.data.model.Coupon;
import com.aviv.coupon_system.data.model.User;
import com.aviv.coupon_system.service.ex.CouponAlreadyExistsException;
import com.aviv.coupon_system.service.ex.CouponNotExistException;
import com.aviv.coupon_system.service.ex.IllegalOperationException;
import com.aviv.coupon_system.service.ex.UpdateNotAllowedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;
    private final CouponRepository couponRepository;
    private long companyId;
    private final int role = 2;

    @Autowired
    public CompanyServiceImpl(CompanyRepository companyRepository, CouponRepository couponRepository) {
        this.companyRepository = companyRepository;
        this.couponRepository = couponRepository;
    }

    @Override
    public List<Coupon> findAllByCompanyId() {
        return couponRepository.findAllByCompanyId(companyId);
    }

    @Override
    public List<Coupon> findAllByCompanyIdAndCategory(int category) {
        return couponRepository.findAllByCompanyIdAndCategory(companyId, category);
    }

    @Override
    public List<Coupon> findAllByCompanyIdAndPriceLessThan(double price) {
        return couponRepository.findAllByCompanyIdAndPriceLessThan(companyId, price);
    }

    @Override
    public List<Coupon> findAllByCompanyIdAndDateBefore(LocalDate date) throws ConversionFailedException {
        LocalDate.parse(date.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return couponRepository.findAllByCompanyIdAndDateBefore(companyId, date);
    }

    @Override
    public Coupon addCoupon(Coupon coupon) throws CouponAlreadyExistsException, IllegalOperationException,
            HttpMessageNotReadableException {
        if (couponCheck(coupon)) {
            throw new IllegalOperationException("The inserted coupon is illegal. " +
                    "Please check amount, dates correctness, and if all data are filled.");
        }

        List<Coupon> companyCoupons = couponRepository.findAllByCompanyId(companyId);
        for (Coupon c : companyCoupons) {
            if (c.getTitle().equals(coupon.getTitle())) {
                throw new CouponAlreadyExistsException();
            }
        }

        coupon.setId(0);
        coupon.setCompany(companyRepository.findById(companyId).get());
        coupon.setCustomers(null);
        return couponRepository.save(coupon);
    }


    @Override
    public void removeCoupon(long id) throws CouponNotExistException, IllegalOperationException {
        Optional<Coupon> optCoupon = couponRepository.findById(id);
        if (!optCoupon.isPresent()) {
            throw new CouponNotExistException();
        }

        Coupon coupon = optCoupon.get();
        if (coupon.getCompany().getId() != companyId) {
            throw new IllegalOperationException("The coupon isn't own to the current company.");
        }
        couponRepository.deleteById(id);
    }

    @Override
    public Coupon updateCoupon(Coupon coupon) throws CouponNotExistException,
            IllegalOperationException, UpdateNotAllowedException {
        if (couponCheck(coupon)) {
            throw new IllegalOperationException("The inserted coupon is illegal. " +
                    "Please check amount, dates correctness, and if all data are filled.");
        }

        Optional<Coupon> optCoupon = couponRepository.findById(coupon.getId());
        if (!optCoupon.isPresent()) {
            throw new CouponNotExistException();
        }

        Coupon dbCoupon = optCoupon.get();
        if (dbCoupon.getCompany().getId() != companyId) {
            throw new IllegalOperationException("The coupon isn't own to the current company.");
        }

        if (!dbCoupon.getTitle().equals(coupon.getTitle())) {
            throw new UpdateNotAllowedException(
                    "The inserted coupon containing a different title from the DB coupon with the same id.");
        }

        coupon.setCompany(companyRepository.findById(companyId).get());
        coupon.setCustomers(dbCoupon.getCustomers());
        return couponRepository.save(coupon);
    }

    /**
     * In this moment a company is allowed to change email.
     *
     * @param company
     * @return
     * @throws UpdateNotAllowedException
     * @throws IllegalOperationException
     */
    @Override
    public Company updateCompany(Company company) throws UpdateNotAllowedException, IllegalOperationException {
        company.setId(companyId);
        if (companyCheck(company)) {
            throw new UpdateNotAllowedException("The given company cannot be updated. One field or more is missing");
        }
        if (companyRepository.findByEmail(company.getUser().getEmail()).isPresent()) {
            throw new IllegalOperationException(
                    String.format("User with email '%s' is already exist.", company.getUser().getEmail()));
        }
        Company dbCompany = companyRepository.findById(companyId).get();
        company.setCoupons(findAllByCompanyId());
        User user = company.getUser();
        user.setClient(company);
        user.setId(dbCompany.getUser().getId());
        company.setUser(user);
        return companyRepository.save(company);
    }

    @Override
    public List<Coupon> findZeroAmountCoupons() {
        return couponRepository.findZeroAmountCoupons(companyId);
    }

    @Override
    public void setId(long companyId) {
        this.companyId = companyId;
    }

    @Override
    public long getId() {
        return companyId;
    }

    @Override
    public int getRole() {
        return role;
    }

    /**
     * In this moment, the system allow to add a coupon with price 0.
     * The function check that there is no null field in the coupon Json or illegal dates.
     *
     * @param coupon
     * @return True if something is null or illegal.
     */
    private boolean couponCheck(Coupon coupon) {
        return Stream.of(coupon.getTitle(), coupon.getDescription(),
                coupon.getImageURL(), coupon.getEndDate(), coupon.getStartDate())
                .anyMatch(Objects::isNull) || coupon.getAmount() < 1 ||
                coupon.getCategory() == 0 ||
                coupon.getEndDate().isBefore(coupon.getStartDate()) ||
                coupon.getEndDate().isBefore(LocalDate.now());
    }

    /**
     * This function check the validity of the inserted company.
     *
     * @param company
     * @return true if a field is null.
     */
    private boolean companyCheck(Company company) {
        return Stream.of(company.getUser().getEmail(), company.getName(), company.getUser().getPassword()).anyMatch(Objects::isNull);
    }

}

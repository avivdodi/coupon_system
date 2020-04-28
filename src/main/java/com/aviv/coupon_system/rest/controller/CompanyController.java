package com.aviv.coupon_system.rest.controller;

import com.aviv.coupon_system.data.model.Company;
import com.aviv.coupon_system.data.model.Coupon;
import com.aviv.coupon_system.rest.ClientSession;
import com.aviv.coupon_system.service.CompanyService;
import com.aviv.coupon_system.service.ex.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/*This controllers handle the client type company requests and return responses to the client.*/

@CrossOrigin
@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private Map<String, ClientSession> tokensMap;

    @Autowired
    public CompanyController(@Qualifier("tokensMap") Map<String, ClientSession> tokensMap) {
        this.tokensMap = tokensMap;
    }

    /***
     * Add a coupon to DB.
     * @param coupon
     * @param token
     * @return
     * @throws InvalidLoginException
     * @throws CouponAlreadyExistsException
     * @throws IllegalOperationException
     * @throws HttpMessageNotReadableException
     * The HttpMessageNotReadableException has been declared to the case if there is wrong dates in the Json.
     */
    @PostMapping("/addCoupon/{token}")
    public ResponseEntity<Coupon> addCoupon(@RequestBody Coupon coupon, @PathVariable String token)
            throws InvalidLoginException, CouponAlreadyExistsException, IllegalOperationException, HttpMessageNotReadableException {
        CompanyService companyService = getService(token);
        companyService.addCoupon(coupon);
        return ResponseEntity.ok(coupon);
    }


    @DeleteMapping("/{token}/coupons/remove/{couponId}")
    public ResponseEntity<String> removeCoupon(@PathVariable long couponId, @PathVariable String token)
            throws InvalidLoginException, CouponNotExistException, IllegalOperationException {
        CompanyService companyService = getService(token);
        companyService.removeCoupon(couponId);
        String msg = String.format("Coupon with id %s has removed now.", couponId);
        return ResponseEntity.ok(msg);
    }

    @PutMapping("/coupons/update/{token}")
    public ResponseEntity<String> updateCoupon(@RequestBody Coupon coupon, @PathVariable String token)
            throws InvalidLoginException, IllegalOperationException, UpdateNotAllowedException,
            CouponNotExistException {

        CompanyService companyService = getService(token);
        Coupon updateCoupon = companyService.updateCoupon(coupon);
        String msg = String.format("Coupon with id '%s' has updated now.", updateCoupon.getId());
        return ResponseEntity.ok(msg);
    }

    @PutMapping("/updateCompany/{token}")
    public ResponseEntity<Company> updateCompany(@RequestBody Company company, @PathVariable String token)
            throws InvalidLoginException, UpdateNotAllowedException, IllegalOperationException {

        CompanyService companyService = getService(token);
        Company updateCompany = companyService.updateCompany(company);
        return ResponseEntity.ok(updateCompany);
    }


    @GetMapping("/coupons/{token}")
    public ResponseEntity<List<Coupon>> getCompanyCoupons(@PathVariable String token)
            throws InvalidLoginException {
        CompanyService companyService = getService(token);
        List<Coupon> coupons = companyService.findAllByCompanyId();
        return coupons.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(coupons);
    }

    @GetMapping("/coupons/{token}/category/{category}")
    public ResponseEntity<List<Coupon>> getCompanyCouponsByCategory(
            @PathVariable String token, @PathVariable int category) throws InvalidLoginException {
        CompanyService companyService = getService(token);
        List<Coupon> coupons = companyService.findAllByCompanyIdAndCategory(category);
        return coupons.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(coupons);
    }

    @GetMapping("/coupons/{token}/price/{price}")
    public ResponseEntity<List<Coupon>> getCompanyCouponsByPriceLessThan(
            @PathVariable String token, @PathVariable double price) throws InvalidLoginException {
        CompanyService companyService = getService(token);
        List<Coupon> coupons = companyService.findAllByCompanyIdAndPriceLessThan(price);
        return coupons.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(coupons);
    }

    @GetMapping("/coupons/{token}/date/{date}")
    public ResponseEntity<List<Coupon>> getCompanyCouponsByDateBefore(@PathVariable String token,
                                                                      @PathVariable @DateTimeFormat
                                                                              (iso = DateTimeFormat.ISO.DATE)
                                                                              LocalDate date)
            throws InvalidLoginException, ConversionFailedException {

        CompanyService companyService = getService(token);
        List<Coupon> coupons = companyService.findAllByCompanyIdAndDateBefore(date);
        return coupons.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(coupons);
    }


    @GetMapping("/{token}/coupons/empty")
    public ResponseEntity<List<Coupon>> getZeroAmountCoupons(@PathVariable String token) throws InvalidLoginException {
        CompanyService companyService = getService(token);
        List<Coupon> emptyCoupons = companyService.findZeroAmountCoupons();
        return emptyCoupons.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(emptyCoupons);
    }

    /**
     * This function log off a company. There is a check if the company has the right token of the specific type of user,
     * Then the token and the clientSession removed from the Tokens map.
     * The delete mapping is my preferred map to this function. Since GET is less secure and I don't post new ClientSession,
     * The DeleteMapping is the right way to delete token, although it's not from the DB.
     * @param token
     * @return
     * @throws InvalidLoginException
     * @throws ClassCastException
     */
    @DeleteMapping("/logOff/{token}")
    public ResponseEntity<HttpStatus> logOff(@PathVariable String token) throws InvalidLoginException,
            ClassCastException {
        getService(token);
        ClientSession clientSession = tokensMap.get(token);
        tokensMap.remove(token, clientSession);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    /**
     * The function below checks the token validity and refresh the relevant Client session.
     * @param token
     * @return
     * @throws InvalidLoginException
     */

    private CompanyService getService(String token) throws InvalidLoginException {
        ClientSession session = tokensMap.get(token);
        if (null == session) {
            throw new InvalidLoginException();
        }
        session.accessed();
        return (CompanyService) session.getAbsService();
    }
}

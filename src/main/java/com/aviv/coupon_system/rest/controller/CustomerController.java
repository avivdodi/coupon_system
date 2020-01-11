package com.aviv.coupon_system.rest.controller;

import com.aviv.coupon_system.data.model.Coupon;
import com.aviv.coupon_system.data.model.Customer;
import com.aviv.coupon_system.rest.ClientSession;
import com.aviv.coupon_system.service.CustomerService;
import com.aviv.coupon_system.service.ex.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/*This controllers handle the client type customer requests and return responses to the client.*/

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private Map<String, ClientSession> tokensMap;

    @Autowired
    public CustomerController(@Qualifier("tokensMap") Map<String, ClientSession> tokensMap) {
        this.tokensMap = tokensMap;
    }

    @GetMapping("/coupons/{token}")
    public ResponseEntity<List<Coupon>> getCustomerCoupons(@PathVariable String token) throws InvalidLoginException {
        CustomerService customerService = getService(token);
        List<Coupon> coupons = customerService.findAllByCustomerId();
        return coupons.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(coupons);
    }


    @GetMapping("/coupons/{token}/category/{category}")
    public ResponseEntity<List<Coupon>> getCustomerCouponsByCategory(
            @PathVariable String token, @PathVariable int category) throws InvalidLoginException {
        CustomerService customerService = getService(token);
        List<Coupon> coupons = customerService.findAllByCustomerIdAndCategory(category);
        return coupons.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(coupons);
    }

    @GetMapping("/coupons/{token}/price/{price}")
    public ResponseEntity<List<Coupon>> getCustomerCouponsByPriceLessThan(
            @PathVariable String token, @PathVariable double price) throws InvalidLoginException {
        CustomerService customerService = getService(token);
        List<Coupon> coupons = customerService.findAllByCustomerIdAndPriceLessThan(price);
        return coupons.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(coupons);
    }

    @GetMapping("/{token}/coupons/beforeEndDate")
    public ResponseEntity<List<Coupon>> getCouponsBeforeEndDate(@PathVariable String token,
                                                                @RequestParam(name = "date")
                                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                                        LocalDate date)
            throws InvalidLoginException, ConversionFailedException {
        CustomerService customerService = getService(token);
        List<Coupon> coupons = customerService.findCouponsBeforeEndDate(date);
        return coupons.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(coupons);
    }


    /**
     * For the next method I have debated what is the right way to map the function.
     * I decided that Patch mapping is the right way to 'update' the specific columns in the Coupon and Customer tables,
     * and I'm send the request without Json in the body.
     *
     * @param token
     * @param couponId
     * @return
     * @throws InvalidLoginException
     * @throws CouponAlreadyPurchasedException
     * @throws ZeroCouponAmountException
     * @throws CouponNotExistException
     */
    @PatchMapping("/{token}/purchaseCoupon/{couponId}")
    public ResponseEntity<Coupon> purchaseCoupon(@PathVariable String token, @PathVariable long couponId)
            throws InvalidLoginException, CouponAlreadyPurchasedException, ZeroCouponAmountException,
            CouponNotExistException {
        CustomerService customerService = getService(token);
        Coupon coupon = customerService.purchaseCoupon(couponId);
        return ResponseEntity.ok(coupon);
    }

    @PutMapping("/updateCustomer/{token}")
    public ResponseEntity<Customer> update(@PathVariable String token, @RequestBody Customer customer)
            throws InvalidLoginException, UpdateNotAllowedException {
        CustomerService customerService = getService(token);
        Customer updatedCustomer = customerService.update(customer);
        return ResponseEntity.ok(updatedCustomer);
    }


    /**
     * This function disconnecting a customer user. There is a check if the user has the right token of the specific type of user,
     * Then the token and the clientSession removed from the Tokens map.
     * The delete mapping is my preferred map to this function. Since GET is less secure and I don't post new ClientSession,
     * The DeleteMapping is the right way to delete token, although it's not from the DB.
     *
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
     *
     * @param token
     * @return
     * @throws InvalidLoginException
     */
    private CustomerService getService(String token) throws InvalidLoginException {
        ClientSession session = tokensMap.get(token);
        if (null == session) {
            throw new InvalidLoginException();
        }
        session.accessed();
        return (CustomerService) session.getAbsService();
    }

}

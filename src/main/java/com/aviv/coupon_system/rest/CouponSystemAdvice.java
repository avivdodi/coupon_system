package com.aviv.coupon_system.rest;

import com.aviv.coupon_system.data.model.CouponSystemErrorResponse;
import com.aviv.coupon_system.service.ex.*;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

/*Exception handler class. */

@ControllerAdvice(annotations = {RestController.class})
public class CouponSystemAdvice {

    @ExceptionHandler(InvalidLoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public CouponSystemErrorResponse handleUnauthorized(InvalidLoginException ex) {
        return CouponSystemErrorResponse.of(HttpStatus.UNAUTHORIZED, "Unauthorized user.");
    }

    @ExceptionHandler(CouponAlreadyPurchasedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ResponseBody
    public CouponSystemErrorResponse handlePurchase(CouponAlreadyPurchasedException ex) {
        return CouponSystemErrorResponse.of(HttpStatus.METHOD_NOT_ALLOWED,
                "Customer already has this coupon.");
    }

    @ExceptionHandler(ZeroCouponAmountException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ResponseBody
    public CouponSystemErrorResponse handleZeroAmount(ZeroCouponAmountException ex) {
        return CouponSystemErrorResponse.of(HttpStatus.METHOD_NOT_ALLOWED,
                "Coupon amount is empty.");
    }

    @ExceptionHandler(CouponAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ResponseBody
    public CouponSystemErrorResponse handleCouponExist(CouponAlreadyExistsException ex) {
        return CouponSystemErrorResponse.of(HttpStatus.METHOD_NOT_ALLOWED,
                "Coupon already exist in the company coupons.");
    }

    @ExceptionHandler(IllegalOperationException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ResponseBody
    public CouponSystemErrorResponse handleIllegals(IllegalOperationException ex) {
        return CouponSystemErrorResponse.of(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage());
    }

    @ExceptionHandler(CouponNotExistException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public CouponSystemErrorResponse handleCouponOwnership(CouponNotExistException ex) {
        return CouponSystemErrorResponse.of(HttpStatus.FORBIDDEN, "The coupon is not exist.");
    }

    @ExceptionHandler(UpdateNotAllowedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ResponseBody
    public CouponSystemErrorResponse handleUpdate(UpdateNotAllowedException ex) {
        return CouponSystemErrorResponse.of(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage());
    }

    @ExceptionHandler(CompanyNotExistException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ResponseBody
    public CouponSystemErrorResponse handleCompany(CompanyNotExistException ex) {
        return CouponSystemErrorResponse.of(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage());
    }

    @ExceptionHandler(CustomerNotExistException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ResponseBody
    public CouponSystemErrorResponse handleCustomer(CustomerNotExistException ex) {
        return CouponSystemErrorResponse.of(HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage());
    }

    @ExceptionHandler(ConversionFailedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public CouponSystemErrorResponse handleIllegalDates(ConversionFailedException ex) {
        return CouponSystemErrorResponse.of(HttpStatus.BAD_REQUEST, "Wrong date inserted.");
    }

    /**
     * This exception is very wide. I decided to handle it for a cases if a user with token of specific type will try to
     * access to controllers of other user types.
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(ClassCastException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public CouponSystemErrorResponse handleClassCast(ClassCastException ex) {
        return CouponSystemErrorResponse.of(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    /**
     * This exception is handled in the system because of the reason that there is a possibility which the controller got
     * requests with a Json with invalid dates.
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public CouponSystemErrorResponse handleJson(HttpMessageNotReadableException ex) {
        return CouponSystemErrorResponse.of(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
}

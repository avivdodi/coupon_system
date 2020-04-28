package com.aviv.coupon_system.rest.controller;

import com.aviv.coupon_system.data.model.Company;
import com.aviv.coupon_system.data.model.Coupon;
import com.aviv.coupon_system.data.model.Customer;
import com.aviv.coupon_system.rest.ClientSession;
import com.aviv.coupon_system.service.AdminService;
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

/*The following controller get all the requests from client admin and send the responses to the client type admin.*/
@CrossOrigin
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private Map<String, ClientSession> tokensMap;

    @Autowired
    public AdminController(@Qualifier("tokensMap") Map<String, ClientSession> tokensMap) {
        this.tokensMap = tokensMap;
    }

    @PostMapping("/createCompany/{token}")
    public ResponseEntity<Company> createCompany(@RequestBody Company company, @PathVariable String token)
            throws InvalidLoginException, IllegalOperationException {
        AdminService adminService = getService(token);
        Company addedCompany = adminService.createCompany(company);
        return ResponseEntity.ok(addedCompany);
    }

    @DeleteMapping("/{token}/removeCompany/{id}")
    public ResponseEntity<String> removeCompany(@PathVariable String token, @PathVariable long id)
            throws InvalidLoginException, CompanyNotExistException {
        AdminService adminService = getService(token);
        adminService.removeCompany(id);
        String msg = String.format("Company with id '%s' has been removed", id);
        return ResponseEntity.ok(msg);
    }

    @PutMapping("/updateCompany/{token}")
    public ResponseEntity<Company> updateCompany(@PathVariable String token, @RequestBody Company company)
            throws InvalidLoginException, CompanyNotExistException, UpdateNotAllowedException {
        AdminService adminService = getService(token);
        Company updateCompany = adminService.updateCompany(company);
        return ResponseEntity.ok(updateCompany);
    }

    @DeleteMapping("/{token}/removeCompany/all")
    public ResponseEntity<String> removeAllCompanies(@PathVariable String token) throws InvalidLoginException {
        AdminService adminService = getService(token);
        adminService.removeAllCompanies();
        return ResponseEntity.ok("All companies have been removed.");
    }

    @PostMapping("/createCustomer/{token}")
    public ResponseEntity<Customer> createCustomer(@PathVariable String token, @RequestBody Customer customer)
            throws InvalidLoginException, IllegalOperationException {
        AdminService adminService = getService(token);
        Customer newCustomer = adminService.createCustomer(customer);
        return ResponseEntity.ok(newCustomer);
    }

    @DeleteMapping("/{token}/removeCustomer/{id}")
    public ResponseEntity<String> removeCustomer(@PathVariable String token, @PathVariable long id)
            throws InvalidLoginException, CustomerNotExistException {
        AdminService adminService = getService(token);
        adminService.removeCustomer(id);
        String msg = String.format("Customer with id '%s' has been removed.", id);
        return ResponseEntity.ok(msg);

    }

    @PutMapping("/updateCustomer/{token}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable String token, @RequestBody Customer customer)
            throws InvalidLoginException, CustomerNotExistException, UpdateNotAllowedException {
        AdminService adminService = getService(token);
        Customer updateCustomer = adminService.updateCustomer(customer);
        return ResponseEntity.ok(updateCustomer);
    }

    @DeleteMapping("/{token}/removeCustomer/all")
    public ResponseEntity<String> removeAllCustomers(@PathVariable String token) throws InvalidLoginException {
        AdminService adminService = getService(token);
        adminService.removeAllCustomers();
        return ResponseEntity.ok("All customers have been removed.");

    }

    @GetMapping("/{token}/coupons/beforeEndDate")
    public ResponseEntity<List<Coupon>> getCouponsBeforeEndDate(@PathVariable String token,
                                                                @RequestParam(name = "date")
                                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                                        LocalDate date)
            throws InvalidLoginException, ConversionFailedException {
        AdminService adminService = getService(token);
        List<Coupon> coupons = adminService.findCouponsBeforeEndDate(date);
        return coupons.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(coupons);
    }

    @GetMapping("/{token}/coupons/empty")
    public ResponseEntity<List<Coupon>> getEmptyCoupons(@PathVariable String token) throws InvalidLoginException {
        AdminService adminService = getService(token);
        List<Coupon> emptyCoupons = adminService.findEmptyCoupons();
        return emptyCoupons.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(emptyCoupons);
    }

    @GetMapping("/{token}/getAllCompanies")
    public ResponseEntity<List<Company>> getAllCompanies(@PathVariable String token) throws InvalidLoginException {
        AdminService adminService = getService(token);
        List<Company> companies = adminService.findAllCompanies();
        return companies.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(companies);
    }

    @GetMapping("/{token}/getAllCustomers")
    public ResponseEntity<List<Customer>> getAllCustomers(@PathVariable String token) throws InvalidLoginException {
        AdminService adminService = getService(token);
        List<Customer> customers = adminService.findAllCustomers();
        return customers.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(customers);
    }

    /**
     * This function disconnecting a user. There is a check if the user has the right token of the specific type of user,
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
    public ResponseEntity<String> logOff(@PathVariable String token) throws InvalidLoginException,
            ClassCastException {
        getService(token);
        ClientSession clientSession = tokensMap.get(token);
        tokensMap.remove(token, clientSession);
        return ResponseEntity.ok(HttpStatus.OK.toString());
    }

    /**
     * The function below checks the token validity and refresh the relevant Client session.
     *
     * @param token
     * @return
     * @throws InvalidLoginException
     */
    private AdminService getService(String token) throws InvalidLoginException {
        ClientSession session = tokensMap.get(token);
        if (null == session) {
            throw new InvalidLoginException();
        }
        session.accessed();
        return (AdminService) session.getAbsService();
    }

}

package com.aviv.coupon_system.rest.controller;

import com.aviv.coupon_system.rest.ClientSession;
import com.aviv.coupon_system.service.LoginSystem;
import com.aviv.coupon_system.service.ex.InvalidLoginException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/*This Rest controller handles login requests and responses.*/
@CrossOrigin
@RestController
@RequestMapping("/api")
public class LoginController {

    private static final int TOKEN_LENGTH = 15;
    private Map<String, ClientSession> tokensMap;
    private LoginSystem loginSystem;

    @Autowired
    public LoginController(@Qualifier("tokensMap") Map<String, ClientSession> tokensMap, LoginSystem loginSystem) {
        this.tokensMap = tokensMap;
        this.loginSystem = loginSystem;
    }

    /**
     * Login into the DB.
     * Although the login function is not includes Json in the body, and there is no change in the DB,
     * I want to use Post mapping to create new ClientSession, and to improve the authentication in the server in future.
     * @param email
     * @param password
     * @return Returns the role number + token. The role number is for the Angular using.
     * @throws InvalidLoginException
     */
    @PostMapping("/users/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password)
            throws InvalidLoginException {

        ClientSession session = loginSystem.login(email, password);
        if (tokensMap != null) {
            ResponseEntity<String> tokenFromChecker = loginSystem.tokenChecker(session, tokensMap);
            if (tokenFromChecker != null) {
                return tokenFromChecker;
            }
        }
        int role = session.getAbsService().getRole();
        String token = role+generateToken();
        tokensMap.put(token, session);
        return ResponseEntity.ok(token);
    }

    private static String generateToken() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, TOKEN_LENGTH);
    }
}

package com.aviv.coupon_system.service;

import com.aviv.coupon_system.data.db.UserRepository;
import com.aviv.coupon_system.data.model.Client;
import com.aviv.coupon_system.data.model.Company;
import com.aviv.coupon_system.data.model.Customer;
import com.aviv.coupon_system.data.model.User;
import com.aviv.coupon_system.rest.ClientSession;
import com.aviv.coupon_system.service.ex.InvalidLoginException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

/*This Service acts as login system and processing login request from the login controller by using the UserRepository.*/

@Service
public class LoginSystem {
    private ApplicationContext context;
    private UserRepository userRepository;


    @Autowired
    public LoginSystem(ApplicationContext context, UserRepository userRepository) {
        this.context = context;
        this.userRepository = userRepository;
    }

    /**
     * A function that get user email and password, and check if it's appear in the User db table.
     * If so it's find the type of this user and sent the class to getSessionWith.
     *
     * @param email
     * @param password
     * @return ClientSession
     * @throws InvalidLoginException
     */
    public ClientSession login(String email, String password) throws InvalidLoginException {
        User user = getUserByEmailAndPassword(email, password);
        Client client = user.getClient();
        Class<? extends AbsService> serviceType;

        if (client instanceof Company) {
            serviceType = CompanyServiceImpl.class;
        } else if (client instanceof Customer) {
            serviceType = CustomerServiceImpl.class;
        } else {
            serviceType = AdminServiceImpl.class;
        }
        return getSessionWith(serviceType, client.getId());
    }

    /**
     * Request the repository to search for matching between email and password.
     *
     * @param email
     * @param password
     * @return User
     * @throws InvalidLoginException
     */
    public User getUserByEmailAndPassword(String email, String password) throws InvalidLoginException {
        Optional<User> user = userRepository.findByEmailAndPassword(email, password);
        if (!user.isPresent()) {
            throw new InvalidLoginException();
        }
        return user.get();

    }

    /**
     * The function Get the id of login user and class type, and make a specific clientSession.
     *
     * @param serviceClass
     * @param clientId
     * @return clientSession
     */
    public ClientSession getSessionWith(Class<? extends AbsService> serviceClass, long clientId) {
        AbsService service = context.getBean(serviceClass);
        ClientSession clientSession = context.getBean(ClientSession.class);
        service.setId(clientId);
        clientSession.setAbsService(service);
        clientSession.accessed();
        return clientSession;
    }

    /**
     * The checker search for a specific Client session in the tokens map.
     * If so, it return the key (Token) for this Client session from the tokens map.
     *
     * @param clientSession
     * @param tokensMap
     * @return
     */
    public ResponseEntity<String> tokenChecker(ClientSession clientSession, Map<String, ClientSession> tokensMap) {

        for (Map.Entry<String, ClientSession> entry : tokensMap.entrySet()) {
            if (clientSession.getAbsService().getId() == entry.getValue().getAbsService().getId() &&
                    clientSession.getAbsService().getRole() == entry.getValue().getAbsService().getRole()) {
                entry.getValue().accessed();
                return ResponseEntity.ok(entry.getKey());
            }
        }
        return null;
    }
}

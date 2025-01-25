package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/id/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {

        Optional<User> findById = userRepository.findById(id);
        
        if (!findById.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(userRepository.findById(id).get());
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> findByUserName(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            log.warn("User is NOT found by username: {}", username);
            return ResponseEntity.notFound().build();
        }
        log.info("User found by username: {}", username);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {

        String username = createUserRequest.getUsername();
        String password = createUserRequest.getPassword();
        String confirmPassword = createUserRequest.getConfirmPassword();

        if (username == null || password == null || confirmPassword == null) {

            log.error("Username, password and confirmPassword all must be provided - Username={} "
                    + "password={} confirmPassword={}", username, password, confirmPassword);
            
            return ResponseEntity.badRequest().build();
        }
        
        Cart cart = new Cart();
        cartRepository.save(cart);
        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        user.setCart(cart);

        if (createUserRequest.getPassword().length() < 7
                || !createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {

            log.warn("User create failed, either password length is less than 7"
                    + " or mismatched confirm password - password:{}  confirm password: {}",
                    createUserRequest.getPassword(), createUserRequest.getConfirmPassword());

            return ResponseEntity.badRequest().build();
        }

        User findByUsername = userRepository.findByUsername(createUserRequest.getUsername().trim());

        /**
         * Check if username is unique
         */
        if (findByUsername != null) {

            log.error("Username must be unique - username {} already exists", createUserRequest.getUsername());
            return ResponseEntity.badRequest().build();
        }

        user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
        userRepository.save(user);

        log.info("User created successfully username: {}   id: {}", user.getUsername(), user.getId());

        return ResponseEntity.ok(user);
    }
}

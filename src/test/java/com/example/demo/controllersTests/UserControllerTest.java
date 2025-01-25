package com.example.demo.controllersTests;

import com.example.demo.TestUtils;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

public class UserControllerTest {

    private UserController userController;

    private UserRepository userRepository= Mockito.mock(UserRepository.class);

    private CartRepository cartRepository= Mockito.mock(CartRepository.class);

    private BCryptPasswordEncoder bCryptPasswordEncoder= Mockito.mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        
        userController = new UserController();
        
        /**
         * Inject mocked dependencies into 'userController' object using TestUtils.injectObject 
         */
        TestUtils.injectObject(userController, "userRepository", userRepository);
        TestUtils.injectObject(userController, "cartRepository", cartRepository);
        TestUtils.injectObject(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }


    @Test
    public void test_Successfull_Create_User() {
        
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("username");
        request.setPassword("password");
        request.setConfirmPassword("password");
        
        String encodedPass = "hashed password generated";
        Mockito.when(bCryptPasswordEncoder.encode("password")).thenReturn(encodedPass);

        ResponseEntity<User> response = userController.createUser(request);
        User user = response.getBody();

        Assert.assertNotNull(response);
        Assert.assertNotNull(user);
        Assert.assertEquals(request.getUsername(), user.getUsername());
        Assert.assertEquals(encodedPass, user.getPassword());
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void test_Successfull_Find_By_Id() {
        
        User user = new User();
        user.setId(1L);
        user.setUsername("kian");
        user.setPassword("abcdefg");

        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        ResponseEntity<User> response = userController.findById(user.getId());

        Assert.assertNotNull(response);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(user, response.getBody());
    }

    @Test
    public void test_Successfull_Find_By_UserName() {
       
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        
        Mockito.when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
        ResponseEntity<User> response = userController.findByUserName(user.getUsername());
        
        Assert.assertNotNull(response);
        Assert.assertEquals(user, response.getBody());
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void test_Failed_Find_By_UserName() {
        /**
         * Since the behavior of userRepository.findByUsername(username) is undefined on the mocked userRepository
         * object, this method invocation returns null at the time it gets invoked inside the method findByUserName
         * of userController class
         */
        
        ResponseEntity<User> response = userController.findByUserName("username");
        
        Assert.assertNotNull(response);
        Assert.assertNull(response.getBody()); 
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
       
    }

    @Test
    public void test_Failed_Find_By_ID() {
        /**
         * Since the behavior of userRepository.findById(id) is
         * undefined on the mocked userRepository object, this method invocation
         * returns null at the time it gets invoked inside method
         * findById of userController class
         */

        ResponseEntity<User> response = userController.findById(1L);

        Assert.assertNotNull(response);
        Assert.assertNull(response.getBody());
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }

    @Test
    public void test_Invalid_Password_Length_Or_Unmatched_ConfirmPassword_For_CreateUser() {
        
        /********************************
         * Password length in less than 7
         ********************************/
        CreateUserRequest request = new CreateUserRequest();
        String password = "654321";
        request.setUsername("kian");
        request.setPassword(password);
        request.setConfirmPassword(password);
        ResponseEntity<User> response = userController.createUser(request);
        
        Assert.assertNotNull(response);
        Assert.assertNull(response.getBody());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
         /********************************************
         * ConfirmPassword does not match the password
         *********************************************/
        CreateUserRequest request2 = new CreateUserRequest();
        String password2 = "87654321";
        request2.setUsername("kian");
        request2.setPassword(password2);
        request2.setConfirmPassword("987654321");
        ResponseEntity<User> response2 = userController.createUser(request2);
        
        Assert.assertNull(response.getBody());
        Assert.assertNotNull(response2);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());

    }
    
    @Test
    public void test_Invalid_UserName_Not_Unique() {
        
        /**
         * Assume a user with username 'username' already exists in the database
         * so userRepository.findByUsername("username") returns a non-null value
         */
        String username = "username";
        
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(username);
        request.setPassword("password");
        request.setConfirmPassword("password");
        
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setPassword("password");

        Mockito.when(userRepository.findByUsername(username)).thenReturn(user);
        ResponseEntity<User> response = userController.createUser(request);

        Assert.assertNull(response.getBody());
        Assert.assertNotNull(response);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}


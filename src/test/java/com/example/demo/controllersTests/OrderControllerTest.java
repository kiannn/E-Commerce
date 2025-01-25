package com.example.demo.controllersTests;

import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import java.util.List;
import org.junit.Assert;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public class OrderControllerTest {

    private OrderController orderController;

    private OrderRepository orderRepository = Mockito.mock(OrderRepository.class);

    private UserRepository userRepository= Mockito.mock(UserRepository.class);

    public OrderControllerTest() {
        System.out.println("com.example.demo.controllersTests.OrderControllerTest.<init>()");
    }

    @Before
    public void setUp() {

        System.out.println(orderRepository+ "    "+userRepository);
        /**
         * Inject mocked dependencies into 'orderController' objects
         */
        orderController = new OrderController(userRepository, orderRepository);
    }

    @Test
    public void test_Successfull_Submit() throws Exception {

        User user = new User();
        user.setId(1L);
        
        final Item item = new Item();
        item.setDescription(" widget that is round");
        item.setId(1L);
        item.setName("Round Widget");
        item.setPrice(new BigDecimal(2.99));

        final Item item1 = new Item();
        item1.setDescription("  widget that is square");
        item1.setId(2L);
        item1.setName("Square Widget");
        item1.setPrice(new BigDecimal(1.99));
        
        Cart cart = new Cart();
        cart.addItem(item);
        cart.addItem(item1);
        cart.setUser(user);
        
        user.setCart(cart);

        String userName = "kian";
        Mockito.when(userRepository.findByUsername(userName)).thenReturn(user);

        ResponseEntity<UserOrder> ordersForUser = orderController.submit(userName);
        
        final UserOrder body = ordersForUser.getBody();
        
        /***
         * Verify response body, which represents the user order, contains the expected 
         * User object, total and items values to be the same as the ones populated in 'cart' object. 
        ***/
        Assert.assertNotNull(body); 
        Assert.assertEquals(body.getUser(), user);
        Assert.assertEquals(body.getTotal(), cart.getTotal());
        Assert.assertEquals(body.getItems(), cart.getItems());
        
        Assert.assertEquals(ordersForUser.getStatusCode(), HttpStatus.OK);

    }

    @Test
    public void test_Successfull_Get_Order_For_User() throws Exception {

        String userName = "kian";

        User user = new User();
        user.setId(1L);
        user.setUsername(userName);

        final Item item = new Item();
        item.setDescription(" widget that is round");
        item.setId(1L);
        item.setName("Round Widget");
        item.setPrice(new BigDecimal(2.99));

        final Item item1 = new Item();
        item1.setDescription("  widget that is square");
        item1.setId(2L);
        item1.setName("Square Widget");
        item1.setPrice(new BigDecimal(1.99));

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.addItem(item);
        cart.addItem(item1);

        user.setCart(cart);

        UserOrder userOrder = new UserOrder();
        userOrder.setId(1L);
        userOrder.setUser(user);

        Mockito.when(userRepository.findByUsername(userName)).thenReturn(user);
        Mockito.when(orderRepository.findByUser(user)).thenReturn(List.of(userOrder));

        final ResponseEntity<List<UserOrder>> ordersForUser = orderController.getOrdersForUser(userName);

        final List<UserOrder> body = ordersForUser.getBody(); 
        
        Assert.assertEquals(ordersForUser.getStatusCode(), HttpStatus.OK);
        
        /** 
         * Verify response body, which represents user list of orders, is the same as 'userOrder' object
        **/
        Assert.assertNotNull(body); 
        Assert.assertEquals(body, List.of(userOrder));
        
//        ordersForUser.getBody().forEach(a -> System.out.println("->  " + a));

    }

    @Test
    public void test_NotFount_UserName_Submit() throws Exception {

        /**
         * The behavior of userRepository.findByUsername method is not defined
         * on the mocked userRepository object hence it returns null at the time of
         * invocation
         */
        final ResponseEntity<UserOrder> submit = orderController.submit("username");

        Assert.assertEquals(submit.getStatusCode(), HttpStatus.NOT_FOUND);
        Assert.assertEquals(submit.getBody(), null);

    }

    @Test
    public void test_Failed_Get_Order_For_User() throws Exception {
        /**
         * The behavior of userRepository.findByUsername method is not defined
         * on the mocked userRepository object hence it returns null at the time of
         * invocation
         */
        final ResponseEntity<List<UserOrder>> ordersForUser = orderController.getOrdersForUser("kian");

        Assert.assertEquals(ordersForUser.getStatusCode(), HttpStatus.NOT_FOUND);
        Assert.assertNull(ordersForUser.getBody());

    }

}

package com.example.demo.controllersTests;

import com.example.demo.TestUtils;
import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CartControllerTest {

    private CartController cartController;

    private UserRepository userRepository = Mockito.mock(UserRepository.class);

    private ItemRepository itemRepository = Mockito.mock(ItemRepository.class);

    private CartRepository cartRepository = Mockito.mock(CartRepository.class);

    @Before
    public void setUp() {

        cartController = new CartController();
        TestUtils.injectObject(cartController, "userRepository", userRepository);
        TestUtils.injectObject(cartController, "itemRepository", itemRepository);
        TestUtils.injectObject(cartController, "cartRepository", cartRepository);

    }

    @Test
    public void test_Success_Add_To_Cart() {

        /**
         * Create a request to modify a user's card by adding an item with
         * quantities of two
         */
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("user");
        request.setItemId(1L);
        request.setQuantity(2);

        Item item = new Item();
        item.setId(1L);
        item.setName("Round Widget");
        item.setDescription("A widget that is round");
        item.setPrice(new BigDecimal(2.99));

        Cart cart = new Cart();
        cart.setId(1L);

        User user = new User();
        user.setCart(cart);

        cart.setUser(user);

        Mockito.when(userRepository.findByUsername(request.getUsername())).thenReturn(user);
        Mockito.when(itemRepository.findById(request.getItemId())).thenReturn(Optional.of(item));

        ResponseEntity<Cart> response = cartController.addTocart(request);

        Assert.assertNotNull(response);

        Cart body = response.getBody();

        /**
         * Expected values for list of items, totals, user, and cart id after
         * adding the items to card derived from 'request' object
         */
        List<Item> expectedListOfItems = Collections.nCopies(request.getQuantity(), item);
        BigDecimal expectedTotal = item.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
        Long expectedCartId = cart.getId();
        User expectedUser = user;

        /**
         * Verify expected values match the the ones coming from the response
         * body
         */
        Assert.assertEquals(body.getTotal(), expectedTotal);
        Assert.assertEquals(body.getItems(), expectedListOfItems);
        Assert.assertEquals(body.getId(), expectedCartId);
        Assert.assertEquals(body.getUser(), expectedUser);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void test_Success_Remove_From_Cart() {

        /**
         * Create a request to modify a user's card by adding an item with
         * quantities of three
         */
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("user");
        request.setItemId(1L);
        request.setQuantity(3);

        Item item = new Item();
        item.setId(1L);
        item.setName("Round Widget");
        item.setDescription("A widget that is round");
        item.setPrice(new BigDecimal(2.99));

        Cart cart = new Cart();
        cart.setId(1L);
        for (byte i = 0; i < request.getQuantity(); i++) {
            cart.addItem(item); // adding three quantities of the item (id = 1) to the list of itemes
        }

        User user = new User();
        user.setCart(cart);

        cart.setUser(user);

        Mockito.when(userRepository.findByUsername(request.getUsername())).thenReturn(user);
        Mockito.when(itemRepository.findById(request.getItemId())).thenReturn(Optional.of(item));

        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        Assert.assertNotNull(response);

        Cart body = response.getBody();

        /**
         * Expected values for list of items, price total, user, and cart id
         * after removing the items from card
         */
        List<Item> expectedListOfItems = List.of(); //a list containing zero elements 
        BigDecimal expectedTotal = BigDecimal.ZERO; // price total becomes zero after removing the itemes
        Long expectedCartId = cart.getId();
        User expectedUser = user;

        /**
         * Verify expected values match the the ones coming from the response
         * body
         */
        Assert.assertEquals(body.getTotal().stripTrailingZeros(), expectedTotal);
        Assert.assertEquals(body.getItems(), expectedListOfItems);
        Assert.assertEquals(body.getId(), expectedCartId);
        Assert.assertEquals(body.getUser(), expectedUser);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    public void test_Failed_Add_To_Cart_Invalid_Username() {

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("user");

        /**
         * Since the behavior of userRepository.findByUsername is undefined on
         * the mocked userRepository object, this method invocation returns null
         * at the time it gets invoked inside the method addTocart of
         * cartController class
         *
         */
        ResponseEntity<Cart> response = cartController.addTocart(request);

        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assert.assertNotNull(response);
        Assert.assertNull(response.getBody());
    }

    @Test
    public void test_Failed_Add_To_Cart_Invalid_ItemId() {

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("user");
        request.setItemId(1L);
        request.setQuantity(2);

        Cart cart = new Cart();
        cart.setId(1L);

        User user = new User();
        user.setCart(cart);

        cart.setUser(user);

        /**
         * Since the behavior of itemRepository.findById is undefined on the
         * mocked itemRepository object, this method invocation returns null at
         * the time it gets invoked inside the method addTocart of
         * cartController class. Only the behavior of
         * userRepository.findByUsername is defined.
        */
        Mockito.when(userRepository.findByUsername(request.getUsername())).thenReturn(user);

        ResponseEntity<Cart> response = cartController.addTocart(request);

        Assert.assertNotNull(response);
        Assert.assertNull(response.getBody());
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void test_Failed_Remove_From_Cart_Invalid_Username() {

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("user");
        request.setItemId(1L);
        request.setQuantity(1);

        /**
         * Since the behavior of userRepository.findByUsername is undefined on
         * the mocked userRepository object, this method invocation returns null
         * at the time it gets invoked inside the method removeFromcart of
         * cartController class
         */
        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assert.assertNotNull(response);
        Assert.assertNull(response.getBody());
    }

    @Test
    public void test_Failed_Remove_From_Cart_Invalid_ItemId() {

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("user");
        request.setItemId(1L);
        request.setQuantity(2);

        Cart cart = new Cart();
        cart.setId(1L);

        User user = new User();
        user.setCart(cart);

        cart.setUser(user);

        /**
         * Since the behavior of itemRepository.findById is undefined on the
         * mocked itemRepository object, this method invocation returns null at
         * the time it gets invoked inside the method removeFromcart of
         * cartController class. Only the behavior of
         * userRepository.findByUsername is defined.
         */
        Mockito.when(userRepository.findByUsername(request.getUsername())).thenReturn(user);

        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        Assert.assertNotNull(response);
        Assert.assertNull(response.getBody());
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}

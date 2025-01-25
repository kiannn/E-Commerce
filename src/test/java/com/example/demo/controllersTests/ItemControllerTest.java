package com.example.demo.controllersTests;

import com.example.demo.TestUtils;
import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public class ItemControllerTest {

    private ItemController itemController;

    private ItemRepository itemRepository = Mockito.mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();

        TestUtils.injectObject(itemController, "itemRepository", itemRepository);

    }

    @Test
    public void test_Get_Items_By_Name() throws Exception {

        List<Item> items = new ArrayList<>();

        /**
         * Create two items with different ids, and the same descriptions and names
         */
        for (byte i = 1; i <= 2; i++) {

            final Item item = new Item();
            item.setDescription(" widget that is round");
            item.setId((long) i);
            item.setName("Round Widget");
            item.setPrice(new BigDecimal(2.99));

            items.add(item);
        }

        final Item item1 = new Item();
        item1.setDescription("  widget that is round");
        item1.setId(2L);
        item1.setName("Round Widget");
        item1.setPrice(new BigDecimal(1.99));

        Mockito.when(itemRepository.findByName("Round Widget")).thenReturn(items);

        ResponseEntity<List<Item>> itemsByName = itemController.getItemsByName("Round Widget");
        final List<Item> body = itemsByName.getBody();

        Assert.assertEquals(itemsByName.getStatusCode(), HttpStatus.OK);
        Assert.assertNotNull(body);
        Assert.assertTrue(!body.isEmpty());
        /**
         * Verify the body returned from response is the same as 'items' as the expected 
         */
        Assert.assertEquals(itemsByName.getBody(), items); 

    }

    @Test
    public void test_Get_Item_ById() throws Exception {

        final Item item = new Item();
        item.setDescription(" widget that is round");
        item.setId(1L);
        item.setName("Round Widget");
        item.setPrice(new BigDecimal(2.99));

        Mockito.when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        final ResponseEntity<Item> itemById = itemController.getItemById(item.getId());

        Assert.assertEquals(itemById.getStatusCode(), HttpStatus.OK);
        Assert.assertNotNull(itemById.getBody());
        Assert.assertEquals(itemById.getBody(), item);

    }

    @Test
    public void test_Get_All_Items() throws Exception {

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

        List<Item> items = List.of(item1, item);

        Mockito.when(itemRepository.findAll()).thenReturn(items);

        final ResponseEntity<List<Item>> allItems = itemController.getItems();

        Assert.assertEquals(allItems.getStatusCode(), HttpStatus.OK);
        Assert.assertNotNull(allItems.getBody());
        Assert.assertEquals(allItems.getBody(), items);

    }

    @Test
    public void test_NotFound_Get_Items_By_Name() throws Exception {

        /**
         * The behavior of itemRepository.findByName method is not defined
         * on the mocked itemRepository object hence it returns null at the time of
         * invocation
        */
        final ResponseEntity<List<Item>> itemsByName = itemController.getItemsByName("invalid item Name");
        final List<Item> body = itemsByName.getBody();

        Assert.assertNotNull(itemsByName);
        Assert.assertNull(body);
        Assert.assertEquals(itemsByName.getStatusCode(), HttpStatus.NOT_FOUND);

    }
    
    @Test
    public void test_NotFound_Get_Items_By_ID() throws Exception {

        /**
         * The behavior of itemRepository.findById(id) method is not defined
         * on the mocked itemRepository object hence it returns null at the time of
         * invocation as if the item does not exist at all.
        */
        final ResponseEntity<Item> itemsByName = itemController.getItemById(3L);
        final Item body = itemsByName.getBody();

        Assert.assertNotNull(itemsByName);
        Assert.assertNull(body);
        Assert.assertEquals(itemsByName.getStatusCode(), HttpStatus.NOT_FOUND);

    }
}

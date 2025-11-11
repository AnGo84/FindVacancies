package ua.findvacancies.common.store.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ua.findvacancies.common.store.DataStore;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("SetBasedDataService Tests")
class SetBasedDataStoreTest {

    private DataStore<String> service;

    /**
     * Set up a new instance of the service before each test.
     */
    @BeforeEach
    void setUp() {
        service = new SetBasedDataStore<>();
    }

    @Test
    @DisplayName("Should add a single new item successfully")
    void shouldAddSingleItemSuccessfully() {
        // Given
        String item = "testItem";

        // When
        boolean result = service.add(item);

        // Then
        assertTrue(result, "The first add should return true");
        assertEquals(1, service.getAll().size(), "Size should be 1 after adding one item");
    }

    @Test
    @DisplayName("Should not add a duplicate item")
    void shouldNotAddDuplicateItem() {
        // Given
        String item = "testItem";

        // When
        service.add(item);

        // Then
        assertFalse(service.add(item), "Adding a duplicate should return false");
        assertEquals(1, service.getAll().size(), "Size should remain 1");
    }

    @Test
    @DisplayName("Should add all items from a collection")
    void shouldAddAllItemsFromCollection() {
        // Given
        Collection<String> itemsToAdd = Arrays.asList("item1", "item2", "item3");

        // When
        boolean result = service.addAll(itemsToAdd);

        // Then
        assertTrue(result, "addAll should return true if items are added");
        assertEquals(3, service.getAll().size(), "Size should be 3 after adding three items");
    }

    @Test
    @DisplayName("Should handle adding a collection with duplicates")
    void shouldHandleCollectionWithDuplicates() {
        // Given
        String item = "item1";
        service.add(item);
        Collection<String> itemsToAdd = Arrays.asList(item, "item2", "item3");

        // When
        boolean result = service.addAll(itemsToAdd);

        // Then
        assertTrue(result, "addAll should return true as new items are added");
        assertEquals(3, service.getAll().size(), "Size should be 3, as duplicates are ignored");
    }

    @Test
    @DisplayName("Should add all items from an empty collection")
    void shouldHandleEmptyCollection() {
        assertFalse(service.addAll(Collections.emptyList()), "addAll should return false for an empty collection");
        assertEquals(0, service.getAll().size(), "Size should be 0");
    }

    @Test
    @DisplayName("Should clear all items from the data store")
    void shouldClearAllItems() {
        // Given
        service.add("item1");
        service.add("item2");
        assertFalse(service.getAll().isEmpty(), "Service should not be empty before clearing");

        // When
        service.clear();

        // Then
        assertTrue(service.getAll().isEmpty(), "Service should be empty after clearing");
    }

    @Test
    @DisplayName("Should return an unmodifiable collection of all items")
    void shouldReturnUnmodifiableCollection() {
        // Given
        service.add("item1");

        // When
        Collection<String> allItems = service.getAll();

        // Then
        assertInstanceOf(Set.class, allItems, "Returned collection should not be the internal Set instance");

        assertThrows(UnsupportedOperationException.class, () -> allItems.add("new item"),
                "Attempting to add to the returned collection should throw UnsupportedOperationException");
    }

    @Test
    @DisplayName("Should return an empty collection when the data store is empty")
    void shouldReturnEmptyCollectionWhenEmpty() {
        // When
        Collection<String> allItems = service.getAll();

        // Then
        assertTrue(allItems.isEmpty(), "Returned collection should be empty");
    }

}

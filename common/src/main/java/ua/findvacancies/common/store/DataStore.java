package ua.findvacancies.common.store;


import java.util.Collection;

/**
 * A generic interface for a data store service.
 *
 * @param <T> The type of objects to be stored in the data store.
 */
public interface DataStore<T> {

    /**
     * Adds a single item to the data store.
     *
     * @param item The item to add.
     * @return true if the item was added, false if it already exists.
     */
    boolean add(T item);

    /**
     * Adds all items from a collection to the data store.
     *
     * @param items The collection of items to add.
     * @return true if any items were added, false if the store remains unchanged.
     */
    boolean addAll(Collection<T> items);

    /**
     * Removes all items from the data store.
     */
    void clear();

    /**
     * Retrieves all items currently in the data store.
     *
     * @return A collection of all items.
     */
    Collection<T> getAll();
}

package ua.findvacancies.common.store.impl;

import ua.findvacancies.common.store.DataStore;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * An implementation of the DataStore interface that uses a Set
 * to store and manage data. This ensures that all stored items are unique.
 *
 * @param <T> The type of objects to be stored.
 */
public class SetBasedDataStore<T> implements DataStore<T> {

    private final Set<T> data = new HashSet<>();

    /**
     * Adds a single item to the Set.
     * The Set's `add` method automatically handles uniqueness.
     *
     * @param item The item to add.
     * @return true if the item was added (i.e., it was not already present), false otherwise.
     */
    @Override
    public boolean add(T item) {
        return data.add(item);
    }

    /**
     * Adds all items from a collection to the Set.
     *
     * @param items The collection of items to add.
     * @return true if the set was changed as a result of the call.
     */
    @Override
    public boolean addAll(Collection<T> items) {
        return data.addAll(items);
    }

    /**
     * Removes all items from the Set.
     */
    @Override
    public void clear() {
        data.clear();
    }

    /**
     * Returns an unmodifiable view of the data Set.
     *
     * @return A collection containing all stored items.
     */
    @Override
    public Collection<T> getAll() {
        return Collections.unmodifiableSet(data);
    }

}

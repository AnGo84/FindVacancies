package ua.findvacancies.mappers;

@FunctionalInterface
public interface AppObjectMapper<T, E> {
    E convert(T object);
}


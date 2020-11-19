package ua.findvacancies.mappers;

@FunctionalInterface
public interface ObjectMapper<T, E> {
    E convert(T object);
}


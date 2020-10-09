package ua.findvacancies.mvc.mappers;

@FunctionalInterface
public interface ObjectMapper<T, E> {
    E convert(T object);
}


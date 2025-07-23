package ru.otus.jdbc.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import ru.otus.jdbc.mapper.annotation.Id;
import ru.otus.jdbc.mapper.exception.JdbcMapperException;

@SuppressWarnings("java:S3011")
public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {

    private final String simpleName;

    private final Constructor<T> constructor;

    private final Field idField;

    private final List<Field> allFields;

    private final List<Field> fieldsWithoutId;

    public EntityClassMetaDataImpl(Class<T> clazz) {
        simpleName = clazz.getSimpleName();

        try {
            constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new JdbcMapperException(
                    "Entity class '%s' doesn't have no-arg constructor".formatted(clazz.getName()), e);
        }

        allFields = Arrays.asList(clazz.getDeclaredFields());
        for (Field fIeld : allFields) {
            fIeld.setAccessible(true);
        }

        idField = allFields.stream()
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new JdbcMapperException("Entity class '%s' doesn't have field annotated with '%s'"
                        .formatted(clazz.getName(), Id.class.getName())));

        fieldsWithoutId = allFields.stream()
                .filter(field -> !field.isAnnotationPresent(Id.class))
                .toList();
    }

    @Override
    public String getName() {
        return simpleName;
    }

    @Override
    public Constructor<T> getConstructor() {
        return constructor;
    }

    @Override
    public Field getIdField() {
        return idField;
    }

    @Override
    public List<Field> getAllFields() {
        return allFields;
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        return fieldsWithoutId;
    }
}

package ru.otus.jdbc.mapper;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

public class EntitySQLMetaDataImpl implements EntitySQLMetaData {

    private final EntityClassMetaData<?> entityClassMetaData;

    public EntitySQLMetaDataImpl(EntityClassMetaData<?> entityClassMetaData) {
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public String getSelectAllSql() {
        List<String> fieldNames =
                entityClassMetaData.getAllFields().stream().map(Field::getName).toList();
        return "select %s from %s".formatted(String.join(",", fieldNames), entityClassMetaData.getName());
    }

    @Override
    public String getSelectByIdSql() {
        List<String> fieldNamesWithoutId = entityClassMetaData.getFieldsWithoutId().stream()
                .map(Field::getName)
                .toList();
        return "select %s from %s where id = ?"
                .formatted(String.join(",", fieldNamesWithoutId), entityClassMetaData.getName());
    }

    @Override
    public String getInsertSql() {
        List<String> fieldNamesWithoutId = entityClassMetaData.getFieldsWithoutId().stream()
                .map(Field::getName)
                .toList();
        return "insert into %s (%s) values (%s)"
                .formatted(
                        entityClassMetaData.getName(),
                        String.join(",", fieldNamesWithoutId),
                        String.join(",", Collections.nCopies(fieldNamesWithoutId.size(), "?")));
    }

    @Override
    public String getUpdateSql() {
        List<String> updateFieldNames = entityClassMetaData.getFieldsWithoutId().stream()
                .map(field -> field.getName() + " = ?")
                .toList();
        return "update %s set %s where id = ?"
                .formatted(entityClassMetaData.getName(), String.join(",", updateFieldNames));
    }
}

package ru.otus.jdbc.mapper;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.DataTemplateException;
import ru.otus.core.repository.executor.DbExecutor;

/** Сохратяет объект в базу, читает объект из базы */
@SuppressWarnings("java:S3011")
public class DataTemplateJdbc<T> implements DataTemplate<T> {

    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData entitySQLMetaData;
    private final EntityClassMetaData<T> entityClassMetaData;

    public DataTemplateJdbc(
            DbExecutor dbExecutor, EntitySQLMetaData entitySQLMetaData, EntityClassMetaData<T> entityClassMetaData) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        return dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectByIdSql(), List.of(id), resultSet -> {
            try {
                if (resultSet.next()) {
                    T object = entityClassMetaData.getConstructor().newInstance();
                    for (Field field : entityClassMetaData.getFieldsWithoutId()) {
                        field.set(object, resultSet.getObject(field.getName()));
                    }
                    var idField = entityClassMetaData.getIdField();
                    idField.setAccessible(true);
                    idField.set(object, id);
                    return object;
                }
                return null;
            } catch (Exception e) {
                throw new DataTemplateException(e);
            }
        });
    }

    @Override
    public List<T> findAll(Connection connection) {
        return dbExecutor
                .executeSelect(connection, entitySQLMetaData.getSelectAllSql(), Collections.emptyList(), resultSet -> {
                    try {
                        List<T> resultList = new ArrayList<>();
                        while (resultSet.next()) {
                            T object = entityClassMetaData.getConstructor().newInstance();
                            for (Field field : entityClassMetaData.getAllFields()) {
                                field.set(object, resultSet.getObject(field.getName()));
                            }
                            resultList.add(object);
                        }
                        return resultList;
                    } catch (Exception e) {
                        throw new DataTemplateException(e);
                    }
                })
                .orElseThrow(() -> new RuntimeException("Unexpected error"));
    }

    @Override
    public long insert(Connection connection, T entity) {
        try {
            List<Field> fields = entityClassMetaData.getFieldsWithoutId();
            List<Object> values = new ArrayList<>();
            for (Field field : fields) {
                var value = field.get(entity);
                values.add(value);
            }
            return dbExecutor.executeStatement(connection, entitySQLMetaData.getInsertSql(), values);
        } catch (IllegalAccessException e) {
            throw new DataTemplateException(e);
        }
    }

    @Override
    public void update(Connection connection, T entity) {
        try {
            List<Field> fields = entityClassMetaData.getFieldsWithoutId();
            List<Object> values = new ArrayList<>();
            for (Field field : fields) {
                var value = field.get(entity);
                values.add(value);
            }
            values.add(entityClassMetaData.getIdField().get(entity));
            dbExecutor.executeStatement(connection, entitySQLMetaData.getUpdateSql(), values);
        } catch (IllegalAccessException e) {
            throw new DataTemplateException(e);
        }
    }
}

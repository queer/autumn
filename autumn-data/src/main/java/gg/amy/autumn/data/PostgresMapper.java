package gg.amy.autumn.data;

import gg.amy.autumn.data.annotation.PrimaryKey;
import gg.amy.autumn.data.annotation.Table;
import gg.amy.autumn.json.Json;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * @author amy
 * @since 5/8/21.
 */
public class PostgresMapper<T> {
    private final Class<T> type;
    private final Database database;
    private Table table;
    private PrimaryKey primaryKey;
    private Field pkField;

    PostgresMapper(final Class<T> type, final Database database) {
        this.type = type;
        this.database = database;
    }

    PostgresMapper<T> init() {
        if(!type.isAnnotationPresent(Table.class)) {
            throw new IllegalStateException("Class " + type + " must have @Table on it to be a valid entity.");
        }

        table = type.getDeclaredAnnotation(Table.class);

        // Scan the class for a primary key
        pkField = null;
        var pkCount = 0;
        for(final var field : type.getDeclaredFields()) {
            field.setAccessible(true);
            if(field.isAnnotationPresent(PrimaryKey.class)) {
                pkField = field;
                ++pkCount;
            }
        }
        if(pkCount != 1) {
            throw new IllegalStateException("Class " + type + " must have exactly one @PrimaryKey on a field to be a valid entity.");
        }

        final String pkSqlType = typeToSqlType(pkField.getType());
        primaryKey = pkField.getDeclaredAnnotation(PrimaryKey.class);
        pkField.setAccessible(true);

        database.sql(String.format("""
                CREATE TABLE
                    IF NOT EXISTS %s (
                        %s %s PRIMARY KEY,
                        data JSONB
                    );
                """, table.value(), primaryKey.value(), pkSqlType));

        // TODO: Indexing?

        return this;
    }

    public void save(final T entity) {
        try {
            final Object pk = pkField.get(entity);
            // Map the object to JSON
            final String json = Json.objectToString(entity);
            // Oh god this is so ugly
            database.sql("INSERT INTO " + table.value() + " (" + primaryKey.value() + ", data) values (?, to_jsonb(?::jsonb)) " +
                    "ON CONFLICT (" + primaryKey.value() + ") DO UPDATE SET " + primaryKey.value() + " = ?, data = to_jsonb(?::jsonb);", c -> {
                c.setObject(1, pk);
                c.setString(2, json);
                c.setObject(3, pk);
                c.setString(4, json);
                c.execute();
            });
        } catch(final IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    public Optional<T> load(final Object pk) {
        final OptionalHolder result = new OptionalHolder();
        database.sql("SELECT * FROM " + table.value() + " WHERE " + primaryKey.value() + " = ?;", c -> {
            c.setObject(1, pk);
            final ResultSet resultSet = c.executeQuery();
            if(resultSet.isBeforeFirst()) {
                resultSet.next();
                try {
                    result.setValue(loadFromResultSet(resultSet));
                } catch(final IllegalStateException e) {
                    e.printStackTrace();
                    // Optional API says this will return Optional.empty()
                    result.setValue(null);
                }
            }
        });
        return result.value;
    }

    private T loadFromResultSet(final ResultSet resultSet) {
        try {
            final String json = resultSet.getString("data");
            try {
                return Json.MAPPER.readValue(json, type);
            } catch(final IOException e) {
                throw new IllegalStateException("Couldn't load entity " + type.getName() + " from JSON " + json, e);
            }
        } catch(final SQLException e) {
            throw new IllegalStateException("Couldn't load entity " + type.getName(), e);
        }
    }

    private String typeToSqlType(final Class<?> type) {
        if(type.equals(String.class)) {
            return "TEXT";
        } else if(type.equals(Integer.class) || type.equals(int.class)) {
            return "INT";
        } else if(type.equals(Long.class) || type.equals(long.class)) {
            return "BIGINT";
        } else {
            throw new IllegalArgumentException("No SQL type mapping known for class of type: " + type.getName());
        }
    }

    // Ugly hack to allow bringing an optional out of a lambda
    private final class OptionalHolder {
        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        private Optional<T> value = Optional.empty();

        private void setValue(final T data) {
            value = Optional.ofNullable(data);
        }
    }
}

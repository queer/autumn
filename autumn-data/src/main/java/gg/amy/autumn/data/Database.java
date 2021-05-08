package gg.amy.autumn.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import gg.amy.autumn.application.annotation.Run;
import gg.amy.autumn.di.annotation.Component;
import gg.amy.autumn.di.annotation.Config;
import gg.amy.autumn.di.annotation.Inject;
import gg.amy.autumn.di.annotation.Singleton;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @author amy
 * @since 5/8/21.
 */
@Component
@Singleton
public class Database {
    private final Map<Class<?>, PostgresMapper<?>> mappers = new ConcurrentHashMap<>();

    @Inject
    private Logger logger;

    @Config("autumn.data.url")
    private String url;
    @Config("autumn.data.user")
    private String user;
    @Config("autumn.data.password")
    private String password;

    private HikariDataSource hikari;

    @Run
    public void connect() {
        final var config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);
        hikari = new HikariDataSource(config);
    }

    public <T> boolean isMapped(@Nonnull final Class<T> cls) {
        return mappers.containsKey(cls);
    }

    @SuppressWarnings("unchecked")
    public <T> PostgresMapper<T> map(@Nonnull final Class<T> cls) {
        // Safe cast
        return (PostgresMapper<T>) mappers.computeIfAbsent(cls, __ -> new PostgresMapper<>(cls, this).init());
    }

    public void sql(@SuppressWarnings("TypeMayBeWeakened") final SqlConsumer<Connection> consumer) {
        try(final var connection = hikari.getConnection()) {
            consumer.accept(connection);
        } catch(final SQLException e) {
            logger.error("Exception while executing SQL:", e);
            throw new IllegalStateException(e);
        }
    }

    public void sql(final String sql, @SuppressWarnings("TypeMayBeWeakened") final SqlConsumer<PreparedStatement> consumer) {
        sql(connection -> {
            try(final PreparedStatement statement = connection.prepareStatement(sql)) {
                logger.trace("Accepting consumer to prepare statement: {}", sql);
                consumer.accept(statement);
            } catch(final SQLException e) {
                logger.error("Exception while executing SQL statement '{}':", sql, e);
                throw new IllegalStateException(e);
            }
        });
    }

    public void sql(final String sql) {
        logger.trace("Running statement: {}", sql);
        sql(sql, PreparedStatement::execute);
    }

    @FunctionalInterface
    public interface SqlConsumer<T> extends Consumer<T> {
        @Override
        default void accept(final T t) {
            try {
                sql(t);
            } catch(final SQLException e) {
                throw new RuntimeException(e);
            }
        }

        void sql(T t) throws SQLException;
    }
}

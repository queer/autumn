package gg.amy.autumn.data;

import gg.amy.autumn.config.annotation.PrimaryKey;
import gg.amy.autumn.config.annotation.Table;
import gg.amy.autumn.di.AutumnDI;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author amy
 * @since 5/8/21.
 */
@SuppressWarnings("OptionalGetWithoutIsPresent")
public class DatabaseTest {
    @Test
    public void testThatItWorks() {
        final var di = new AutumnDI().init(getClass());
        final var database = di.getComponent(getClass(), Database.class).get();
        database.connect();
        database.sql("BEGIN;");
        final var entity = new Entity("test", "string");
        final var mapper = database.map(Entity.class);
        mapper.save(entity);
        final var loaded = mapper.load(entity.primaryKey).get();
        assertEquals(entity.primaryKey, loaded.primaryKey);
        assertEquals(entity.value, loaded.value);
        database.sql("ROLLBACK;");
    }

    @Table("test")
    public record Entity(@Nonnull @PrimaryKey("id") String primaryKey, @Nonnull String value) {
    }
}

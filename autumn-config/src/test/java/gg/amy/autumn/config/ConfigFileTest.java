package gg.amy.autumn.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author amy
 * @since 5/8/21.
 */
public class ConfigFileTest {
    @Test
    public void testThatDotsWork() {
        final var config = ConfigFile.readFile("test-config.hjson");
        assertEquals("test", config.getString("test"));
        assertEquals("e", config.getString("a.b.c.d"));
        assertEquals(1, config.getInt("arr.0"));
        assertEquals("value", config.get("otherArr.0.key"));
        assertNull(config.get("alsdfasdf"));
        assertThrows(NullPointerException.class, () -> config.get("f.qa.c.g"));
    }
}

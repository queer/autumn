package gg.amy.autumn.json;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author amy
 * @since 5/2/21.
 */
public class JsonArrayTest {
    @Test
    public void testThatSerialisationWorks() {
        final var a = new JsonArray().add("test");
        assertEquals("[\"test\"]", a.toJson());
    }

    @Test
    public void testThatDeserialisationWorks() {
        final var a = new JsonArray().add("test");
        assertEquals(a, new JsonArray("[\"test\"]"));
    }
}

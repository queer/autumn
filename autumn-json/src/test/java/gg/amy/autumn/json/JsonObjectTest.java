package gg.amy.autumn.json;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author amy
 * @since 5/2/21.
 */
public class JsonObjectTest {
    @Test
    public void testThatSerialisationWorks() {
        final var o = new JsonObject().put("test", 1);
        assertEquals("{\"test\":1}", o.toJson());
    }

    @Test
    public void testThatDeserialisationWorks() {
        final var o = new JsonObject().put("test", 1);
        assertEquals(o, new JsonObject("{\"test\":1}"));
    }

    @Test
    public void testThatIntoWorks() {
        final var o = new JsonObject().put("test", "test");
        final var mapped = o.into(TestObject.class);
        assertEquals("test", mapped.test);
    }

    @Test
    public void testThatNestingWorks() {
        final var o = new JsonObject().put("test", new JsonObject().put("test", "test"));
        assertEquals("{\"test\":{\"test\":\"test\"}}", o.toJson());
    }

    private record TestObject(String test) {}
}

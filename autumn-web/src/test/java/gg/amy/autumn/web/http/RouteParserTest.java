package gg.amy.autumn.web.http;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author amy
 * @since 5/1/21.
 */
public class RouteParserTest {
    @Test
    public void testThatRouteCompilationWorks() throws Exception {
        final var route = RouteParser.compile(new Object(), Object.class.getDeclaredMethod("hashCode"), HttpMethod.GET, "/test");
        assertArrayEquals(new String[]{"test"}, route.parts());
    }

    @Test
    public void testThatRouteMatchingWorks() throws Exception {
        final var route = RouteParser.compile(new Object(), Object.class.getDeclaredMethod("hashCode"), HttpMethod.GET, "/test");
        assertTrue(route.matches(new String[]{"test"}));
    }

    @Test
    public void testThatRouteMatchingWorksWithParams() throws Exception {
        final var route = RouteParser.compile(new Object(), Object.class.getDeclaredMethod("hashCode"), HttpMethod.GET, "/test/:param");
        assertTrue(route.matches(new String[]{"test", "1"}));
    }

    @Test
    public void testThatRouteParamParsingWorks() throws Exception {
        final var route = RouteParser.compile(new Object(), Object.class.getDeclaredMethod("hashCode"), HttpMethod.GET, "/test/:param");
        assertEquals(Map.of("param", "1"), RouteParser.parseOutParams(route, "/test/1"));
    }

    @Test
    public void testThatInvalidParamsThrow() {
        assertThrows(IllegalArgumentException.class, () -> RouteParser.compile(new Object(), Object.class.getDeclaredMethod("hashCode"), HttpMethod.GET, "/test/:1"));
        assertThrows(IllegalArgumentException.class, () -> RouteParser.compile(new Object(), Object.class.getDeclaredMethod("hashCode"), HttpMethod.GET, "/test/:_"));
    }
}

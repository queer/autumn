package gg.amy.autumn.example;

import gg.amy.autumn.example.routes.BasicRoutes;
import gg.amy.autumn.web.http.HttpMethod;
import gg.amy.autumn.web.http.Request;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author amy
 * @since 5/1/21.
 */
public class BasicRoutesTest {
    @Test
    public void testIndex() {
        final var response = new BasicRoutes().index(Request.create(HttpMethod.GET, "/", new byte[0]));
        assertEquals(200, response.status());
        assertEquals("henlo world!", new String(response.body(), StandardCharsets.UTF_8));
    }
}

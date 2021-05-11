package gg.amy.autumn.example;

import gg.amy.autumn.application.AutumnApplication;
import gg.amy.autumn.web.http.HttpMethod;
import gg.amy.autumn.web.http.Request;
import gg.amy.autumn.web.http.Router;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author amy
 * @since 5/1/21.
 */
@SuppressWarnings("OptionalGetWithoutIsPresent")
public class BasicRoutesTest {
    static {
        AutumnApplication.bootstrap(BasicRoutesTest.class);
    }

    @Test
    public void testIndex() {
        final var router = AutumnApplication.createDI(getClass()).getComponent(getClass(), Router.class).get();
        final var response = router.runRequest(Request.create(HttpMethod.GET, "/", new byte[0]));
        assertEquals(200, response.status());
        assertEquals("henlo world!", new String(response.body(), StandardCharsets.UTF_8));
    }

    @Test
    public void testHenlo() {
        final var router = AutumnApplication.createDI(getClass()).getComponent(getClass(), Router.class).get();
        final var response = router.runRequest(Request.create(HttpMethod.GET, "/henlo/person", new byte[0]));
        assertEquals(200, response.status());
        assertEquals("henlo person!", new String(response.body(), StandardCharsets.UTF_8));
    }
}

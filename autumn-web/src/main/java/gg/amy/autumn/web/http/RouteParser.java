package gg.amy.autumn.web.http;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author amy
 * @since 5/1/21.
 */
public final class RouteParser {
    private RouteParser() {
    }

    @Nonnull
    public static HttpRoute compile(@Nonnull final Object o, @Nonnull final Method m,
                                    @Nonnull final HttpMethod httpMethod, @Nonnull final String route) {
        return ImmutableHttpRoute.builder()
                .object(o)
                .method(m)
                .httpMethod(httpMethod)
                // Only the first / can be ignored, after that they mean something
                .parts(parseRoute(route, !route.equals("/")))
                .build();
    }

    public static Map<String, String> parseOutParams(@Nonnull final SimpleHttpRoute route, @Nonnull final String path) {
        final var out = new HashMap<String, String>();
        final var parsed = parseRoute(path, false);

        for(int i = 0; i < route.parts().length; i++) {
            final var part = route.parts()[i];
            if(part.startsWith(":")) {
                final var pathPart = parsed[i];
                out.put(part.substring(1), pathPart);
            }
        }

        return out;
    }

    public static String[] parseRoute(@Nonnull final String route, final boolean validate) {
        final var parts = route.replaceFirst("/", "").split("/+");
        if(validate) {
            for(final var part : parts) {
                // TODO: Unicode?
                if(!part.matches(":?[A-Za-z][A-Za-z0-9_]*")) {
                    throw new IllegalArgumentException("Route part `" + part + "` invalid (must be valid identifier).");
                }
            }
        }
        return parts;
    }
}

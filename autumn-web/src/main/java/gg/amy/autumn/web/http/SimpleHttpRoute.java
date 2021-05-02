package gg.amy.autumn.web.http;

import javax.annotation.Nonnull;

/**
 * A basic HTTP route. A route is made of two components, its method and its
 * parts. The parts of a route are the {@code /}-delimited sections, split into
 * an ordered array. Routes support matching, ie. testing a parts array against
 * its own parts to test for a match, including path params.
 *
 * @author amy
 * @since 5/1/21.
 */
public interface SimpleHttpRoute {
    String[] parts();

    HttpMethod httpMethod();

    default boolean matches(@Nonnull final String[] input) {
        final var parts = parts();
        if(parts.length != input.length) {
            return false;
        }
        var matches = 0;
        for(int i = 0; i < parts.length; i++) {
            final var part = parts[i];
            final var inputPart = input[i];
            if(part.equalsIgnoreCase(inputPart) || part.startsWith(":")) {
                matches++;
            }
        }
        return matches == parts.length;
    }
}

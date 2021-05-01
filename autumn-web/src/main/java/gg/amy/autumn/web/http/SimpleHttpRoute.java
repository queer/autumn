package gg.amy.autumn.web.http;

import javax.annotation.Nonnull;

/**
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

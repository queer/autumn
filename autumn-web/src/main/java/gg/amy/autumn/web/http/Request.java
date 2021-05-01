package gg.amy.autumn.web.http;

import org.immutables.value.Value.Immutable;

/**
 * @author amy
 * @since 5/1/21.
 */
@Immutable
public interface Request {
    String id();

    HttpMethod method();

    String path();

    byte[] body();

    default String bodyString() {
        return new String(body());
    }
}

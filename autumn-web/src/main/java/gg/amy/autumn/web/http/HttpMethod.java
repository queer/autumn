package gg.amy.autumn.web.http;

import javax.annotation.Nonnull;

/**
 * HTTP method enum.
 *
 * @author amy
 * @since 5/1/21.
 */
public enum HttpMethod {
    CONNECT,
    DELETE,
    GET,
    HEAD,
    OPTIONS,
    PATCH,
    POST,
    PUT,
    TRACE,
    ;

    public static HttpMethod fromNetty(@Nonnull final io.netty.handler.codec.http.HttpMethod nettyMethod) {
        return byName(nettyMethod.name());
    }

    public static HttpMethod byName(@Nonnull final String name) {
        return valueOf(name.toUpperCase());
    }
}

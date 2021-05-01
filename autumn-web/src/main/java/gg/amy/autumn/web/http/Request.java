package gg.amy.autumn.web.http;

import gg.amy.autumn.web.util.ID;
import org.immutables.value.Value.Immutable;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;

/**
 * @author amy
 * @since 5/1/21.
 */
@Immutable
public interface Request {
    @Nonnull
    String id();

    @Nonnull
    HttpMethod method();

    @Nonnull
    String path();

    @Nonnull
    byte[] body();

    @Nonnull
    default String bodyString() {
        return new String(body());
    }

    @Nonnull
    static Request create(@Nonnull final HttpMethod method, @Nonnull final String path, @Nonnull final String body) {
        return create(method, path, body.getBytes(StandardCharsets.UTF_8));
    }

    static Request create(@Nonnull final HttpMethod method, @Nonnull final String path, @Nonnull final byte[] body) {
        return ImmutableRequest.builder()
                .id(ID.gen())
                .method(method)
                .path(path)
                .body(body)
                .build();
    }
}

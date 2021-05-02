package gg.amy.autumn.web.http;

import gg.amy.autumn.web.util.ID;
import org.immutables.value.Value.Immutable;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * An HTTP request. Contains all the relevant information needed for handling.
 * Requests can be created for tests via {@link #create(HttpMethod, String, byte[])}
 * and {@link #create(HttpMethod, String, String)}.
 *
 * @author amy
 * @since 5/1/21.
 */
@Immutable
public interface Request {
    /**
     * @return The <a href="https://github.com/segmentio/ksuid">K-sortable</a>
     * ID of this request.
     */
    @Nonnull
    String id();

    /**
     * @return The method of this request.
     */
    @Nonnull
    HttpMethod method();

    /**
     * @return The path this request was sent to.
     */
    @Nonnull
    String path();

    /**
     * @return The request body, as bytes.
     */
    @Nonnull
    byte[] body();

    /**
     * <strong>This method lazily-constructs strings!</strong> That is, each
     * invocation of this method allocates a new string object.
     *
     * @return The request body as a string.
     */
    @Nonnull
    default String bodyString() {
        return new String(body());
    }

    /**
     * @return The parameters of the request.
     */
    @Nonnull
    Map<String, String> params();

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
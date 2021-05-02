package gg.amy.autumn.web.http;

import org.immutables.value.Value.Immutable;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * An HTTP response. New responses can easily be constructed via
 * {@link #create()}.
 *
 * @author amy
 * @since 5/1/21.
 */
@Immutable
public interface Response {
    /**
     * @return A new response object.
     */
    @Nonnull
    static Response create() {
        return ImmutableResponse.builder()
                .status(200)
                .headers(Map.of())
                .body()
                .build();
    }

    /**
     * @return The response's status code.
     */
    int status();

    /**
     * @return The response's headers.
     */
    Map<String, String> headers();

    /**
     * @return The response's body, as bytes.
     */
    byte[] body();

    /**
     * Sets the response's status.
     *
     * @param status The new status.
     * @return The new response.
     */
    default Response status(@Nonnegative final int status) {
        return ImmutableResponse.builder().from(this).status(status).build();
    }

    /**
     * Sets the response's headers.
     *
     * @param headers The new headers.
     * @return The new response.
     */
    default Response headers(@Nonnull final Map<String, String> headers) {
        return ImmutableResponse.builder().from(this).headers(headers).build();
    }

    /**
     * Sets the response's body.
     *
     * @param body The new body.
     * @return The new response.
     */
    default Response body(@Nonnull final byte[] body) {
        return ImmutableResponse.builder().from(this).body(body).build();
    }

    /**
     * Sets the response's body. The string is converted into bytes internally.
     *
     * @param body The new body.
     * @return The new response.
     */
    default Response body(@Nonnull final String body) {
        return body(body.getBytes(StandardCharsets.UTF_8));
    }
}

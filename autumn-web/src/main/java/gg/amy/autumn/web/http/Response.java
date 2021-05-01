package gg.amy.autumn.web.http;

import org.immutables.value.Value.Immutable;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author amy
 * @since 5/1/21.
 */
@Immutable
public interface Response {
    int status();

    Map<String, String> headers();

    byte[] body();

    default Response status(@Nonnegative final int status) {
        return ImmutableResponse.builder().from(this).status(status).build();
    }

    default Response headers(@Nonnull final Map<String, String> headers) {
        return ImmutableResponse.builder().from(this).headers(headers).build();
    }

    default Response body(@Nonnull final byte[] body) {
        return ImmutableResponse.builder().from(this).body(body).build();
    }

    default Response body(@Nonnull final String body) {
        return ImmutableResponse.builder().from(this).body(body.getBytes()).build();
    }

    @Nonnull
    static Response create() {
        return ImmutableResponse.builder()
                .status(200)
                .headers(Map.of())
                .body()
                .build();
    }
}

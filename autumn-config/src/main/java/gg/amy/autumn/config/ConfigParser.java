package gg.amy.autumn.config;

import gg.amy.autumn.json.JsonObject;
import org.hjson.JsonValue;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.StringWriter;

/**
 * @author amy
 * @since 5/8/21.
 */
public final class ConfigParser {
    private ConfigParser() {
    }

    public static JsonObject parse(@Nonnull final String config) {
        // TODO: Be better.
        final var hjson = JsonValue.readHjson(config).asObject();
        try(final var sw = new StringWriter()) {
            hjson.asObject().writeTo(sw);
            return new JsonObject(sw.toString());
        } catch(final IOException e) {
            throw new IllegalStateException(e);
        }
    }
}

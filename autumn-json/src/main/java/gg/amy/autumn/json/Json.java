package gg.amy.autumn.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

/**
 * @author amy
 * @since 5/2/21.
 */
public final class Json {
    public static final ObjectMapper MAPPER = new ObjectMapper();

    private Json() {
    }

    public static String objectToString(@Nonnull final Object data) {
        try {
            return MAPPER.writeValueAsString(data);
        } catch(final JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> stringToObject(@Nonnull final String data) {
        try {
            return (Map<String, Object>) MAPPER.readValue(data, Map.class);
        } catch(final JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Object> stringToArray(@Nonnull final String data) {
        try {
            return (List<Object>) MAPPER.readValue(data, List.class);
        } catch(final JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }
}

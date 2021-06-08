package gg.amy.autumn.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.StringWriter;
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

    public static String serialize(@Nonnull final JsonObject json) {
        try {
            final var sw = new StringWriter();
            final var gen = MAPPER.createGenerator(sw);
            serializeData(json, gen);
            gen.close();
            return sw.toString();
        } catch(final Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static String serialize(@Nonnull final JsonArray json) {
        try {
            final var sw = new StringWriter();
            final var gen = MAPPER.createGenerator(sw);
            serializeData(json, gen);
            gen.close();
            return sw.toString();
        } catch(final Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static void serializeData(Object data, @Nonnull final JsonGenerator gen) throws IOException {
        // get usable delegates
        if(data instanceof JsonObject) {
            data = ((JsonObject) data).__delegate();
        }
        if(data instanceof JsonArray) {
            data = ((JsonArray) data).__delegate();
        }


        if(data instanceof Map) {
            gen.writeStartObject();
            //noinspection unchecked
            ((Map<String, Object>) data).forEach((k, v) -> {
                try {
                    gen.writeFieldName(k);
                    serializeData(v, gen);
                } catch(final IOException e) {
                    throw new IllegalStateException(e);
                }
            });
            gen.writeEndObject();
        } else if(data instanceof List) {
            gen.writeStartArray();
            ((List<?>) data).forEach(v -> {
                try {
                    serializeData(v, gen);
                } catch(final IOException e) {
                    throw new IllegalStateException(e);
                }
            });
            gen.writeEndArray();
        } else if(data instanceof String) {
            gen.writeString((String) data);
        } else if(data instanceof Boolean) {
            gen.writeBoolean((Boolean) data);
        } else if(data instanceof Byte) {
            gen.writeNumber(((Byte) data).shortValue());
        } else if(data instanceof Short) {
            gen.writeNumber((Short) data);
        } else if(data instanceof Character) {
            gen.writeNumber((short) ((Character) data).charValue());
        } else if(data instanceof Integer) {
            gen.writeNumber((Integer) data);
        } else if(data instanceof Long) {
            gen.writeNumber((Long) data);
        } else if(data instanceof Float) {
            gen.writeNumber((Float) data);
        } else if(data instanceof Double) {
            gen.writeNumber((Double) data);
        } else if(data == null) {
            gen.writeNull();
        } else {
            throw new IllegalArgumentException("Unkown data type " + data.getClass() + ": " + data);
        }
    }
}

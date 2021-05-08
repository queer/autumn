package gg.amy.autumn.json;

import com.fasterxml.jackson.core.JsonProcessingException;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * A <strong>mutable</strong> JSON object.
 *
 * @author amy
 * @since 5/2/21.
 */
public final class JsonObject {
    private final Map<String, Object> delegate = new HashMap<>();

    public JsonObject() {
    }

    public JsonObject(@Nonnull final String data) {
        final var map = Json.stringToObject(data);
        delegate.putAll(map);
    }

    public JsonObject(@Nonnull final Map<String, Object> data) {
        delegate.putAll(data);
    }

    @Nonnull
    public static JsonObject from(@Nonnull final Object obj) {
        // TODO: More performant implementation
        return new JsonObject(Json.objectToString(obj));
    }

    public Object get(@Nonnull final String key) {
        return delegate.get(key);
    }

    public byte getByte(@Nonnull final String key) {
        return (byte) get(key);
    }

    public short getShort(@Nonnull final String key) {
        return (short) get(key);
    }

    public int getInt(@Nonnull final String key) {
        return (int) get(key);
    }

    public long getLong(@Nonnull final String key) {
        return (long) get(key);
    }

    public boolean getBoolean(@Nonnull final String key) {
        return (boolean) get(key);
    }

    public String getString(@Nonnull final String key) {
        return (String) get(key);
    }

    public JsonObject getJsonObject(@Nonnull final String key) {
        return (JsonObject) get(key);
    }

    public JsonArray getJsonArray(@Nonnull final String key) {
        return (JsonArray) get(key);
    }

    public JsonObject put(@Nonnull final String key, @Nonnull final Object value) {
        delegate.put(key, value);
        return this;
    }

    public JsonObject remove(@Nonnull final String key) {
        delegate.remove(key);
        return this;
    }

    public JsonObject merge(@Nonnull final JsonObject second) {
        second.delegate.forEach(this::put);
        return this;
    }

    public String toJson() {
        try {
            return Json.MAPPER.writeValueAsString(delegate);
        } catch(final JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    Map<String, Object> __delegate() {
        return delegate;
    }

    @Override
    public boolean equals(final Object obj) {
        if(obj instanceof final JsonObject j) {
            return delegate.equals(j.delegate);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }
}

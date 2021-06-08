package gg.amy.autumn.json;

import com.fasterxml.jackson.core.JsonProcessingException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    @Nullable
    public Object get(@Nonnull final String key) {
        return delegate.get(key);
    }

    @SuppressWarnings("ConstantConditions")
    public byte getByte(@Nonnull final String key) {
        return (byte) get(key);
    }

    @SuppressWarnings("ConstantConditions")
    public short getShort(@Nonnull final String key) {
        return (short) get(key);
    }

    @SuppressWarnings("ConstantConditions")
    public int getInt(@Nonnull final String key) {
        return (int) get(key);
    }

    @SuppressWarnings("ConstantConditions")
    public long getLong(@Nonnull final String key) {
        return (long) get(key);
    }

    @SuppressWarnings("ConstantConditions")
    public float getFloat(@Nonnull final String key) {
        return (float) get(key);
    }

    @SuppressWarnings("ConstantConditions")
    public double getDouble(@Nonnull final String key) {
        return (double) get(key);
    }

    @SuppressWarnings("ConstantConditions")
    public boolean getBoolean(@Nonnull final String key) {
        return (boolean) get(key);
    }

    @Nullable
    public String getString(@Nonnull final String key) {
        return (String) get(key);
    }

    @Nullable
    public JsonObject getJsonObject(@Nonnull final String key) {
        return (JsonObject) get(key);
    }

    @Nullable
    public JsonArray getJsonArray(@Nonnull final String key) {
        return (JsonArray) get(key);
    }

    @Nonnull
    public JsonObject put(@Nonnull final String key, @Nonnull final Object value) {
        delegate.put(key, value);
        return this;
    }

    @Nonnull
    public JsonObject remove(@Nonnull final String key) {
        delegate.remove(key);
        return this;
    }

    @Nonnull
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

    @Nonnull
    public <T> T into(@Nonnull final Class<T> cls) {
        try {
            return Json.MAPPER.readValue(toJson(), cls);
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

package gg.amy.autumn.json;

import com.fasterxml.jackson.core.JsonProcessingException;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * A <strong>mutable</strong> JSON array.
 *
 * @author amy
 * @since 5/2/21.
 */
public final class JsonArray {
    private final List<Object> delegate = new ArrayList<>();

    public JsonArray() {
    }

    public JsonArray(@Nonnull final String data) {
        final var list = Json.stringToArray(data);
        delegate.addAll(list);
    }

    public JsonArray(@Nonnull final Collection<Object> data) {
        delegate.addAll(data);
    }

    public static JsonArray from(@Nonnull final List<Object> list) {
        return new JsonArray(list);
    }

    public static JsonArray from(@Nonnull final Object[] array) {
        return new JsonArray(Arrays.asList(array));
    }

    public JsonArray add(@Nonnull final Object object) {
        delegate.add(object);
        return this;
    }

    public JsonArray add(@Nonnegative final int index, @Nonnull final Object object) {
        delegate.add(index, object);
        return this;
    }

    public JsonArray remove(@Nonnull final Object object) {
        delegate.remove(object);
        return this;
    }

    public JsonArray remove(@Nonnegative final int index) {
        delegate.remove(index);
        return this;
    }

    public JsonArray set(final int index, final Object element) {
        delegate.set(index, element);
        return this;
    }

    public Object get(final int index) {
        return delegate.get(index);
    }

    public byte getByte(@Nonnegative final int index) {
        return (byte) get(index);
    }

    public short getShort(@Nonnegative final int index) {
        return (short) get(index);
    }

    public int getInt(@Nonnegative final int index) {
        return (int) get(index);
    }

    public long getLong(@Nonnegative final int index) {
        return (long) get(index);
    }

    public boolean getBoolean(@Nonnegative final int index) {
        return (boolean) get(index);
    }

    public String getString(@Nonnegative final int index) {
        return (String) get(index);
    }

    public JsonObject getJsonObject(@Nonnegative final int index) {
        return (JsonObject) get(index);
    }

    public JsonArray getJsonArray(@Nonnegative final int index) {
        return (JsonArray) get(index);
    }

    public String toJson() {
        try {
            return Json.MAPPER.writeValueAsString(delegate);
        } catch(final JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    public JsonArray merge(@Nonnull final JsonArray second) {
        delegate.addAll(second.delegate);
        return this;
    }

    List<Object> __delegate() {
        return delegate;
    }

    @Override
    public boolean equals(final Object obj) {
        if(obj instanceof final JsonArray a) {
            return delegate.equals(a.delegate);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }
}

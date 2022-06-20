package gg.amy.autumn.config;

import gg.amy.autumn.json.JsonArray;
import gg.amy.autumn.json.JsonObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A config file wraps a {@link JsonObject} and provides a similar API, as well
 * as easy-to-use factory methods for creating. A config file is different from
 * a JSON object in the following ways:
 * <ul>
 *     <li>
 *         {@link #get(String)} and related methods can take a
 *         {@code .}-delimited path, instead of just a single key. The path
 *         will be automatically recursively traversed. If the provided key is
 *         a number, it will be used as an index into an array if possible.
 *     </li>
 * </ul>
 *
 * @author amy
 * @since 5/8/21.
 */
@SuppressWarnings("UseOfConcreteClass")
public final class ConfigFile {
    private final JsonObject delegate;

    private ConfigFile(@Nonnull final String config) {
        delegate = ConfigParser.parse(config);
    }

    private ConfigFile(@Nonnull final JsonObject config) {
        delegate = new JsonObject().merge(config);
    }

    /**
     * Reads the config from a given file. Configs are searched in this order:
     * <ol>
     *     <li>Inside the current JAR file, path and filename.</li>
     *     <li>Inside the current JAR file, filename only.</li>
     *     <li>Inside the current working directory.</li>
     * </ol>
     * Config files that are found will be merged in the order above.
     *
     * @return The deserialised config file.
     * @throws IllegalStateException If the provided file is invalid.
     */
    public static ConfigFile readFile(@Nonnull final String path) {
        String fromJar = null;
        //noinspection ConstantConditions
        try(final var reader = new BufferedReader(new InputStreamReader(ConfigFile.class.getResourceAsStream('/' + path)))) {
            fromJar = reader.lines().collect(Collectors.joining());
        } catch(final IOException e) {
            throw new IllegalStateException(e);
        } catch(final NullPointerException ignored) {
        }

        String fromJarNoPath = null;
        //noinspection ConstantConditions
        try(final var reader = new BufferedReader(new InputStreamReader(ConfigFile.class.getResourceAsStream('/' + new File(path).getName())))) { // wheee
            fromJarNoPath = reader.lines().collect(Collectors.joining());
        } catch(final IOException e) {
            throw new IllegalStateException(e);
        } catch(final NullPointerException ignored) {
        }

        String fromLocalFile = null;
        try(final var reader = new BufferedReader(new FileReader("./" + path))) {
            fromLocalFile = reader.lines().collect(Collectors.joining());
        } catch(final FileNotFoundException | NullPointerException ignored) {
        } catch(final IOException e) {
            throw new IllegalStateException(e);
        }

        if(fromJar == null && fromJarNoPath == null && fromLocalFile == null) {
            throw new IllegalStateException("No file found!");
        }

        final var config = new JsonObject();
        if(fromJar != null) {
            config.merge(ConfigParser.parse(fromJar));
        }
        if(fromJarNoPath != null) {
            config.merge(ConfigParser.parse(fromJarNoPath));
        }
        if(fromLocalFile != null) {
            config.merge(ConfigParser.parse(fromLocalFile));
        }
        return new ConfigFile(config);
    }

    @Nonnull
    public static ConfigFile fromString(@Nonnull final String config) {
        return new ConfigFile(config);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public Object get(@Nonnull final String key) {
        final var parts = key.split("\\.");
        Object target = delegate.get(parts[0]);
        final var iter = parts.length == 1 ? new String[0] : Arrays.copyOfRange(parts, 1, parts.length);
        for(@Nonnull final var part : iter) {
            if(target == null) {
                throw new NullPointerException("Key " + key + " not found");
            }
            if(part.matches("\\d+")) {
                target = ((List<Object>) target).get(Integer.parseInt(part));
            } else {
                target = ((Map<String, Object>) target).get(part);
            }
        }
        return target;
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
}

package gg.amy.autumn.di.annotation;

import gg.amy.autumn.config.ConfigFile;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field as being loaded from a config file. See {@link ConfigFile} for
 * more information about how config loading works. By default, this will
 * search for a file named {@code config.hjson}. If this value is not found,
 * system properties will be searched, and then finally environment variables.
 * Environment variables are the key name uppercased, with {@code .}s replaced
 * with {@code _}s.
 *
 * @author amy
 * @since 5/8/21.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Config {
    /**
     * @return The config file to read from.
     */
    String file() default "config.hjson";

    /**
     * @return The key to read from said config file.
     */
    String value();
}

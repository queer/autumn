package gg.amy.autumn.di.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>A creator method is a static method that takes only a {@link Class} as
 * arguments, and returns a new instance of some value.</p>
 * <p>An example use-case is a creator method that returns a new logger
 * instance for the provided class.</p>
 *
 * @author amy
 * @since 5/1/21.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Creator {
}

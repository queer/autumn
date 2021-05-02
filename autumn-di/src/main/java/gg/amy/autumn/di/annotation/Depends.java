package gg.amy.autumn.di.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Singleton components that a singleton component depends on. Annotating a
 * non-singleton component with this is an error.</p>
 * <p><strong>WARNING: DEPENDENCY LOOPS ARE NOT CURRENTLY DETECTED.</strong></p>
 *
 * @author amy
 * @since 5/1/21.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Depends {
    Class<?>[] value();
}

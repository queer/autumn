package gg.amy.autumn.di.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the field as having a value injected into it. The DI process will
 * attempt to find exact matches, then fuzzy-matches via checking whether a
 * given type can be assigned to the field.
 *
 * @author amy
 * @since 5/1/21.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Inject {
}

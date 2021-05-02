package gg.amy.autumn.di.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An Autumn component. Components are generally instantiated at point-of-use;
 * that is, components are instantiated as needed. The exception to this rule
 * is singleton components, ie. components that only have a single instance
 * ever. A use-case for this is an HTTP router that wants to only load routes
 * once. Components can be made singletons via the {@link Singleton}
 * annotation.
 *
 * @author amy
 * @since 5/1/21.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {
}

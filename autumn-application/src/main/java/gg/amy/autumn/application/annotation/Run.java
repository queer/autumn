package gg.amy.autumn.application.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A method that needs to be "run," ie the actual "meat" of an application. A
 * method annotated with this will be run after all setup. Methods with this
 * annotation are run <strong>sequentially</strong>, meaning that such methods
 * should avoid blocking and return as quickly as possible.
 *
 * @author amy
 * @since 5/1/21.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Run {
}

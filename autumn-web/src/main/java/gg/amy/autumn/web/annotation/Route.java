package gg.amy.autumn.web.annotation;

import gg.amy.autumn.web.http.HttpMethod;

import javax.annotation.Nonnull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author amy
 * @since 5/1/21.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Route {
    @Nonnull
    HttpMethod method();

    @Nonnull
    String path();
}

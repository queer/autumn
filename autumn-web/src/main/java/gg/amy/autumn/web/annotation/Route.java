package gg.amy.autumn.web.annotation;

import gg.amy.autumn.web.http.HttpMethod;
import gg.amy.autumn.web.http.Request;

import javax.annotation.Nonnull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as handling a specific route. Routes have two components, the
 * HTTP method, and the path. Currently, paths support exact matches (ex. a
 * path like {@code "/api/ping"}) and parameter-matches (ex. a path like
 * {@code "/hello/:name"}. In the case of the latter, parameter values will be
 * injected into the relevant {@link Request}.
 *
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

package gg.amy.autumn.web.http;

import gg.amy.autumn.di.AutumnDI;
import gg.amy.autumn.di.annotation.Component;
import gg.amy.autumn.di.annotation.Init;
import gg.amy.autumn.di.annotation.Inject;
import gg.amy.autumn.di.annotation.Singleton;
import gg.amy.autumn.web.annotation.Route;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author amy
 * @since 5/1/21.
 */
@Component
@Singleton
public class Router {
    private Set<HttpRoute> routes = new HashSet<>();

    @Inject
    private AutumnDI di;

    @Init
    public void scanForRoutes() {
        di.graph().getClassesWithMethodAnnotation(Route.class.getName())
                .getNames()
                .stream()
                .map(c -> {
                    try {
                        return Class.forName(c);
                    } catch(final ClassNotFoundException e) {
                        throw new IllegalStateException(e);
                    }
                })
                .forEach(c -> {
                    try {
                        final var instance = c.getConstructor().newInstance();
                        Arrays.stream(c.getDeclaredMethods())
                                .filter(m -> m.isAnnotationPresent(Route.class))
                                .forEach(m -> {
                                    if(m.getParameterCount() != 1 || !m.getParameterTypes()[0].equals(Request.class)) {
                                        throw new IllegalStateException(String.format(
                                                "@Route method %s#%s must take a Request as its only param.",
                                                c.getName(), m.getName()
                                        ));
                                    }
                                    final var route = m.getDeclaredAnnotation(Route.class);
                                    routes.add(RouteParser.compile(instance, m, route.method(), route.path()));
                                });
                    } catch(@Nonnull final Throwable e) {
                        throw new IllegalStateException(e);
                    }
                });
    }

    public Optional<HttpRoute> match(@Nonnull final HttpMethod method, @Nonnull final String path) {
        final var parsedPath = RouteParser.parseRoute(path, false);
        return routes.stream().filter(r -> r.httpMethod() == method && r.matches(parsedPath)).findFirst();
    }
}

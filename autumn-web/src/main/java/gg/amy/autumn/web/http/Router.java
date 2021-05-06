package gg.amy.autumn.web.http;

import gg.amy.autumn.di.AutumnDI;
import gg.amy.autumn.di.annotation.Component;
import gg.amy.autumn.di.annotation.Init;
import gg.amy.autumn.di.annotation.Inject;
import gg.amy.autumn.di.annotation.Singleton;
import gg.amy.autumn.web.annotation.Route;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

/**
 * The HTTP router component. Holds information about routes, and can route
 * individual {@link Request}s to the relevant handlers.
 *
 * @author amy
 * @since 5/1/21.
 */
@Component
@Singleton
public class Router {
    private final Collection<HttpRoute> routes = new HashSet<>();

    @Inject
    private AutumnDI di;

    @Inject
    private Logger logger;

    @Init
    public void scanForRoutes() {
        final var routeLookup = new HashSet<String>();
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
                        di.injectComponents(instance);
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
                                    if(!route.path().startsWith("/")) {
                                        throw new IllegalArgumentException(String.format(
                                                "Route '%s' must start with `/` (registered by %s#%s)",
                                                route.path(), c.getName(), m.getName()
                                        ));
                                    }
                                    if(routeLookup.contains(route.path())) {
                                        throw new IllegalArgumentException(String.format(
                                                "%s#%s tried to register route '%s', but it already exists.",
                                                c.getName(), m.getName(), route.path()
                                        ));
                                    }
                                    routes.add(RouteParser.compile(instance, m, route.method(), route.path()));
                                    routeLookup.add(route.path());
                                    logger.info("Loaded route {}#{}: {}", c.getName(), m.getName(), route.path());
                                });
                    } catch(@Nonnull final Throwable e) {
                        throw new IllegalStateException(e);
                    }
                });
    }

    public Response runRequest(@Nonnull Request req) {
        final var maybeRoute = match(req.method(), req.path());
        if(maybeRoute.isPresent()) {
            try {
                final var route = maybeRoute.get();
                final var params = RouteParser.parseOutParams(route, req.path());
                req = ImmutableRequest.copyOf(req).withParams(params);
                logger.trace("{}: object = {}, req = {}", req.id(), route.object(), req);
                return (Response) route.method().invoke(route.object(), req);
            } catch(final Throwable e) {
                logger.error("error handling request! ;-;", e);
                final String message;
                if(e.getMessage() == null) {
                    message = "<no message>";
                } else {
                    message = e.getMessage();
                }
                return Response.create().status(500).body(message);
            }
        } else {
            return Response.create().status(404).body("it's not here D:");
        }
    }

    private Optional<HttpRoute> match(@Nonnull final HttpMethod method, @Nonnull final String path) {
        final var parsedPath = RouteParser.parseRoute(path, false);
        return routes.stream().filter(r -> r.httpMethod() == method && r.matches(parsedPath)).findFirst();
    }
}

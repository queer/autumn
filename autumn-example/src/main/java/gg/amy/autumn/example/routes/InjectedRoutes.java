package gg.amy.autumn.example.routes;

import gg.amy.autumn.di.annotation.Inject;
import gg.amy.autumn.example.component.RamStats;
import gg.amy.autumn.web.annotation.Route;
import gg.amy.autumn.web.http.HttpMethod;
import gg.amy.autumn.web.http.Request;
import gg.amy.autumn.web.http.Response;

import javax.annotation.Nonnull;

/**
 * @author amy
 * @since 5/1/21.
 */
public class InjectedRoutes {
    @Inject
    private RamStats ramStats;

    @Route(method = HttpMethod.GET, path = "/ram")
    public Response handle(@Nonnull final Request req) {
        return Response.create().body("stats:\n" + ramStats.stats());
    }
}

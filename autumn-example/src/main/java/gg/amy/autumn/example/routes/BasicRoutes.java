package gg.amy.autumn.example.routes;

import gg.amy.autumn.web.annotation.Route;
import gg.amy.autumn.web.http.HttpMethod;
import gg.amy.autumn.web.http.Request;
import gg.amy.autumn.web.http.Response;

import javax.annotation.Nonnull;

/**
 * @author amy
 * @since 5/1/21.
 */
public class BasicRoutes {
    @Route(method = HttpMethod.GET, path = "/")
    public Response index(@Nonnull final Request req) {
        return Response.create().body("henlo world!");
    }

    @Route(method = HttpMethod.GET, path = "/henlo/:name")
    public Response henlo(@Nonnull final Request req) {
        return Response.create().body("henlo " + req.params().get("name") + '!');
    }
}

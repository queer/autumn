![autumn](./autumn.png)

Experimental Netty-based application/web framework.

An example application can be seen [here](https://github.com/queer/autumn/tree/mistress/autumn-example).

## Components

- `autumn-application`: Application bootstrap.
- `autumn-di`: Dependency injection framework.
- `autumn-web`: Web framework
- `autumn-example`: Example application

## What doesn't work

- Multipart
- Websockets
- Anything databases, lol
- HTTP/2 (dealing with SSL is a hairy problem)
- Globbing routes

## Build an API in 30 seconds

```java
public class Readme {
    public static void main(String[] args) {
        AutumnApplication.run();
    }

    @Route(method = HttpMethod.GET, path = "/hello/:name")
    public Response hello(Request req) {
        return Response.create().body("henlo " + req.params().get("name") + '!');
    }
}
```
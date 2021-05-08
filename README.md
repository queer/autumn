![autumn](./autumn.png)

Experimental Netty-based application/web framework.

An example application can be seen [here](https://github.com/queer/autumn/tree/mistress/autumn-example).

## Should I use this?

Probably not! It's still incredibly early-stage -- everything's versioned at
`0.0.1` for a reason, after all~ -- and so there's all sorts of instabilities,
weirdness, etc. that you probably don't want to deal with.

## Components

- `autumn-example`: Example application.
- `autumn-application`: Application bootstrap.
- `autumn-config`: [HJSON](https://hjson.github.io/)-based configuration.
- `autumn-di`: Dependency injection framework.
- `autumn-data`: Database access layer.
- `autumn-json`: JSON helper library.
- `autumn-web`: Web framework.

## What does work

- JVM boot to usable API in ~500ms
- Runs with 32MB of heap RAM or less
- Runtime DI + modularisation via components
- Component dependencies
- JSON
- Web server
- [HJSON](https://hjson.github.io/)-based autoconfig

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

## Modularise with components

```java

@Component
public class HelloComponent {
    public String sayHello(String name) {
        return "henlo " + name + "!";
    }
}

public class Readme {
    @Inject
    private HelloComponent hello;

    public static void main(String[] args) {
        AutumnApplication.run();
    }

    @Route(method = HttpMethod.GET, path = "/hello/:name")
    public Response hello(Request req) {
        return Response.create().body(hello.sayHello(req.params().get("name")));
    }
}
```

## Singleton components for ex. databases

```java

@Component
@Singleton
public class SingletonComponent {
    public String sayHello(String name) {
        return "henlo " + name + "!";
    }
}

public class Readme {
    @Inject
    private SingletonComponent singleton;

    public static void main(String[] args) {
        AutumnApplication.run();
    }

    @Route(method = HttpMethod.GET, path = "/hello/:name")
    public Response hello(Request req) {
        return Response.create().body(singleton.sayHello(req.params().get("name")));
    }
}
```

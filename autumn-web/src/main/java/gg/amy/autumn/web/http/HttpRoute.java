package gg.amy.autumn.web.http;

import org.immutables.value.Value.Immutable;

import java.lang.invoke.MethodHandle;

/**
 * @author amy
 * @since 5/1/21.
 */
@Immutable
public interface HttpRoute extends SimpleHttpRoute {
    Object object();

    MethodHandle method();
}

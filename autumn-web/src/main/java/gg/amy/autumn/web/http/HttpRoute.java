package gg.amy.autumn.web.http;

import org.immutables.value.Value.Immutable;

import javax.annotation.Nonnull;
import java.lang.invoke.MethodHandle;

/**
 * A fleshed-out HTTP route. For internal use.
 *
 * @author amy
 * @since 5/1/21.
 */
@Immutable
public interface HttpRoute extends SimpleHttpRoute {
    @Nonnull
    Object object();

    @Nonnull
    MethodHandle method();
}

package gg.amy.autumn.web.http;

import org.immutables.value.Value.Immutable;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;

/**
 * @author amy
 * @since 5/1/21.
 */
@Immutable
public interface HttpRoute extends SimpleHttpRoute {
    Object object();

    Method method();
}

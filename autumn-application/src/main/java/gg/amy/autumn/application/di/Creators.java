package gg.amy.autumn.application.di;

import gg.amy.autumn.di.annotation.Creator;
import gg.amy.autumn.di.annotation.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * Built-in {@link Creator} methods. Not for use via means other than
 * {@link Inject}.
 *
 * @author amy
 * @since 5/1/21.
 */
public final class Creators {
    private Creators() {
    }

    @Creator
    public static Logger createLogger(@Nonnull final Class<?> cls) {
        return LoggerFactory.getLogger(cls);
    }
}

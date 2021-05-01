package gg.amy.autumn.application;

import gg.amy.autumn.di.AutumnDI;
import gg.amy.autumn.di.annotation.Creator;
import gg.amy.autumn.di.annotation.Inject;
import gg.amy.autumn.di.annotation.Run;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author amy
 * @since 5/1/21.
 */
public final class AutumnApplication {
    // bleh
    @SuppressWarnings("StaticVariableOfConcreteClass")
    private static AutumnDI DI;
    @Inject
    private Logger logger;

    private AutumnApplication() {
    }

    public static void run() {
        DI = new AutumnDI().loadComponents().initSingletons().finish();
        final var app = new AutumnApplication();
        DI.injectComponents(app);
        app.boot();
    }

    @Creator
    public static AutumnDI createDI(@Nonnull final Class<?> __) {
        return DI;
    }

    private void boot() {
        logger.info("Running @Run methods...");
        DI.singletons().values().forEach(o -> {
            for(final var m : o.getClass().getDeclaredMethods()) {
                if(m.isAnnotationPresent(Run.class)) {
                    try {
                        m.invoke(o);
                    } catch(@Nonnull final Throwable e) {
                        logger.error("Encountered error with @Run method {}#{}:", o.getClass().getName(), m.getName(), e);
                    }
                }
            }
        });

        final var startTime = ManagementFactory.getRuntimeMXBean().getStartTime();
        logger.info("Booted Autumn application (delta={}ms).", System.currentTimeMillis() - startTime);
    }
}

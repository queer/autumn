package gg.amy.autumn.application;

import gg.amy.autumn.application.annotation.Run;
import gg.amy.autumn.di.AutumnDI;
import gg.amy.autumn.di.annotation.Creator;
import gg.amy.autumn.di.annotation.Init;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.StackWalker.Option;
import java.lang.management.ManagementFactory;

/**
 * The entrypoint of an Autumn application. Autumn applications have two phases
 * to them:
 * <ol>
 *     <li>Loading phase, via {@link #load()}. This phase loads all components,
 *     invokes {@link Init} methods, and does other related tasks.</li>
 *     <li>Running phase. {@link #run()} will first call {@link #load()} and
 *     then will actually boot the application. The boot process is really just
 *     calling all the {@link Run} methods.</li>
 * </ol>
 *
 * @author amy
 * @since 5/1/21.
 */
public final class AutumnApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutumnApplication.class);
    // bleh
    @SuppressWarnings("StaticVariableOfConcreteClass")
    private static final AutumnDI DI = new AutumnDI();
    private static boolean LOADED;
    private static boolean RUNNING;

    private AutumnApplication() {
    }

    public static void load() {
        if(LOADED) {
            return;
        }
        LOADED = true;
        LOGGER.info("Booting new Autumn application...");

        final var stackWalker = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE);
        // Safe
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        final var caller = stackWalker.walk(s -> s.skip(1).findFirst()).get().getDeclaringClass();
        LOGGER.info("Booting from: {}.", caller.getName());

        DI.loadComponents().initSingletons().finish();
    }

    public static void run() {
        if(RUNNING) {
            return;
        }
        RUNNING = true;
        load();
        final var app = new AutumnApplication();
        DI.injectComponents(app);
        app.boot();
    }

    @Creator
    public static AutumnDI createDI(@Nonnull final Class<?> __) {
        return DI;
    }

    private void boot() {
        LOGGER.info("Running all components...");
        DI.singletons().values().forEach(o -> {
            for(final var m : o.getClass().getDeclaredMethods()) {
                if(m.isAnnotationPresent(Run.class)) {
                    try {
                        m.invoke(o);
                    } catch(@Nonnull final Throwable e) {
                        LOGGER.error("Encountered error with @Run method {}#{}:", o.getClass().getName(), m.getName(), e);
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        final var startTime = ManagementFactory.getRuntimeMXBean().getStartTime();
        LOGGER.info("Booted Autumn application (delta={}ms).", System.currentTimeMillis() - startTime);
    }
}

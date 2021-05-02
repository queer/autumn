package gg.amy.autumn.application;

import gg.amy.autumn.application.annotation.Run;
import gg.amy.autumn.di.AutumnDI;
import gg.amy.autumn.di.annotation.Creator;
import gg.amy.autumn.di.annotation.Init;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.lang.StackWalker.Option;
import java.lang.management.ManagementFactory;

/**
 * The entrypoint of an Autumn application. Autumn applications have two phases
 * to them:
 * <ol>
 *     <li>Loading phase, via {@link #bootstrap()}. This phase loads all components,
 *     invokes {@link Init} methods, and does other related tasks.</li>
 *     <li>Running phase. {@link #run()} will first call {@link #bootstrap()} and
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
    private static boolean BOOTSTRAPPED;
    private static boolean RUNNING;

    private AutumnApplication() {
    }

    public static void bootstrap() {
        if(BOOTSTRAPPED) {
            return;
        }
        BOOTSTRAPPED = true;
        LOGGER.info("Bootstrapping new Autumn application...");

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
        final var bootTime = System.currentTimeMillis();
        RUNNING = true;
        bootstrap();
        final var app = new AutumnApplication();
        DI.injectComponents(app);
        app.boot(bootTime);
    }

    @Creator
    public static AutumnDI createDI(@Nonnull final Class<?> __) {
        return DI;
    }

    private void boot(@Nonnegative final long bootTime) {
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
        LOGGER.info("Booted Autumn application (start={}ms, boot={}ms, full={}ms).",
                bootTime - startTime, System.currentTimeMillis() - bootTime, System.currentTimeMillis() - startTime);
    }
}

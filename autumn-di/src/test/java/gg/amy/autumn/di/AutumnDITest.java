package gg.amy.autumn.di;

import gg.amy.autumn.di.annotation.Component;
import gg.amy.autumn.di.annotation.Depends;
import gg.amy.autumn.di.annotation.Inject;
import gg.amy.autumn.di.annotation.Singleton;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author amy
 * @since 5/1/21.
 */
public class AutumnDITest {
    @Test
    public void testThatComponentInjectionWorks() {
        final var di = new AutumnDI(getClass()).loadComponents().initSingletons().finish();
        final var target = new TestTarget();
        di.injectComponents(target);
        assertNotNull(target.component);
    }

    @Test
    public void testThatSingletonComponentInjectionWorks() {
        final var di = new AutumnDI(getClass()).loadComponents().initSingletons().finish();
        final var target = new TestSingletonTarget();
        di.injectComponents(target);
        assertNotNull(target.component);
    }

    @Test
    public void testThatSingletonComponentInjectionWithDependenciesWorks() {
        final var di = new AutumnDI(getClass()).loadComponents().initSingletons().finish();
        final var target = new TestSingletonWithDepsTarget();
        di.injectComponents(target);
        assertNotNull(target.component);
        assertNotNull(target.component.component);
    }

    @SuppressWarnings("unused")
    public static class TestTarget {
        @Inject
        private TestComponent component;
    }

    @SuppressWarnings("unused")
    public static class TestSingletonTarget {
        @Inject
        private TestSingleton component;
    }

    @SuppressWarnings("unused")
    public static class TestSingletonWithDepsTarget {
        @Inject
        private TestSingletonWithDeps component;
    }

    @Component
    public static class TestComponent {
    }

    @Component
    @Singleton
    public static class TestSingleton {
    }

    @Component
    @Singleton
    @Depends(TestSingleton.class)
    public static class TestSingletonWithDeps {
        @Inject
        private TestSingleton component;
    }
}

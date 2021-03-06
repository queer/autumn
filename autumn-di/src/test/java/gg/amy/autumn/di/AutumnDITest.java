package gg.amy.autumn.di;

import gg.amy.autumn.di.annotation.*;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author amy
 * @since 5/1/21.
 */
public class AutumnDITest {
    private final AutumnDI di = new AutumnDI().init(getClass());

    @Test
    public void testThatComponentInjectionWorks() {
        final var target = new TestTarget();
        di.injectComponents(target);
        assertNotNull(target.component);
    }

    @Test
    public void testThatSingletonComponentInjectionWorks() {
        final var target = new TestSingletonTarget();
        di.injectComponents(target);
        assertNotNull(target.component);
    }

    @Test
    public void testThatSingletonComponentInjectionWithDependenciesWorks() {
        final var target = new TestSingletonWithDepsTarget();
        di.injectComponents(target);
        assertNotNull(target.component);
        assertNotNull(target.component.component);
    }

    @Test
    public void testThatCreatorMethodsWork() {
        final var target = new TestCreatorTarget();
        di.injectComponents(target);
        assertNotNull(target.component);
    }

    @Test
    public void testThatInitMethodsAreCalled() {
        final var target = new TestInitTarget();
        di.injectComponents(target);
        assertNotNull(target.component.value);
    }

    @Test
    public void testThatConfigInjectionWorks() {
        final var target = new TestConfigInjection();
        di.injectConfig(target);
        assertEquals("value", target.value);
        assertEquals("test value", target.otherValue);
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

    @SuppressWarnings("unused")
    public static class TestCreatorTarget {
        @Inject
        private TestCreatorMethod component;
    }

    @SuppressWarnings("unused")
    public static class TestInitTarget {
        @Inject
        private TestInitMethod component;
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
    @SuppressWarnings("unused")
    public static class TestSingletonWithDeps {
        @Inject
        private TestSingleton component;
    }

    @SuppressWarnings("InstantiationOfUtilityClass")
    public static final class TestCreatorMethod {
        private TestCreatorMethod() {
        }

        @Creator
        public static TestCreatorMethod supply(@Nonnull final Class<?> cls) {
            return new TestCreatorMethod();
        }
    }

    @Component
    @Singleton
    public static class TestInitMethod {
        private Object value;

        @Init
        public void init() {
            value = new Object();
        }
    }

    public static class TestConfigInjection {
        @Config("key")
        private String value;

        @Config(file = "test-config.hjson", value = "testKey")
        private String otherValue;
    }
}

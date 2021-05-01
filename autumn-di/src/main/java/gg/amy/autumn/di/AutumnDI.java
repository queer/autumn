package gg.amy.autumn.di;

import gg.amy.autumn.di.annotation.Component;
import gg.amy.autumn.di.annotation.Depends;
import gg.amy.autumn.di.annotation.Inject;
import gg.amy.autumn.di.annotation.Singleton;
import gg.amy.autumn.di.util.DirectedGraph;
import gg.amy.autumn.di.util.TopologicalSort;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author amy
 * @since 5/1/21.
 */
public final class AutumnDI {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Collection<Class<?>> components = new HashSet<>();
    private final Map<Class<?>, Object> singletons = new HashMap<>();

    private final ScanResult graph;

    public AutumnDI(@Nonnull final Class<?> base) {
        final var pkg = base.getPackageName();
        logger.info("Scanning `{}` for components...", pkg);
        graph = new ClassGraph().enableAllInfo().acceptPackages(pkg).scan();
    }

    public AutumnDI loadComponents() {
        final var singletonGraph = new DirectedGraph<Class<?>>();
        graph.getClassesWithAnnotation(Component.class.getName())
                .getNames()
                .stream()
                .map(c -> {
                    try {
                        return Class.forName(c);
                    } catch(final ClassNotFoundException e) {
                        throw new IllegalStateException(e);
                    }
                })
                .forEach(cls -> {
                    if(cls.isAnnotationPresent(Singleton.class)) {
                        singletonGraph.addNode(cls);
                        if(cls.isAnnotationPresent(Depends.class)) {
                            for(final var dep : cls.getDeclaredAnnotation(Depends.class).value()) {
                                singletonGraph.addNode(dep);
                                singletonGraph.addEdge(cls, dep);
                            }
                        }
                    } else {
                        if(cls.isAnnotationPresent(Depends.class)) {
                            throw new IllegalStateException(String.format("Class %s is annotated @Depends, but is not @Singleton!", cls.getName()));
                        }
                        components.add(cls);
                        logger.info("Loaded component: {}", cls.getName());
                    }
                });

        final var deps = TopologicalSort.sort(singletonGraph);
        Collections.reverse(deps);
        for(final var dep : deps) {
            try {
                singletons.put(dep, dep.getDeclaredConstructor().newInstance());
                logger.info("Loaded new singleton component: {}", dep.getName());
            } catch(final InstantiationException | NoSuchMethodException | InvocationTargetException
                    | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public AutumnDI initSingletons() {
        singletons.values().forEach(this::injectComponents);
        return this;
    }

    public AutumnDI finish() {
        logger.info("Done!");
        graph.close();
        return this;
    }

    public final void injectComponents(final Object component) {
        injectComponents(component, new HashMap<>());
    }

    public final void injectComponents(final Object object, final Map<Class<?>, ?> ctx) {
        for(final var field : object.getClass().getDeclaredFields()) {
            try {
                if(field.isAnnotationPresent(Inject.class)) {
                    field.setAccessible(true);
                    final Class<?> type = field.getType();
                    final Optional<Class<?>> ctxMatch = ctx.keySet().stream().filter(type::isAssignableFrom).findFirst();
                    if(ctxMatch.isPresent()) {
                        field.set(object, ctx.get(ctxMatch.get()));
                    } else {
                        final Optional<?> located = getComponent(type, ctx);
                        if(located.isPresent()) {
                            field.set(object, located.get());
                        } else {
                            logger.error("Couldn't inject component of type `{}` into {}#{}: no such component found!",
                                    type.getName(), object.getClass().getName(), field.getName());
                        }
                    }
                }
            } catch(final IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public final <T> Optional<T> getComponent(final Class<T> cls) {
        return getComponent(cls, new HashMap<>());
    }

    @SuppressWarnings({"unchecked", "DuplicatedCode"})
    public final <T> Optional<T> getComponent(final Class<T> cls, final Map<Class<?>, ?> ctx) {
        if(singletons.containsKey(cls)) {
            return Optional.of((T) singletons.get(cls));
        } else {
            final Optional<Class<?>> first = singletons.keySet().stream().filter(cls::isAssignableFrom).findFirst();
            if(first.isPresent()) {
                return Optional.of((T) singletons.get(first.get()));
            } else {
                final Optional<Class<?>> comp = components.stream().filter(cls::isAssignableFrom).findFirst();
                if(comp.isPresent()) {
                    try {
                        final Object instance = comp.get().getDeclaredConstructor().newInstance();
                        injectComponents(instance, ctx);
                        return Optional.of((T) instance);
                    } catch(final InstantiationException | NoSuchMethodException | InvocationTargetException
                            | IllegalAccessException e) {
                        throw new IllegalStateException(e);
                    }
                } else {
                    return Optional.empty();
                }
            }
        }
    }
}

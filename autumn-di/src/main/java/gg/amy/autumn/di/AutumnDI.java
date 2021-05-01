package gg.amy.autumn.di;

import gg.amy.autumn.di.annotation.*;
import gg.amy.autumn.di.util.DirectedGraph;
import gg.amy.autumn.di.util.TopologicalSort;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;

/**
 * @author amy
 * @since 5/1/21.
 */
@SuppressWarnings("unused")
public final class AutumnDI {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Collection<Class<?>> components = new HashSet<>();
    private final Map<Class<?>, Object> singletons = new HashMap<>();
    private final Map<Class<?>, Function<Class<?>, Object>> creators = new HashMap<>();
    private final ScanResult graph;

    private Phase phase = Phase.BOOT;

    public AutumnDI() {
        logger.info("Scanning for components...");
        graph = new ClassGraph().enableAllInfo().scan();
    }

    public AutumnDI loadComponents() {
        if(phase != Phase.BOOT) {
            throw new IllegalStateException("Cannot load components when phase is not BOOT.");
        }
        phase = Phase.SCAN;

        // Load components
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
                            throw new IllegalStateException(String.format("Class %s is annotated @Depends, but is not @Singleton.", cls.getName()));
                        }
                        components.add(cls);
                        logger.info("Loaded component: {}", cls.getName());
                    }
                });

        // Init singletons in dependency ordering
        final var deps = TopologicalSort.sort(singletonGraph);
        Collections.reverse(deps);
        for(final var dep : deps) {
            try {
                singletons.put(dep, dep.getDeclaredConstructor().newInstance());
                logger.info("Loaded new singleton component: {}.", dep.getName());
            } catch(final InstantiationException | NoSuchMethodException | InvocationTargetException
                    | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        // Load creator methods
        graph.getClassesWithMethodAnnotation(Creator.class.getName())
                .getNames()
                .stream()
                .map(c -> {
                    try {
                        return Class.forName(c);
                    } catch(final ClassNotFoundException e) {
                        throw new IllegalStateException(e);
                    }
                })
                .forEach(c -> {
                    final var lookup = MethodHandles.lookup();
                    for(final var m : c.getDeclaredMethods()) {
                        if(m.isAnnotationPresent(Creator.class)) {
                            if(!Modifier.isStatic(m.getModifiers())) {
                                logger.error("Couldn't load @Creator method {}#{}: Method is not static.", c.getName(), m.getName());
                                continue;
                            }
                            if(m.getParameterCount() != 1 || !m.getParameterTypes()[0].equals(Class.class)) {
                                logger.error("Couldn't load @Creator method {}#{}: Method does not take a single `Class<?>` param.", c.getName(), m.getName());
                                continue;
                            }
                            try {
                                m.setAccessible(true);
                                final var handle = lookup.unreflect(m);
                                final var returnType = m.getReturnType();
                                creators.put(returnType, target -> {
                                    try {
                                        return handle.invoke(target);
                                    } catch(final Throwable e) {
                                        throw new IllegalStateException(e);
                                    }
                                });
                            } catch(final IllegalAccessException e) {
                                logger.error("Couldn't load @Creator method {}#{}:", c.getName(), m.getName(), e);
                            }
                            logger.info("Loaded @Creator method {}#{}.", c.getName(), m.getName());
                        }
                    }
                });
        return this;
    }

    public AutumnDI initSingletons() {
        if(phase != Phase.SCAN) {
            throw new IllegalStateException("Cannot init singletons when phase is not SCAN.");
        }
        phase = Phase.INJECT;
        singletons.values().forEach(s -> {
            injectComponents(s);
            Arrays.stream(s.getClass().getDeclaredMethods()).filter(m -> m.isAnnotationPresent(Init.class)).forEach(m -> {
                m.setAccessible(true);
                try {
                    m.invoke(s);
                } catch(@Nonnull final Throwable e) {
                    logger.error("Couldn't call @Init method {}#{}:", s.getClass().getName(), m.getName(), e);
                    throw new RuntimeException(e);
                }
            });
        });
        return this;
    }

    public AutumnDI finish() {
        if(phase != Phase.INJECT) {
            throw new IllegalStateException("Cannot finish DI work when phase is not INJECT.");
        }
        phase = Phase.DONE;
        logger.info("Done.");
        graph.close();
        return this;
    }

    /**
     * @see #injectComponents(Object, Map)
     */
    public final void injectComponents(final Object component) {
        injectComponents(component, new HashMap<>());
    }

    /**
     * <p>Injects components into the specified object, using the supplied
     * context map.</p>
     * <p>Any fields in the passed object that are annotated with
     * {@link Inject} will automatically have values injected into them.
     * Injection will load values from the context map first, if possible, and
     * only if it cannot find a value in the context will it fall back to the
     * standard component loading flow.</p>
     *
     * @param object The object into which values will be injected.
     * @param ctx    The injection context.
     * @see #getComponent(Class, Class, Map)
     */
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
                        final Optional<?> located = getComponent(object.getClass(), type, ctx);
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

    /**
     * @see #getComponent(Class, Class, Map)
     */
    @Nonnull
    public final <T, E> Optional<T> getComponent(@Nonnull final Class<E> source, @Nonnull final Class<T> cls) {
        return getComponent(source, cls, new HashMap<>());
    }

    /**
     * <p>Locates a component of the given type, first searching the context
     * map and then falling back onto singleton components, instanced
     * components, and finally creator methods.</p>
     *
     * @param cls The component type to find.
     * @param ctx The context map.
     * @param <T> The type of the class.
     * @return An {@link Optional} that may contain a matching component.
     */
    @Nonnull
    @SuppressWarnings({"unchecked", "DuplicatedCode"})
    public final <T, E> Optional<T> getComponent(@Nonnull final Class<E> source, @Nonnull final Class<T> cls,
                                                 @Nonnull final Map<Class<?>, ?> ctx) {
        // Search context
        if(ctx.containsKey(cls)) {
            return Optional.of((T) ctx.get(cls));
        }
        final var fuzzyCtxMatch = ctx.keySet().stream().filter(cls::isAssignableFrom).findFirst();
        if(fuzzyCtxMatch.isPresent()) {
            return Optional.of((T) ctx.get(fuzzyCtxMatch.get()));
        }

        // Search singletons
        if(singletons.containsKey(cls)) {
            return Optional.of((T) singletons.get(cls));
        }
        final var fuzzyMatch = singletons.keySet().stream().filter(cls::isAssignableFrom).findFirst();
        if(fuzzyMatch.isPresent()) {
            return Optional.of((T) singletons.get(fuzzyMatch.get()));
        }

        // Search instanced components
        final Optional<Class<?>> comp;
        if(components.contains(cls)) {
            comp = Optional.of(cls);
        } else {
            comp = components.stream().filter(cls::isAssignableFrom).findFirst();
        }

        if(comp.isPresent()) {
            try {
                final Object instance = comp.get().getDeclaredConstructor().newInstance();
                injectComponents(instance, ctx);
                return Optional.of((T) instance);
            } catch(final InstantiationException | NoSuchMethodException | InvocationTargetException
                    | IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }

        // Search creator methods
        if(creators.containsKey(cls)) {
            return Optional.of((T) creators.get(cls).apply(source));
        }
        final var fuzzyCreatorMatch = creators.keySet().stream().filter(cls::isAssignableFrom).findFirst();
        //noinspection OptionalIsPresent
        if(fuzzyCreatorMatch.isPresent()) {
            return Optional.of((T) creators.get(fuzzyCreatorMatch.get()).apply(source));
        }

        // Give up
        return Optional.empty();
    }

    @Nonnull
    public ScanResult graph() {
        if(phase == Phase.DONE) {
            throw new IllegalStateException("Cannot access class graph when phase is DONE.");
        }
        return graph;
    }

    @Nonnull
    public Map<Class<?>, Object> singletons() {
        return singletons;
    }

    private enum Phase {
        BOOT,
        SCAN,
        INJECT,
        DONE,
    }
}

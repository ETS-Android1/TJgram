package com.alexvasilkov.events.internal;

import android.util.Log;

import com.alexvasilkov.events.Events.Background;
import com.alexvasilkov.events.Events.Cache;
import com.alexvasilkov.events.Events.Failure;
import com.alexvasilkov.events.Events.Result;
import com.alexvasilkov.events.Events.Status;
import com.alexvasilkov.events.Events.Subscribe;
import com.alexvasilkov.events.EventsException;
import com.alexvasilkov.events.cache.CacheProvider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

class EventMethodsHelper {

    private static final Map<Class<?>, List<EventMethod>> cacheStatic = new HashMap<>();
    private static final Map<Class<?>, List<EventMethod>> cacheInstance = new HashMap<>();

    private EventMethodsHelper() {
        // No instances
    }

    /**
     * Returns list of annotated methods for given class.
     * This list will be cached on per-class basis to avoid costly annotations look up.
     */
    static List<EventMethod> getMethodsForTarget(@NonNull Object target) {
        if (target instanceof Class) {
            return getMethodsFromClass((Class<?>) target, true);
        } else {
            return getMethodsFromClass(target.getClass(), false);
        }
    }

    private static List<EventMethod> getMethodsFromClass(Class<?> clazz, boolean statics) {
        Map<Class<?>, List<EventMethod>> cache = statics ? cacheStatic : cacheInstance;
        List<EventMethod> methods = cache.get(clazz);

        if (methods == null) {
            long start = System.nanoTime();

            methods = collectMethodsRecursively(clazz, cache, statics);

            if (EventsParams.isDebug()) {
                long time = System.nanoTime() - start;
                Log.d(Utils.TAG, String.format("Collecting %d methods of %s in %.3f ms",
                        methods.size(), clazz.getName(), time / 1e6d));
            }
        }

        return methods;
    }

    @NonNull
    private static List<EventMethod> collectMethodsRecursively(Class<?> clazz,
            Map<Class<?>, List<EventMethod>> cache, boolean statics) {

        List<EventMethod> list = cache.get(clazz);

        if (list != null) {
            return list;
        } else if (clazz.getName().startsWith("android.") || clazz.getName().startsWith("java.")) {
            // Ignoring system classes
            return Collections.emptyList();
        } else {
            list = new ArrayList<>();

            // First adding all methods from super classes
            if (clazz.getSuperclass() != null) {
                List<EventMethod> superList = collectMethodsRecursively(
                        clazz.getSuperclass(), cache, statics);
                list.addAll(superList);
            }

            // Now collecting methods from current class and store result in cache
            collectMethods(clazz, list, statics);
            cache.put(clazz, list);
            return list;
        }
    }

    private static void collectMethods(Class<?> clazz, List<EventMethod> list, boolean statics) {
        // Looking for methods annotated as event handlers
        Method[] methods = clazz.getDeclaredMethods();
        EventMethod info;

        for (Method m : methods) {
            if (Modifier.isStatic(m.getModifiers()) != statics) {
                continue;
            }

            info = null;

            if (m.isAnnotationPresent(Subscribe.class)) {

                checkNoAnnotations(m, Subscribe.class, Status.class, Result.class, Failure.class);

                // No method's parameters check is required here since any combination is valid

                String key = m.getAnnotation(Subscribe.class).value();

                boolean isBack = m.isAnnotationPresent(Background.class);
                boolean isSingle = isBack && m.getAnnotation(Background.class).singleThread();
                boolean hasReturn = !m.getReturnType().equals(Void.TYPE);

                CacheProvider cache = getCacheProvider(m);

                info = new EventMethod(m, EventMethod.Type.SUBSCRIBE, key, statics, hasReturn,
                        isBack, isSingle, cache);

            } else if (m.isAnnotationPresent(Status.class)) {

                checkNoAnnotations(m, Status.class, Subscribe.class, Background.class, Cache.class,
                        Result.class, Failure.class);

                checkNoReturn(m, Status.class);

                String key = m.getAnnotation(Status.class).value();
                info = new EventMethod(m, EventMethod.Type.STATUS, key, statics);

            } else if (m.isAnnotationPresent(Result.class)) {

                checkNoAnnotations(m, Result.class, Subscribe.class, Background.class, Cache.class,
                        Status.class, Failure.class);

                checkNoReturn(m, Result.class);

                String key = m.getAnnotation(Result.class).value();
                info = new EventMethod(m, EventMethod.Type.RESULT, key, statics);

            } else if (m.isAnnotationPresent(Failure.class)) {

                checkNoAnnotations(m, Failure.class, Subscribe.class, Background.class, Cache.class,
                        Status.class, Result.class);

                checkNoReturn(m, Failure.class);

                String key = m.getAnnotation(Failure.class).value();
                info = new EventMethod(m, EventMethod.Type.FAILURE, key, statics);

            } else if (m.isAnnotationPresent(Background.class)
                    || m.isAnnotationPresent(Cache.class)) {

                throw new EventsException("Method " + Utils.methodToString(m)
                        + " should be marked with @" + Subscribe.class.getSimpleName());

            }

            if (info != null) {
                list.add(info);
            }
        }

        // Only static methods can be executed in background, to not leak object references
        for (Method m : methods) {
            if (!Modifier.isStatic(m.getModifiers()) && m.isAnnotationPresent(Background.class)) {
                throw new EventsException("Method " + Utils.methodToString(m)
                        + " marked with @" + Background.class.getSimpleName() + " should be static."
                        + " To subscribe static methods pass Class object to Events.register()");
            }
        }
    }


    // Checks that no given annotations are present on given method
    @SafeVarargs
    private static void checkNoAnnotations(Method method, Class<? extends Annotation> foundAn,
            Class<? extends Annotation>... disallowedAn) {

        for (Class<? extends Annotation> an : disallowedAn) {
            if (method.isAnnotationPresent(an)) {
                throw new EventsException("Method " + Utils.methodToString(method)
                        + " marked with @" + foundAn.getSimpleName()
                        + " cannot be marked with @" + an.getSimpleName());
            }
        }
    }

    private static void checkNoReturn(Method method, Class<? extends Annotation> an) {
        if (!method.getReturnType().equals(Void.TYPE)) {
            throw new EventsException("Method " + Utils.methodToString(method)
                    + " marked with @" + an.getSimpleName() + " can only have void return type.");
        }
    }

    // Retrieves cache provider instance for method
    private static CacheProvider getCacheProvider(Method javaMethod) {
        if (!javaMethod.isAnnotationPresent(Cache.class)) {
            return null;
        }

        Cache an = javaMethod.getAnnotation(Cache.class);
        Class<? extends CacheProvider> cacheClazz = an.value();

        try {
            Constructor<? extends CacheProvider> constructor = cacheClazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new EventsException("Cannot instantiate cache provider "
                    + cacheClazz.getSimpleName() + " for method "
                    + Utils.methodToString(javaMethod), e);
        }
    }
}
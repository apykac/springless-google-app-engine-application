package com.ticket_pipeline.simple_context.init;

import com.ticket_pipeline.simple_context.Destroy;
import com.ticket_pipeline.simple_context.Init;
import com.ticket_pipeline.simple_context.exception.InitContextRuntimeException;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CollectorCommon {
    private CollectorCommon() {
    }

    public static Relation collectRelations(Class<?> clazz) {
        Relation relation = new Relation();
        collectRelations(clazz, relation.getParents(), relation.getInterfaces());
        return relation;
    }

    private static void collectRelations(Class<?> clazz, Set<Class<?>> parents, Set<Class<?>> interfaces) {
        if (clazz == Object.class || clazz == null) {
            return;
        }
        Class<?> parent = clazz.getSuperclass();
        if (parent != Object.class && parent != null) {
            parents.add(parent);
        }
        Class<?>[] interfacesArr = clazz.getInterfaces();
        if (interfacesArr.length != 0) {
            interfaces.addAll(Arrays.asList(interfacesArr));
        }

        collectRelations(clazz.getSuperclass(), parents, interfaces);
    }

    public static class Relation {
        private final Set<Class<?>> interfaces = new HashSet<>();
        private final Set<Class<?>> parents = new HashSet<>();

        public Set<Class<?>> getInterfaces() {
            return interfaces;
        }

        public Set<Class<?>> getParents() {
            return parents;
        }
    }

    public static InitDestroyConfig collectInitDestroyMethods(Class<?> clazz) {
        String initId = "@Init";
        String destroyId = "@Destroy";
        InitDestroyConfig initDestroyConfig = new InitDestroyConfig();
        for (Method method : clazz.getDeclaredMethods()) {
            Init init = method.getAnnotation(Init.class);
            Destroy destroy = method.getAnnotation(Destroy.class);
            if (init != null) {
                checkMethodUnique(initDestroyConfig.getInitMethod(), initId);
                checkMethodModifiers(method, initId);
                checkMethodParams(method, initId);
                initDestroyConfig.addInit(init, method);
            }
            if (destroy != null) {
                checkMethodUnique(initDestroyConfig.getDestroyMethod(), destroyId);
                checkMethodModifiers(method, destroyId);
                checkMethodParams(method, destroyId);
                initDestroyConfig.addDestroy(destroy, method);
            }
        }
        return initDestroyConfig;
    }

    private static void checkMethodUnique(Method method, String id) {
        if (method != null) {
            throw new InitContextRuntimeException("Only one method can be annotated by " + id);
        }
    }

    private static void checkMethodModifiers(Method method, String id) {
        int modifiers = method.getModifiers();
        if (Modifier.isFinal(modifiers)) {
            throw new InitContextRuntimeException("Method can't be final annotated by " + id);
        }
        if (Modifier.isStatic(modifiers)) {
            throw new InitContextRuntimeException("Method can't be static annotated by " + id);
        }
    }

    private static void checkMethodParams(Method method, String id) {
        Class<?>[] params = method.getParameterTypes();
        if (params != null && params.length != 0) {
            throw new InitContextRuntimeException("Method can't have any parameters annotated by " + id);
        }
    }

    public static class InitDestroyConfig {
        private Init init;
        private Destroy destroy;
        private Method initMethod;
        private Method destroyMethod;

        private void addInit(Init init, Method initMethod) {
            this.init = init;
            this.initMethod = initMethod;
        }

        private void addDestroy(Destroy destroy, Method destroyMethod) {
            this.destroy = destroy;
            this.destroyMethod = destroyMethod;
        }

        public Method getInitMethod() {
            return initMethod;
        }

        public Method getDestroyMethod() {
            return destroyMethod;
        }

        public int getInitOrder() {
            return init == null ? 0 : init.value();
        }

        public int getDestroyOrder() {
            return destroy == null ? 0 : destroy.value();
        }
    }
}

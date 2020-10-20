package com.roundrobin_assignment.ticketpipeline.config.context.init;

import com.roundrobin_assignment.ticketpipeline.config.context.BeanModel;
import com.roundrobin_assignment.ticketpipeline.config.context.Component;
import com.roundrobin_assignment.ticketpipeline.config.context.Destroy;
import com.roundrobin_assignment.ticketpipeline.config.context.Init;
import com.roundrobin_assignment.ticketpipeline.exception.InitContextRuntimeException;
import com.roundrobin_assignment.ticketpipeline.util.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ComponentCollector {
    private ComponentCollector() {
    }

    public static Map<String, BeanModel> beanModelMap(List<Class<?>> classes) {
        return classes.stream()
                .filter(clazz -> clazz.getAnnotation(Component.class) != null)
                .map(ComponentCollector::getBeanModel)
                .collect(HashMap::new,
                        (map, beanModel) -> {
                            String beanName = beanModel.getName();
                            BeanModel beanModelFromMap = map.get(beanName);
                            if (beanModelFromMap != null) {
                                throw new InitContextRuntimeException("Bean duplicate by name: " + beanName);
                            } else {
                                map.put(beanName, beanModel);
                            }
                        },
                        HashMap::putAll);
    }

    private static BeanModel getBeanModel(Class<?> clazz) {
        Component component = clazz.getAnnotation(Component.class);
        String beanName = StringUtils.hasText(component.value()) ? clazz.getName() + "@" + component.value() : clazz.getName();
        Constructor<?> constructor = resolveConstructor(clazz);
        CollectorCommon.Relation relation = CollectorCommon.collectRelations(clazz);
        InitDestroyConfig initDestroyConfig = collectInitDestroyMethods(clazz);
        return new BeanModel(beanName,
                clazz,
                constructor,
                Arrays.asList(constructor.getParameterTypes()),
                Arrays.asList(constructor.getGenericParameterTypes()),
                relation.getInterfaces(),
                relation.getParents(),
                initDestroyConfig.getInitMethod(),
                initDestroyConfig.getDestroyMethod(),
                initDestroyConfig.getInitOrder(),
                initDestroyConfig.getDestroyOrder());
    }

    private static Constructor<?> resolveConstructor(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        if (constructors.length == 0) {
            throw new InitContextRuntimeException("Can't find constructor for class '" + clazz.getName() + '\'');
        } else if (constructors.length == 1) {
            return constructors[0];
        } else {
            List<Constructor<?>> constructorList = Arrays.stream(constructors)
                    .filter(constructor -> constructor.getAnnotation(com.roundrobin_assignment.ticketpipeline.config.context.Constructor.class) != null)
                    .collect(Collectors.toList());
            if (constructorList.isEmpty()) {
                throw new InitContextRuntimeException("On of constructors must be annotated by @Constructor");
            }
            if (constructorList.size() > 1) {
                throw new InitContextRuntimeException("Only one constructor can't be annotated by @Constructor");
            }
            return constructorList.get(0);
        }
    }

    private static InitDestroyConfig collectInitDestroyMethods(Class<?> clazz) {
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

    private static class InitDestroyConfig {
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

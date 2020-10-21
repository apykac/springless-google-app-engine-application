package com.ticket_pipeline.simple_context.init;

import com.ticket_pipeline.simple_context.Bean;
import com.ticket_pipeline.simple_context.BeanModel;
import com.ticket_pipeline.simple_context.Configuration;
import com.ticket_pipeline.simple_context.exception.InitContextRuntimeException;
import com.ticket_pipeline.simple_utils.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfigurationCollector {
    private ConfigurationCollector() {
    }

    public static Map<String, BeanModel> beanModelMap(List<Class<?>> classes) {
        return classes.stream()
                .filter(clazz -> clazz.getAnnotation(Configuration.class) != null)
                .map(ConfigurationCollector::getBeanModel)
                .flatMap(Collection::stream)
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

    private static List<BeanModel> getBeanModel(Class<?> clazz) {
        Object configClass;
        try {
            configClass = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new InitContextRuntimeException("Can't create config bean " + clazz.getName() + " cause: " + e.getMessage(), e);
        }
        Method[] methods = clazz.getDeclaredMethods();
        if (methods == null || methods.length == 0) {
            return Collections.emptyList();
        }

        return Arrays.stream(methods)
                .filter(method -> method.getAnnotation(Bean.class) != null)
                .map(method -> ConfigurationCollector.getBeanModel(method, configClass))
                .collect(Collectors.toList());
    }

    private static BeanModel getBeanModel(Method method, Object configClass) {
        Bean bean = method.getAnnotation(Bean.class);
        Class<?> beanType = method.getReturnType();
        CollectorCommon.Relation relation = CollectorCommon.collectRelations(beanType);
        if (!beanType.isInterface()) {
            String beanName = StringUtils.hasText(bean.value()) ? beanType.getName() + "@" + bean.value() : beanType.getName();
            CollectorCommon.InitDestroyConfig initDestroyConfig = CollectorCommon.collectInitDestroyMethods(beanType);
            return new BeanModel(beanName,
                    beanType,
                    method,
                    configClass,
                    Arrays.asList(method.getParameterTypes()),
                    Arrays.asList(method.getGenericParameterTypes()),
                    relation.getInterfaces(),
                    relation.getParents(),
                    initDestroyConfig.getInitMethod(),
                    initDestroyConfig.getDestroyMethod(),
                    initDestroyConfig.getInitOrder(),
                    initDestroyConfig.getDestroyOrder());
        } else {
            String beanName = StringUtils.hasText(bean.value()) ? beanType.getName() + "@" + bean.value() : beanType.getName() + "@" + method.getName();
            return new BeanModel(beanName,
                    null,
                    method,
                    configClass,
                    Arrays.asList(method.getParameterTypes()),
                    Arrays.asList(method.getGenericParameterTypes()),
                    relation.getInterfaces(),
                    relation.getParents(),
                    null,
                    null,
                    0,
                    0);
        }
    }
}

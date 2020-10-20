package com.roundrobin_assignment.ticketpipeline.config.context.init;

import com.roundrobin_assignment.ticketpipeline.config.context.Bean;
import com.roundrobin_assignment.ticketpipeline.config.context.BeanModel;
import com.roundrobin_assignment.ticketpipeline.config.context.Configuration;
import com.roundrobin_assignment.ticketpipeline.exception.InitContextRuntimeException;
import com.roundrobin_assignment.ticketpipeline.util.StringUtils;

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
        String beanName = StringUtils.hasText(bean.value()) ? beanType.getName() + "@" + bean.value() : beanType.getName();
        CollectorCommon.Relation relation = CollectorCommon.collectRelations(beanType);
        return new BeanModel(beanName,
                beanType,
                method,
                configClass,
                Arrays.asList(method.getParameterTypes()),
                Arrays.asList(method.getGenericParameterTypes()),
                relation.getInterfaces(),
                relation.getParents());
    }
}

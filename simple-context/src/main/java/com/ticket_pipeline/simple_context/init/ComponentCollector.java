package com.ticket_pipeline.simple_context.init;

import com.ticket_pipeline.simple_context.BeanModel;
import com.ticket_pipeline.simple_context.Component;
import com.ticket_pipeline.simple_context.exception.InitContextRuntimeException;
import com.ticket_pipeline.simple_utils.StringUtils;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ticket_pipeline.simple_context.init.CollectorCommon.collectInitDestroyMethods;

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
        CollectorCommon.InitDestroyConfig initDestroyConfig = collectInitDestroyMethods(clazz);
        return new BeanModel(
                beanName,
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
                    .filter(constructor -> constructor.getAnnotation(com.ticket_pipeline.simple_context.Constructor.class) != null)
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
}

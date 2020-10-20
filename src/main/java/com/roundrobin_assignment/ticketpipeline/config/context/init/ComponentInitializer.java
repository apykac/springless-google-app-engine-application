package com.roundrobin_assignment.ticketpipeline.config.context.init;

import com.roundrobin_assignment.ticketpipeline.config.context.BeanModel;
import com.roundrobin_assignment.ticketpipeline.exception.InitContextRuntimeException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ComponentInitializer {
    private ComponentInitializer() {
    }

    public static void initializeComponents(Collection<BeanModel> list) {
        List<BeanModel> initChain = resolveInitChain(list);
        initBeans(initChain);
        initChain.sort((o1, o2) -> Integer.compare(o2.getInitOrder(), o1.getInitOrder()));
        initChain.forEach(BeanModel::invokeInitMethod);
    }

    private static List<BeanModel> resolveInitChain(Collection<BeanModel> list) {
        Set<BeanModel> beanModelsWithLists = list.stream()
                .filter(beanModel ->
                        beanModel.getConstructorParameterTypes().stream()
                                .anyMatch(Collection.class::isAssignableFrom))
                .collect(Collectors.toSet());

        Deque<BeanModel> deque = list.stream().filter(beanModel -> !beanModelsWithLists.contains(beanModel)).collect(Collectors.toCollection(ArrayDeque::new));
        Set<Class<?>> resolvedComponents = new TreeSet<>(Comparator.comparing(Class::getName));

        List<BeanModel> result = new ArrayList<>(list.size());

        handleDequeue(deque, result, resolvedComponents);
        beanModelsWithLists.forEach(deque::addLast);
        if (!handleDequeue(deque, result, resolvedComponents)) {
            throw new InitContextRuntimeException("Can't find or create next beans: " + getErrorMessage(deque, resolvedComponents));
        }

        return result;
    }

    private static boolean handleDequeue(Deque<BeanModel> deque, List<BeanModel> result, Set<Class<?>> resolvedComponents) {
        BeanModel beanModel;
        int count = 0;
        while ((beanModel = deque.pollFirst()) != null && count <= deque.size() * 2) {
            if (beanModel.getConstructorParameterTypes().isEmpty() || resolvedComponents.containsAll(getWithListTypes(beanModel))) {
                result.add(beanModel);
                resolvedComponents.add(beanModel.getBeanType());
                resolvedComponents.addAll(beanModel.getInterfaces());
                resolvedComponents.addAll(beanModel.getParents());
                count = 0;
            } else {
                deque.addLast(beanModel);
                count++;
            }
        }
        if (beanModel != null) {
            deque.add(beanModel);
        }
        return count == 0;
    }

    private static List<Class<?>> getWithListTypes(BeanModel beanModel) {
        List<Class<?>> classes = beanModel.getConstructorParameterTypes();
        List<Type> types = beanModel.getConstructorParameterGenericTypes();
        List<Class<?>> result = new ArrayList<>(classes.size());
        for (int i = 0; i < classes.size(); i++) {
            if (Collection.class.isAssignableFrom(classes.get(i))) {
                result.add(getGenericClass(types.get(i), beanModel));
            } else {
                result.add(classes.get(i));
            }
        }
        return result;
    }

    private static String getErrorMessage(Deque<BeanModel> deque, Set<Class<?>> resolvedComponents) {
        StringBuilder builder = new StringBuilder();
        BeanModel beanModel;
        while ((beanModel = deque.pollFirst()) != null) {
            builder.append(System.lineSeparator())
                    .append(beanModel.getBeanType().getName())
                    .append("  cause can't find in context beans: ")
                    .append(beanModel.getConstructorParameterTypes().stream().filter(clazz -> !resolvedComponents.contains(clazz)).map(Class::getName).collect(Collectors.joining(", ")));
        }
        return builder.toString();
    }

    private static void initBeans(List<BeanModel> initChain) {
        Set<BeanModel> initializedBeans = new HashSet<>();
        Set<BeanModel> notInitializedBeans = new HashSet<>(initChain);
        Deque<BeanModel> deque = new ArrayDeque<>(initChain);

        BeanModel beanModel;
        while ((beanModel = deque.pollFirst()) != null) {
            if (!initBean(beanModel, initializedBeans, notInitializedBeans)) {
                deque.addLast(beanModel);
            }
        }
    }

    private static boolean initBean(BeanModel beanModel, Set<BeanModel> initializedBeans, Set<BeanModel> notInitializedBeans) {
        try {
            if (beanModel.getConstructorParameterTypes().isEmpty()) {
                beanModel.createBean();
            } else {
                Object[] args = getArgs(beanModel, initializedBeans, notInitializedBeans);
                beanModel.createBean(args);
            }
            initializedBeans.add(beanModel);
            notInitializedBeans.remove(beanModel);
            return true;
        } catch (CollectionInitException e) {
            return false;
        } catch (Exception e) {
            throw new InitContextRuntimeException("Can't init bean: " + beanModel.getName() + " cause: " + e.getMessage(), e);
        }
    }

    private static Object[] getArgs(BeanModel beanModel, Set<BeanModel> initializedBeans, Set<BeanModel> notInitializedBeans)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, CollectionInitException {
        List<Class<?>> classes = beanModel.getConstructorParameterTypes();
        List<Type> types = beanModel.getConstructorParameterGenericTypes();
        Object[] result = new Object[classes.size()];
        for (int i = 0; i < result.length; i++) {
            Class<?> clazz = classes.get(i);
            Type type = types.get(i);
            if (Collection.class.isAssignableFrom(clazz)) {
                result[i] = getCollection(clazz, getGenericClass(type, beanModel), initializedBeans, notInitializedBeans);
            } else {
                result[i] = getObject(clazz, initializedBeans);
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static Object getCollection(Class<?> collectionClass, Class<?> elementType, Set<BeanModel> initializedBeans, Set<BeanModel> notInitializedBeans)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, CollectionInitException {
        Collection<Object> collection;
        if (collectionClass == List.class || collectionClass == Collection.class) {
            collection = ArrayList.class.getDeclaredConstructor().newInstance();
        } else if (collectionClass == Set.class) {
            collection = HashSet.class.getDeclaredConstructor().newInstance();
        } else {
            throw new InitContextRuntimeException("Unknown collection: " + collectionClass.getName());
        }

        Predicate<BeanModel> mainFilter = beanModel ->
                beanModel.getBeanType() == elementType ||
                        beanModel.getParents().contains(elementType) ||
                        beanModel.getInterfaces().contains(elementType);

        if (notInitializedBeans.stream()
                .filter(mainFilter)
                .findFirst()
                .orElse(null) != null) {
            throw new CollectionInitException();
        }

        collection.addAll(initializedBeans.stream()
                .filter(mainFilter)
                .map(BeanModel::getBean)
                .collect(Collectors.toList()));

        return collection;
    }

    private static Object getObject(Class<?> objectClass, Set<BeanModel> initializedBeans) {
        return initializedBeans.stream()
                .filter(beanModel -> beanModel.getBeanType() == objectClass ||
                        beanModel.getParents().contains(objectClass) ||
                        beanModel.getInterfaces().contains(objectClass))
                .map(BeanModel::getBean)
                .findFirst()
                .orElseThrow(() -> new InitContextRuntimeException("Can't find in context bean: " + objectClass.getName()));
    }

    private static Class<?> getGenericClass(Type type, BeanModel beanModel) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Object currentType = ((ParameterizedType) type).getActualTypeArguments()[0];
        if (currentType instanceof ParameterizedType) {
            String className = ((ParameterizedType) currentType).getRawType().getTypeName();
            try {
                return classLoader.loadClass(className);
            } catch (Exception e) {
                throw new InitContextRuntimeException("Can't load class: " + className);
            }
        } else if (currentType instanceof Class) {
            return (Class<?>) currentType;
        } else {
            throw new InitContextRuntimeException("Can't get construct args for: " + beanModel);
        }
    }

    private static class CollectionInitException extends Exception {
    }
}

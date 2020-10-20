package com.roundrobin_assignment.ticketpipeline.config.context;

import com.roundrobin_assignment.ticketpipeline.exception.InitContextRuntimeException;
import com.roundrobin_assignment.ticketpipeline.util.log.Logger;
import com.roundrobin_assignment.ticketpipeline.util.log.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class BeanModel implements Comparable<BeanModel> {
    private static final Logger LOG = LoggerFactory.getLogger(BeanModel.class);

    private final boolean isConfigBean;
    private final String name;
    private final Class<?> beanType;
    private final Constructor<?> constructor;
    private final Object configComponent;
    private final Method buildMethod;
    private final List<Class<?>> constructorParameterTypes;
    private final List<Type> constructorParameterGenericTypes;
    private final Set<Class<?>> interfaces;
    private final Set<Class<?>> parents;
    private final Method initMethod;
    private final Method destroyMethod;
    private final int destroyOrder;
    private final int initOrder;

    private Object bean;

    public BeanModel(String name,
                     Class<?> beanType,
                     Constructor<?> constructor,
                     List<Class<?>> constructorParameterTypes,
                     List<Type> constructorParameterGenericTypes,
                     Set<Class<?>> interfaces,
                     Set<Class<?>> parents,
                     Method initMethod,
                     Method destroyMethod,
                     int initOrder,
                     int destroyOrder) {
        isConfigBean = false;
        buildMethod = initMethod;
        configComponent = new Object();

        this.name = name;
        this.beanType = beanType;
        this.constructor = constructor;
        this.constructorParameterTypes = constructorParameterTypes;
        this.constructorParameterGenericTypes = constructorParameterGenericTypes;
        this.interfaces = interfaces;
        this.parents = parents;
        this.initMethod = initMethod;
        this.destroyMethod = destroyMethod;
        this.initOrder = initOrder;
        this.destroyOrder = destroyOrder;
    }

    public BeanModel(String name,
                     Class<?> beanType,
                     Method buildMethod,
                     Object configComponent,
                     List<Class<?>> constructorParameterTypes,
                     List<Type> constructorParameterGenericTypes,
                     Set<Class<?>> interfaces,
                     Set<Class<?>> parents) {
        isConfigBean = true;
        constructor = beanType.getDeclaredConstructors()[0];
        initMethod = buildMethod;
        destroyMethod = buildMethod;
        destroyOrder = 0;
        initOrder = 0;

        this.name = name;
        this.beanType = beanType;
        this.buildMethod = buildMethod;
        this.configComponent = configComponent;
        this.constructorParameterTypes = constructorParameterTypes;
        this.constructorParameterGenericTypes = constructorParameterGenericTypes;
        this.interfaces = interfaces;
        this.parents = parents;
    }

    public void createBean(Object... args) {
        try {
            LOG.debug("Start creating bean {}", () -> name);
            if (isConfigBean) {
                bean = buildMethod.invoke(configComponent, args);
            } else {
                bean = constructor.newInstance(args);
            }
            LOG.debug("Bean {} create successful", () -> name);
        } catch (Exception e) {
            throw new InitContextRuntimeException("Can't create bean " + beanType.getName() + " cause: " + e.getMessage(), e);
        }
    }

    public void invokeInitMethod() {
        try {
            if (!isConfigBean && initMethod != null) {
                LOG.debug("Start call init method for bean {}", () -> name);
                initMethod.invoke(bean);
                LOG.debug("Init method for bean {} called successful", () -> name);
            }
        } catch (Exception e) {
            throw new InitContextRuntimeException("Can't invoke init method cause: " + e.getMessage(), e);
        }
    }

    public void invokeDestroyMethod() {
        try {
            if (!isConfigBean && destroyMethod != null) {
                LOG.debug("Start call destroy method for bean {}", () -> name);
                destroyMethod.invoke(bean);
                LOG.debug("Destroy method for bean {} called successful", () -> name);
            }
        } catch (Exception e) {
            //nothing
        }
    }

    public String getName() {
        return name;
    }

    public Class<?> getBeanType() {
        return beanType;
    }

    public Set<Class<?>> getInterfaces() {
        return interfaces;
    }

    public Set<Class<?>> getParents() {
        return parents;
    }

    public Method getInitMethod() {
        return initMethod;
    }

    public Method getDestroyMethod() {
        return destroyMethod;
    }

    public int getInitOrder() {
        return initOrder;
    }

    public int getDestroyOrder() {
        return destroyOrder;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public List<Class<?>> getConstructorParameterTypes() {
        return constructorParameterTypes;
    }

    public List<Type> getConstructorParameterGenericTypes() {
        return constructorParameterGenericTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeanModel beanModel = (BeanModel) o;
        return Objects.equals(name, beanModel.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "BeanModel{" +
                "isConfigBean=" + isConfigBean +
                ", name='" + name + '\'' +
                ", beanType=" + beanType +
                ", constructor=" + constructor +
                ", configComponent=" + configComponent +
                ", buildMethod=" + buildMethod +
                ", constructorParameterTypes=" + constructorParameterTypes +
                ", constructorParameterGenericTypes=" + constructorParameterGenericTypes +
                ", interfaces=" + interfaces +
                ", parents=" + parents +
                ", initMethod=" + initMethod +
                ", destroyMethod=" + destroyMethod +
                ", destroyOrder=" + destroyOrder +
                ", initOrder=" + initOrder +
                ", bean=" + bean +
                '}';
    }

    @Override
    public int compareTo(BeanModel o) {
        return name.compareTo(o.name);
    }
}

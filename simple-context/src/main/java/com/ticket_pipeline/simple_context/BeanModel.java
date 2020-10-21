package com.ticket_pipeline.simple_context;

import com.ticket_pipeline.simple_context.exception.InitContextRuntimeException;
import com.ticket_pipeline.simple_context.init.CollectorCommon;
import com.ticket_pipeline.simple_utils.log.Logger;
import com.ticket_pipeline.simple_utils.log.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class BeanModel implements Comparable<BeanModel> {
    private static final Logger LOG = LoggerFactory.getLogger(BeanModel.class);

    private final boolean isConfigBean;
    private final String name;
    private final List<Class<?>> constructorParameterTypes;
    private final List<Type> constructorParameterGenericTypes;
    private final Set<Class<?>> interfaces;
    private final Set<Class<?>> parents;

    private Constructor<?> constructor;
    private Object configComponent;
    private Method buildMethod;
    private Class<?> beanType;
    private Method initMethod;
    private Method destroyMethod;
    private int destroyOrder;
    private int initOrder;

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
                     Set<Class<?>> parents,
                     Method initMethod,
                     Method destroyMethod,
                     int initOrder,
                     int destroyOrder) {
        isConfigBean = true;

        this.name = name;
        this.beanType = beanType;
        this.buildMethod = buildMethod;
        this.configComponent = configComponent;
        this.constructorParameterTypes = constructorParameterTypes;
        this.constructorParameterGenericTypes = constructorParameterGenericTypes;
        this.interfaces = interfaces;
        this.parents = parents;
        this.initMethod = initMethod;
        this.destroyMethod = destroyMethod;
        this.initOrder = initOrder;
        this.destroyOrder = destroyOrder;
    }

    public void createBean(Object... args) {
        try {
            LOG.debug("Start creating bean {}", () -> name);
            if (isConfigBean) {
                bean = buildMethod.invoke(configComponent, args);
                if (beanType == null && bean != null) {
                    CollectorCommon.InitDestroyConfig initDestroyConfig = CollectorCommon.collectInitDestroyMethods(bean.getClass());
                    initMethod = initDestroyConfig.getInitMethod();
                    destroyMethod = initDestroyConfig.getDestroyMethod();
                    initOrder = initDestroyConfig.getInitOrder();
                    destroyOrder = initDestroyConfig.getDestroyOrder();
                }
            } else {
                bean = constructor.newInstance(args);
            }
            LOG.debug("Bean {} create successful", () -> name);
        } catch (Exception e) {
            throw new InitContextRuntimeException("Can't create bean '" + (beanType == null ? null : beanType.getName()) +
                    "' cause: " + e.getMessage(), e);
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
            throw new InitContextRuntimeException("Can't invoke init int '" + beanType.getName() +
                    "' method '" + initMethod.getName() + "' cause: " + e.getMessage(), e);
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
        return interfaces == null ? Collections.emptySet() : interfaces;
    }

    public Set<Class<?>> getParents() {
        return parents == null ? Collections.emptySet() : parents;
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

package com.roundrobin_assignment.ticketpipeline.config.context;

import com.roundrobin_assignment.ticketpipeline.Application;
import com.roundrobin_assignment.ticketpipeline.config.context.init.ComponentCollector;
import com.roundrobin_assignment.ticketpipeline.config.context.init.ComponentInitializer;
import com.roundrobin_assignment.ticketpipeline.config.context.init.ConfigurationCollector;
import com.roundrobin_assignment.ticketpipeline.config.context.init.ScannedClassCollector;
import com.roundrobin_assignment.ticketpipeline.util.log.Logger;
import com.roundrobin_assignment.ticketpipeline.util.log.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Context {
    private static final Logger LOG = LoggerFactory.getLogger(Application.class.getName());

    private static final Map<String, BeanModel> BEAN_MODEL_MAP = new HashMap<>();

    private Context() {
    }

    public static void init(String packageToScan) {
        LOG.warn("Start scan {}", () -> packageToScan);
        List<Class<?>> scannedClasses = ScannedClassCollector.collectScannedClasses(packageToScan);
        LOG.warn("Scan finished count of elements {}", scannedClasses::size);
        LOG.warn("Putting components", scannedClasses::size);
        BEAN_MODEL_MAP.putAll(ComponentCollector.beanModelMap(scannedClasses));
        LOG.warn("Putting configurations", scannedClasses::size);
        BEAN_MODEL_MAP.putAll(ConfigurationCollector.beanModelMap(scannedClasses));
        LOG.warn("Start init components configurations", scannedClasses::size);
        ComponentInitializer.initializeComponents(BEAN_MODEL_MAP.values());
    }

    public static void stop() {
        BEAN_MODEL_MAP.values().stream()
                .sorted((o1, o2) -> Integer.compare(o2.getDestroyOrder(), o1.getDestroyOrder()))
                .forEach(BeanModel::invokeDestroyMethod);
    }
}

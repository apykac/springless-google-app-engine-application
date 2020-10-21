package com.ticket_pipeline.simple_context.init;

import com.ticket_pipeline.simple_context.exception.InitContextRuntimeException;
import com.ticket_pipeline.simple_utils.ClassPathResourcesUtil;

import java.util.List;
import java.util.stream.Collectors;

public class ScannedClassCollector {
    private ScannedClassCollector() {
    }

    public static List<Class<?>> collectScannedClasses(String packageToScan) {
        if (packageToScan == null) {
            throw new InitContextRuntimeException("Scan package is null");
        }
        String pathToScan = packageToScan.replace(".", "/");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        List<String> files = ClassPathResourcesUtil.getDirectoryFiles(".");
        if (files.isEmpty()) {
            files = ClassPathResourcesUtil.getDirectoryFiles(pathToScan);
        }

        return files.stream()
                .map(path -> path.replace("\\", "/"))
                .filter(path -> path.contains(pathToScan))
                .filter(path -> path.endsWith(".class"))
                .filter(path -> !path.contains("$"))
                .map(path -> path.substring(path.indexOf(pathToScan)))
                .map(path -> path.replace("/", "."))
                .map(className -> className.substring(0, className.lastIndexOf(".class")))
                .map(className -> ScannedClassCollector.loadClass(className, classLoader))
                .collect(Collectors.toList());
    }

    private static Class<?> loadClass(String className, ClassLoader classLoader) {
        try {
            return classLoader.loadClass(className);
        } catch (Exception e) {
            throw new InitContextRuntimeException("Can't load class '" + className + "' for scan cause: " + e.getMessage(), e);
        }
    }
}

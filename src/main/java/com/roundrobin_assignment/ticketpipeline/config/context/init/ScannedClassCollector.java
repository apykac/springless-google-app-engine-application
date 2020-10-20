package com.roundrobin_assignment.ticketpipeline.config.context.init;

import com.roundrobin_assignment.ticketpipeline.exception.InitContextRuntimeException;
import com.roundrobin_assignment.ticketpipeline.util.ClassPathResourcesUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
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

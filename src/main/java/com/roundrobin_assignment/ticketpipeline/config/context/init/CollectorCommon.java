package com.roundrobin_assignment.ticketpipeline.config.context.init;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CollectorCommon {
    private CollectorCommon() {
    }

    public static Relation collectRelations(Class<?> clazz) {
        Relation relation = new Relation();
        collectRelations(clazz, relation.getParents(), relation.getInterfaces());
        return relation;
    }

    private static void collectRelations(Class<?> clazz, Set<Class<?>> parents, Set<Class<?>> interfaces) {
        if (clazz == Object.class) {
            return;
        }
        Class<?> parent = clazz.getSuperclass();
        if (parent != Object.class) {
            parents.add(parent);
        }
        Class<?>[] interfacesArr = clazz.getInterfaces();
        if (interfacesArr.length != 0) {
            interfaces.addAll(Arrays.asList(interfacesArr));
        }

        collectRelations(clazz.getSuperclass(), parents, interfaces);
    }

    public static class Relation {
        private final Set<Class<?>> interfaces = new HashSet<>();
        private final Set<Class<?>> parents = new HashSet<>();

        public Set<Class<?>> getInterfaces() {
            return interfaces;
        }

        public Set<Class<?>> getParents() {
            return parents;
        }
    }
}

/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.extension.e2e.junit.utils

class InstanceList {
    private val byClass: MutableMap<Class<*>, MutableList<Instance<*>>> = HashMap()
    private val all: MutableList<Instance<*>> = ArrayList()

    fun <T> add(
        instance: Instance<T>
    ) {
        all.add(instance)

        fun addInternal(clazz: Class<*>, instance: Instance<*>) {
            byClass.computeIfAbsent(clazz) { ArrayList() }.add(instance)
        }

        // register superclasses
        var cls: Class<in T> = instance.clazz
        while (cls != Any::class.java) {
            addInternal(cls, instance)

            // register interfaces
            cls.interfaces.forEach { interfaceClass ->
                addInternal(interfaceClass, instance)
            }
            cls = cls.superclass!!
        }
    }

    fun remove(clazz: Class<*>) {
        fun Instance<*>.shouldRemove(): Boolean =
            clazz.isAssignableFrom(this.clazz)

        byClass.values.forEach {
            it.removeIf { instance -> instance.shouldRemove() }
        }
        all.removeIf { it.shouldRemove() }
    }

    fun has(clazz: Class<*>, annotations: List<Class<out Annotation>>): Boolean {
        return byClass[clazz]?.any {
            annotations.containsAll(it.annotations)
        } ?: false
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> get(clazz: Class<T>, annotations: List<Class<out Annotation>>): List<T> {
        return byClass[clazz]
            ?.filter { annotations.containsAll(it.annotations) }
            ?.map { it.value.get() as T }
            ?: emptyList()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getAllForCleanup(clazz: Class<T>): List<T> {
        return byClass[clazz]
            ?.filter { it.value.isInitialized }
            ?.map { it.value.get() as T }
            ?: emptyList()
    }
}

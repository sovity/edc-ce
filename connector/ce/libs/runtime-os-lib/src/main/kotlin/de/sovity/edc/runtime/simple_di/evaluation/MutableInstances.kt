/*
 * Copyright 2025 sovity GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 */
package de.sovity.edc.runtime.simple_di.evaluation

import de.sovity.edc.runtime.simple_di.Instances

/**
 * Implementation of [Instances] that also allows intercepting the accessing operations.
 */
class MutableInstances(
    private val instances: MutableMap<Class<out Any>, MutableList<Any>> = mutableMapOf(),
    private val onBeforeAccess: (clazz: Class<out Any>) -> Unit = {}
) : Instances {
    fun add(value: Any) {
        var clazz: Class<out Any> = value::class.java
        while (clazz != Any::class.java) {
            instances.computeIfAbsent(clazz) { mutableListOf() }.add(value)
            clazz.interfaces.forEach { iface ->
                instances.computeIfAbsent(iface) { mutableListOf() }.add(value)
            }

            clazz = clazz.superclass
        }
    }

    fun addAll(values: Collection<Any>) {
        values.forEach { add(it) }
    }

    override fun <T> getSingle(clazz: Class<T>): T {
        val found = getAll(clazz)
        require(found.size == 1) { "Wanted one instance for $clazz, but got ${found.size}: $found" }
        return found.single()
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getAll(clazz: Class<T>): List<T> {
        onBeforeAccess(clazz as Class<out Any>)
        return instances[clazz] as? List<T> ?: emptyList()
    }

    override fun has(clazz: Class<out Any>): Boolean {
        onBeforeAccess(clazz)
        return instances[clazz]?.isNotEmpty() == true
    }

    /**
     * [instances] is passed by reference!
     *
     * The copy still mutates the same [instances], but is using a different interceptor
     */
    fun withAccessInterceptor(onBeforeAccessProperty: (clazz: Class<out Any>) -> Unit) =
        MutableInstances(instances, onBeforeAccessProperty)
}

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

import java.lang.reflect.Modifier.isAbstract

object Instantiator {
    /**
     * Instantiate a class.
     *
     * Expects a single public constructor.
     *
     * Constructor Parameters are resolved with the [parameterResolver].
     */
    @Suppress("UNCHECKED_CAST")
    fun instantiate(clazz: Class<out Any>, parameterResolver: (clazz: Class<out Any>) -> Any): Any {
        require(isInstantiable(clazz)) { "Class $clazz is not instantiable." }

        val constructor = clazz.constructors.singleOrNull { it.canAccess(null) }
            ?: error("Must have exactly one accessible constructor")

        val parameters = constructor.parameters.map { parameter ->
            val parameterType = parameter.type as Class<Any>
            val parameterValue = parameterResolver(parameterType)
            parameterValue
        }

        val instance = constructor.newInstance(*parameters.toTypedArray())
        return instance
    }

    fun isInstantiable(clazz: Class<out Any>): Boolean =
        !(isAbstract(clazz.modifiers) || clazz.isInterface)

}

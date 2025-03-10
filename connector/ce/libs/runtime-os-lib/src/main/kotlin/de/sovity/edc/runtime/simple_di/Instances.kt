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
package de.sovity.edc.runtime.simple_di


interface Instances {
    fun has(clazz: Class<out Any>): Boolean
    fun <T> getSingle(clazz: Class<T>): T
    fun <T> getAll(clazz: Class<T>): List<T>
}

inline fun <reified T> Instances.getSingle(): T = getSingle(T::class.java)
inline fun <reified T> Instances.getAll(): List<T> = getAll(T::class.java)


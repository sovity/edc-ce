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

/**
 * Tracks the evaluation path for better error reporting on circular dependencies.
 */
class EvaluationPath(
    private val path: List<String> = listOf()
) {
    /**
     * Returns a new copy with the path extended by the given property
     */
    fun forChild(property: String) =
        EvaluationPath(path + property)

    override fun toString(): String {
        if (path.isEmpty()) {
            return "[empty]"
        }
        return path.joinToString(" -> ")
    }
}

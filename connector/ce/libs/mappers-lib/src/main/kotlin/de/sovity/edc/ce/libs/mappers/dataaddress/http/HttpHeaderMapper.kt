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
package de.sovity.edc.ce.libs.mappers.dataaddress.http

import de.sovity.edc.runtime.simple_di.Service
import de.sovity.edc.utils.jsonld.vocab.Prop

@Service
class HttpHeaderMapper {
    fun buildHeaderProps(headers: Map<String, String?>?): Map<String, String?> {
        return headers?.mapKeys { (key, _) ->
            if ("content-type".equals(key, ignoreCase = true)) {
                // Content-Type is overridden by a special Data Address property
                // So we should set that instead of attempting to set a header
                Prop.Edc.CONTENT_TYPE
            } else {
                "header:$key"
            }
        } ?: emptyMap()
    }
}

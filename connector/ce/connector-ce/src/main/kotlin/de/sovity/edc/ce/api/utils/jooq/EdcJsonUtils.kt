/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.utils.jooq

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import de.sovity.edc.runtime.simple_di.Service
import org.jooq.JSON

@Service
class EdcJsonUtils(private val objectMapper: ObjectMapper) {
    fun toPostgresqlJsonArray(list: List<String>): JSON = JSON.json(objectMapper.writeValueAsString(list))

    fun toPostgresqlJson(map: Map<String, Any>) = JSON.json(objectMapper.writeValueAsString(map))

    fun parseStringArray(json: JSON): List<String> =
        objectMapper.readValue(json.data(), object : TypeReference<List<String>>() {})

    fun parseMap(json: JSON): Map<String, Any> =
        objectMapper.readValue(json.data(), object : TypeReference<Map<String, Any>>() {})
}

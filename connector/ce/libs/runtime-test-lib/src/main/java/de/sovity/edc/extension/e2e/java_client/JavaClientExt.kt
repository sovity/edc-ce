/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.extension.e2e.java_client

import de.sovity.edc.client.gen.model.ContractDefinitionRequest
import de.sovity.edc.client.gen.model.UiCriterion
import de.sovity.edc.client.gen.model.UiCriterionLiteral
import de.sovity.edc.client.gen.model.UiCriterionLiteralType
import de.sovity.edc.client.gen.model.UiCriterionOperator
import de.sovity.edc.utils.jsonld.vocab.Prop

object JavaClientExt {
    fun ContractDefinitionRequest.ContractDefinitionRequestBuilder.assetSelectorById(assetId: String) =
        this.assetSelector(
            listOf(
                UiCriterion.builder()
                    .operandLeft(Prop.Edc.ID)
                    .operator(UiCriterionOperator.EQ)
                    .operandRight(
                        UiCriterionLiteral.builder()
                            .type(UiCriterionLiteralType.VALUE)
                            .value(assetId)
                            .build()
                    )
                    .build()
            )
        )
}

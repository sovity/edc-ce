/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.modules.messaging.messenger.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.sovity.edc.utils.jsonld.vocab.Prop;

public record SovityMessageResponse(
    @JsonProperty(Prop.SovityMessageExt.HEADER)
    String header,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty(Prop.SovityMessageExt.BODY)
    String body
) {
}

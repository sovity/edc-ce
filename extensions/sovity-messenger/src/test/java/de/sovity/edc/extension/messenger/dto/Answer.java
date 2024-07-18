/*
 *  Copyright (c) 2024 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 *
 */

package de.sovity.edc.extension.messenger.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.sovity.edc.extension.messenger.SovityMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Answer implements SovityMessage {
    @Override
    public String getType() {
        return getClass().getCanonicalName();
    }

    @JsonProperty
    private int answer;
}

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

package de.sovity.edc.extension.messenger.demo.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.sovity.edc.extension.messenger.SovityMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Addition implements SovityMessage {

    public static final String TYPE = "demo-add";

    @Override
    public String getType() {
        return TYPE;
    }

    @JsonProperty
    public int op1;

    @JsonProperty
    public int op2;

}

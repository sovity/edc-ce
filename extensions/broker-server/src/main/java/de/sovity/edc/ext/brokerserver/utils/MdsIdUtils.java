/*
 *  Copyright (c) 2023 sovity GmbH
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

package de.sovity.edc.ext.brokerserver.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MdsIdUtils {
    public static String getMdsIdFromParticipantId(String participantId) {
        if (participantId == null || !participantId.matches("^MDSL[A-Za-z0-9]+\\.C[A-Za-z0-9]+")) {
            return null;
        }

        return participantId.split("\\.")[0];
    }

    public static Boolean isValidMdsId(String mdsId) {
        return mdsId != null && mdsId.matches("^MDSL[A-Za-z0-9]+");
    }
}

/*
 * Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *      sovity GmbH - init
 */

package de.sovity.edc.ext.wrapper.api.ee;

import de.sovity.edc.ext.wrapper.api.ee.model.ConnectorLimits;
import de.sovity.edc.ext.wrapper.api.ee.model.StoredFile;

import java.util.List;
import java.util.Map;

public class EnterpriseEditionResourceImpl implements EnterpriseEditionResource {
    @Override
    public ConnectorLimits connectorLimits() {
        return new ConnectorLimits(-1, -1);
    }

    @Override
    public List<StoredFile> listStoredFiles() {
        return null;
    }

    @Override
    public StoredFile createStoredFileAsset(String storedFileId,
                                            Map<String, String> assetProperties) {
        return null;
    }
}

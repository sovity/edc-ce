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

package de.sovity.edc.ext.wrapper.api.ui.pages.asset.services.utils;

import de.sovity.edc.ext.wrapper.api.ui.model.DataAddressDto;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.types.domain.DataAddress;

@RequiredArgsConstructor
public class AssetUtils {

    public DataAddressDto mapToDataAddressDto(DataAddress dataAddress) {
        if (dataAddress == null) {
            return null;
        }

        DataAddressDto dto = new DataAddressDto();
        dto.setProperties(dataAddress.getProperties());
        return dto;
    }


    public DataAddress mapToDataAddress(DataAddressDto dataAddressDto) {
        if (dataAddressDto == null) {
            return null;
        }

        DataAddress dataAddress = DataAddress.Builder.newInstance().properties(dataAddressDto.getProperties()).build();
        return dataAddress;
    }


}

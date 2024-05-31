/*
 *  Copyright (c) 2022 sovity GmbH
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

package de.sovity.edc.ext.wrapper.api.ui.pages.asset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AssetIdValidatorTest {
    AssetIdValidator assetIdValidator;

    @BeforeEach
    void setup() {
        assetIdValidator = new AssetIdValidator();
    }

    @Test
    void testOk() {
        var assetId = "test-1.0";
        assertThat(assetIdValidator.isValid(assetId)).isTrue();
        assetIdValidator.assertValid(assetId);
    }

    @Test
    void testColon() {
        var assetId = "test:1.0";
        assertThat(assetIdValidator.isValid(assetId)).isFalse();
        assertThatThrownBy(() -> assetIdValidator.assertValid(assetId))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testWhitespace() {
        var assetId = "test asset";
        assertThat(assetIdValidator.isValid(assetId)).isFalse();
        assertThatThrownBy(() -> assetIdValidator.assertValid(assetId))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

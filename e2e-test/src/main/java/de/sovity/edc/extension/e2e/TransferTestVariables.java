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

package de.sovity.edc.extension.e2e;

public class TransferTestVariables {

    public static final String EDC_MANAGEMENT_URL = "%s_EDC_MANAGEMENT_URL";
    public static final String EDC_PROTOCOL_URL = "%s_EDC_PROTOCOL_URL";
    public static final String EDC_MANAGEMENT_AUTH_HEADER = "%s_EDC_MANAGEMENT_AUTH_HEADER";
    public static final String EDC_MANAGEMENT_AUTH_VALUE = "%s_EDC_MANAGEMENT_AUTH_VALUE";
    public static final String PROVIDER_TARGET_URL = "PROVIDER_TARGET_URL";
    public static final String CONSUMER_TARGET_URL = "CONSUMER_TARGET_URL";
    public static final String TEST_BACKEND_CHECK_URL = "TEST_BACKEND_CHECK_URL";
    public static final String TEST_BACKEND_TEST_DATA = "TEST_BACKEND_TEST_DATA";

    private TransferTestVariables() {
    }
}

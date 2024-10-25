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

package de.sovity.edc.ext.wrapper.api.ui.pages.dashboard.services;

import de.sovity.edc.ext.wrapper.api.ui.model.DashboardCxDidConfig;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.system.configuration.Config;

import static com.apicatalog.jsonld.StringUtils.isBlank;

@RequiredArgsConstructor
public class CxDidConfigService {
    private final Config config;

    /**
     * Wallet Token Url
     */
    private static final String EDC_IAM_STS_OAUTH_TOKEN_URL = "edc.iam.sts.oauth.token.url";


    /**
     * Trusted VC Issuer
     */
    private static final String EDC_IAM_TRUSTED_ISSUER_COFINITY_ID = "edc.iam.trusted-issuer.cofinity.id";


    /**
     * BDRS Url
     */

    private static final String TX_IAM_IATP_BDRS_SERVER_URL = "tx.iam.iatp.bdrs.server.url";


    /**
     * My DID
     */
    private static final String EDC_IAM_ISSUER_ID = "edc.iam.issuer.id";


    /**
     * DIM Url
     */
    private static final String EDC_IAM_STS_DIM_URL = "edc.iam.sts.dim.url";

    public DashboardCxDidConfig buildCxDidConfigOrNull() {
        var cxDidConfig = new DashboardCxDidConfig();
        cxDidConfig.setMyDid(configValue(EDC_IAM_ISSUER_ID));
        cxDidConfig.setWalletTokenUrl(configValue(EDC_IAM_STS_OAUTH_TOKEN_URL));
        cxDidConfig.setTrustedVcIssuer(configValue(EDC_IAM_TRUSTED_ISSUER_COFINITY_ID));
        cxDidConfig.setDimUrl(configValue(EDC_IAM_STS_DIM_URL));
        cxDidConfig.setBdrsUrl(configValue(TX_IAM_IATP_BDRS_SERVER_URL));
        return isBlank(cxDidConfig.getMyDid()) ? null : cxDidConfig;
    }

    private String configValue(String configKey) {
        return config.getString(configKey, "");
    }
}

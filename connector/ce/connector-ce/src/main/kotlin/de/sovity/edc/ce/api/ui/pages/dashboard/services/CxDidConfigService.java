/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
package de.sovity.edc.ce.api.ui.pages.dashboard.services;

import de.sovity.edc.ce.api.ui.model.DashboardCxDidConfig;
import de.sovity.edc.runtime.simple_di.Service;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.system.configuration.Config;

import static com.apicatalog.jsonld.StringUtils.isBlank;

@RequiredArgsConstructor
@Service
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

plugins {
    `java-library`
}

var edcGroup = libs.versions.edcGroup.get()
var edcVersion = libs.versions.edc.get()
var edcVersionForOtherRepos = libs.versions.edcForOtherRepos.get()

val catenaVersion = "0.7.6"
val catenaGroup = "org.eclipse.tractusx.edc"

dependencies {
    // For Custom Launcher Code
    implementation(project(":config"))

    // // Control-Plane
    // api(libs.edc.controlPlaneCore)
    // api(libs.edc.managementApi)
    // api(libs.edc.apiObservability)
    // api(libs.edc.configurationFilesystem)
    // api(libs.edc.controlPlaneAggregateServices)
    // api(libs.edc.http)
    // api(libs.edc.dsp)
    // api(libs.edc.jsonLd)

    // // Management API Key
    api(libs.edc.authTokenbased)

    // // sovity Extensions Package
    api(project(":extensions:sovity-edc-extensions-package"))
    api(project(":extensions:postgres-flyway"))

    // // Control-plane to Data-plane
    // api(libs.edc.transferDataPlane)
    // api(libs.edc.dataPlaneSelectorCore)
    // api(libs.edc.dataPlaneSelectorClient)

    // // Data-plane
    // api(libs.edc.dataPlaneHttp)
    // api(libs.edc.dataPlaneCore)
    // api(libs.edc.dataPlaneUtil)


    // sovity ee extensions
    implementation("${edcGroup}:json-ld:${edcVersion}")
    implementation("${edcGroup}:connector-core:${edcVersion}")
    implementation("${edcGroup}:policy-monitor-core:${edcVersion}")
    implementation("${edcGroup}:validator-data-address-http-data:${edcVersion}")
    implementation("${edcGroup}:callback-event-dispatcher:${edcVersion}")
    implementation("${edcGroup}:callback-http-dispatcher:${edcVersion}")
    implementation("${edcGroup}:callback-static-endpoint:${edcVersion}")

    // control-plane
    implementation("${edcGroup}:control-plane-core:${edcVersion}")
    implementation("${edcGroup}:management-api:${edcVersion}")
    implementation("${edcGroup}:secrets-api:${edcVersion}")
    implementation("${edcGroup}:control-api-configuration:${edcVersion}")
    implementation("${edcGroup}:control-plane-api:${edcVersion}")
    implementation("${edcGroup}:http:${edcVersion}")
    implementation("${edcGroup}:dsp:${edcVersion}")
    implementation("${edcGroup}:configuration-filesystem:${edcVersion}")

    // data-plane-selector
    implementation("${edcGroup}:data-plane-selector-core:${edcVersion}")
    implementation("${edcGroup}:data-plane-selector-api:${edcVersion}")

    // Data Transfer
    implementation("${edcGroup}:transfer-pull-http-dynamic-receiver:${edcVersion}")
    implementation("${edcGroup}:edr-store-core:${edcVersion}")
    implementation("${edcGroup}:edr-store-receiver:${edcVersion}")
    implementation("${catenaGroup}:transfer-dataplane-signaling:${catenaVersion}")
    implementation("${catenaGroup}:azblob-provisioner:${catenaVersion}")

    // fix CVE in Catalog, see: https://github.com/eclipse-tractusx/tractusx-edc/pull/1584
    implementation("${catenaGroup}:dataset-bugfix:${catenaVersion}")

    //
    // DP Dependencies
    //
    // data-plane
    // implementation("${edcGroup}:json-ld:${edcVersion}")
    // implementation("${edcGroup}:connector-core:${edcVersion}")
    implementation("${edcGroup}:web-spi:${edcVersion}")
    implementation("${edcGroup}:data-plane-core:${edcVersion}")
    // normally the pipeline service is provided by the data-plane-core dependency
    // however there is a TX version, which contains some fixes
    // see: https://github.com/eclipse-tractusx/tractusx-edc/pull/1520
    implementation("${catenaGroup}:pipeline-service:${catenaVersion}")
    // implementation("${edcGroup}:http:${edcVersion}")
    implementation("${edcGroup}:data-plane-control-api:${edcVersion}")
    // implementation("${edcGroup}:control-api-configuration:${edcVersion}")
    implementation("${edcGroup}:data-plane-public-api-v2:${edcVersion}")
    implementation("${edcGroup}:control-plane-api-client:${edcVersion}")

    implementation(project(":extensions:integrated-data-plane-initializer"))

    // transfer-types
    implementation("${edcGroup}:data-plane-http:${edcVersion}")
    implementation("${edcGroup}:data-plane-http-oauth2:${edcVersion}")
    implementation("${edcGroup}.azure:data-plane-azure-storage:${edcVersionForOtherRepos}")
    implementation("${edcGroup}.aws:data-plane-aws-s3:${edcVersionForOtherRepos}")

    // persistence-db
    implementation("${edcGroup}:data-plane-store-sql:${edcVersion}")
    implementation("${edcGroup}:accesstokendata-store-sql:${edcVersion}")

    // signalling
    implementation("${edcGroup}:data-plane-signaling-api:${edcVersion}")

    // signalling-refresh
    // TODO: unsure if required
    // implementation("${catenaGroup}:edr-core:${catenaVersion}")
    // implementation("${catenaGroup}:token-refresh-api:${catenaVersion}")

    // implementation("${catenaGroup}:token-refresh-core:${catenaVersion}")
    // implementation("${edcGroup}:identity-did-core:${edcVersion}")
    // implementation("${edcGroup}:identity-did-web:${edcVersion}")

    // implementation("${catenaGroup}:tokenrefresh-handler:${catenaVersion}")
    // implementation("${catenaGroup}:tx-iatp-sts-dim:${catenaVersion}")
    // implementation("${edcGroup}:edr-store-core:${edcVersion}")
    // implementation("${edcGroup}:edr-index-sql:${edcVersion}")

    // implementation("${catenaGroup}:edc-dataplane-proxy-consumer-api:${catenaVersion}")
}

group = libs.versions.sovityEdcGroup.get()

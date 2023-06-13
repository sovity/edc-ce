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

package de.sovity.edc.ext.brokerserver.dao;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssetProperty {
    public static final String ASSET_ID = "asset:prop:id";
    public static final String ASSET_NAME = "asset:prop:name";
    public static final String DESCRIPTION = "asset:prop:description";
    public static final String KEYWORDS = "asset:prop:keywords";


    public static final String CONTENT_TYPE = "asset:prop:contenttype";
    public static final String ORIGINATOR = "asset:prop:originator";
    public static final String ORIGINATOR_ORGANIZATION = "asset:prop:originatorOrganization";
    public static final String VERSION = "asset:prop:version";
    public static final String CURATOR_ORGANIZATION_NAME = "asset:prop:curatorOrganizationName";
    public static final String LANGUAGE = "asset:prop:language";
    public static final String PUBLISHER = "asset:prop:publisher";
    public static final String STANDARD_LICENSE = "asset:prop:standardLicense";
    public static final String ENDPOINT_DOCUMENTATION = "asset:prop:endpointDocumentation";

    public static final String DATA_CATEGORY = "http://w3id.org/mds#dataCategory";
    public static final String DATA_SUBCATEGORY = "http://w3id.org/mds#dataSubcategory";
    public static final String DATA_MODEL = "http://w3id.org/mds#dataModel";
    public static final String GEO_REFERENCE_METHOD = "http://w3id.org/mds#geoReferenceMethod";
    public static final String TRANSPORT_MODE = "http://w3id.org/mds#transportMode";
}

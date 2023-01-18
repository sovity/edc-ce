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
package sender;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.fraunhofer.iais.eis.ConnectorEndpointBuilder;
import de.fraunhofer.iais.eis.DynamicAttributeToken;
import de.fraunhofer.iais.eis.IANAMediaTypeBuilder;
import de.fraunhofer.iais.eis.Language;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.MessageProcessedNotificationMessageImpl;
import de.fraunhofer.iais.eis.RepresentationBuilder;
import de.fraunhofer.iais.eis.ResourceBuilder;
import de.fraunhofer.iais.eis.ResourceUpdateMessageBuilder;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import org.eclipse.edc.protocol.ids.api.multipart.dispatcher.sender.MultipartSenderDelegate;
import org.eclipse.edc.protocol.ids.api.multipart.dispatcher.sender.response.IdsMultipartParts;
import org.eclipse.edc.protocol.ids.api.multipart.dispatcher.sender.response.MultipartResponse;
import org.eclipse.edc.protocol.ids.spi.domain.IdsConstants;
import org.eclipse.edc.protocol.ids.util.CalendarUtil;
import sender.message.RegisterResourceMessage;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.eclipse.edc.protocol.ids.api.multipart.dispatcher.sender.util.ResponseUtil.parseMultipartStringResponse;
import static org.eclipse.edc.protocol.ids.jsonld.JsonLd.getObjectMapper;

public class RegisterResourceRequestSender implements MultipartSenderDelegate<RegisterResourceMessage,
        String> {

    private ObjectMapper objectMapper;

    public RegisterResourceRequestSender(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Message buildMessageHeader(RegisterResourceMessage registerResourceMessage,
                                      DynamicAttributeToken token) throws Exception {
        return new ResourceUpdateMessageBuilder(registerResourceMessage.affectedResourceUri())
                ._modelVersion_(IdsConstants.INFORMATION_MODEL_VERSION)
                ._issued_(CalendarUtil.gregorianNow())
                ._securityToken_(token)
                ._issuerConnector_(registerResourceMessage.connectorBaseUrl())
                ._senderAgent_(registerResourceMessage.connectorBaseUrl())
                ._affectedResource_(registerResourceMessage.affectedResourceUri())
                .build();
    }

    @Override
    public String buildMessagePayload(RegisterResourceMessage registerResourceMessage) throws Exception {
        var assetTitle = getAssetTitle(registerResourceMessage);
        var assetDescription = getAssetDescription(registerResourceMessage);
        var language = Arrays.stream(Language.values())
                .filter(l -> l.getId().toString().equals(getAssetLanguage(registerResourceMessage)))
                .findFirst()
                .orElse(Language.EN);
        var version = getAssetVersion(registerResourceMessage);
        var keywords = getAssetKeywords(registerResourceMessage)
                .stream()
                .map(k -> new TypedLiteral(k, language.name()))
                .toList();
        var mediaType = getAssetMediaType(registerResourceMessage);
        var publisher = getAssetPublisher(registerResourceMessage);
        var standardLicense = getAssetStandardLicense(registerResourceMessage);
        var endpointDocumentation = getAssetEndpointDocumentation(registerResourceMessage);
        var transportMode = getAssetTransportMode(registerResourceMessage);
        var dataCategory = getAssetDataCategory(registerResourceMessage);
        var dataSubcategory = getAssetDataSubcategory(registerResourceMessage);
        var dataModel = getAssetDataModel(registerResourceMessage);
        var geoReferenceMethod = getAssetGeoReferenceMethod(registerResourceMessage);

        var resource = new ResourceBuilder(registerResourceMessage.affectedResourceUri())
                ._title_(new TypedLiteral(assetTitle, language.name()))
                ._description_(new TypedLiteral(assetDescription, language.name()))
                ._language_(language)
                ._version_(version)
                ._keyword_(keywords)
                ._publisher_(URI.create(publisher))
                ._standardLicense_(URI.create(standardLicense))
                ._representation_(new RepresentationBuilder()
                        ._language_(language)
                        ._mediaType_(new IANAMediaTypeBuilder()._filenameExtension_(mediaType).build())
                        .build())
                ._resourceEndpoint_(List.of(new ConnectorEndpointBuilder()
                        ._endpointDocumentation_(URI.create(endpointDocumentation))
                        ._accessURL_(registerResourceMessage.affectedResourceUri())
                        .build()))
                .build();

        ObjectNode json = (ObjectNode) objectMapper.readTree(objectMapper.writeValueAsString(resource));
        json.set("http://w3id.org/mds#transportMode", getTransportModeJson(transportMode));
        json.set("http://w3id.org/mds#dataCategory", getDataCategoryJson(dataCategory));
        json.set("http://w3id.org/mds#dataSubcategory", getDataSubcategoryJson(dataSubcategory));
        json.set("http://w3id.org/mds#dataModel", getDataModelJson(dataModel));
        json.set("http://w3id.org/mds#geoReferenceMethod", getGeoReferenceMethodJson(geoReferenceMethod));
        return objectMapper.writeValueAsString(json);
    }

    private ObjectNode getGeoReferenceMethodJson(String geoReferenceMethod) {
        return buildStringProperty(geoReferenceMethod);
    }

    private ObjectNode getDataModelJson(String dataModel) {
        return buildStringProperty(dataModel);
    }

    private ObjectNode getDataSubcategoryJson(String dataSubcategory) {
        return buildStringProperty(dataSubcategory);
    }

    private ObjectNode getTransportModeJson(String transportMode) {
        return buildStringProperty(transportMode);
    }

    private ObjectNode getDataCategoryJson(String dataCategory) {
        return buildStringProperty(dataCategory);
    }

    private ObjectNode buildStringProperty(String property) {
        ObjectNode transportModeJson = objectMapper.createObjectNode();
        transportModeJson.put("@value", property);
        transportModeJson.put("@type", "http://www.w3.org/2001/XMLSchema#string");
        return transportModeJson;
    }

    private String getAssetGeoReferenceMethod(RegisterResourceMessage registerResourceMessage) {
        return getAssetProperty(registerResourceMessage, "http://w3id.org/mds#geoReferenceMethod");
    }

    private String getAssetDataModel(RegisterResourceMessage registerResourceMessage) {
        return getAssetProperty(registerResourceMessage, "http://w3id.org/mds#dataModel");
    }

    private String getAssetDataSubcategory(RegisterResourceMessage registerResourceMessage) {
        return getAssetProperty(registerResourceMessage, "http://w3id.org/mds#dataSubcategory");
    }

    private String getAssetDataCategory(RegisterResourceMessage registerResourceMessage) {
        return getAssetProperty(registerResourceMessage, "http://w3id.org/mds#dataCategory");
    }

    private String getAssetTransportMode(RegisterResourceMessage registerResourceMessage) {
        return getAssetProperty(registerResourceMessage, "http://w3id.org/mds#transportMode");
    }

    private String getAssetEndpointDocumentation(RegisterResourceMessage registerResourceMessage) {
        return getAssetProperty(registerResourceMessage, "asset:prop:endpointDocumentation");
    }

    private String getAssetStandardLicense(RegisterResourceMessage registerResourceMessage) {
        return getAssetProperty(registerResourceMessage, "asset:prop:standardLicense");
    }

    private String getAssetPublisher(RegisterResourceMessage registerResourceMessage) {
        return getAssetProperty(registerResourceMessage, "asset:prop:publisher");
    }

    private String getAssetLanguage(RegisterResourceMessage registerResourceMessage) {
        return getAssetProperty(registerResourceMessage, "asset:prop:language");
    }

    private static List<String> getAssetKeywords(RegisterResourceMessage registerResourceMessage) {
        var keywords = getAssetProperty(registerResourceMessage, "asset:prop:keywords");
        return new ArrayList<>(Arrays.asList(keywords.split(",")));
    }

    private static String getAssetMediaType(RegisterResourceMessage registerResourceMessage) {
        return getAssetProperty(registerResourceMessage, "asset:prop:contenttype");
    }

    private static String getAssetDescription(RegisterResourceMessage registerResourceMessage) {
        return getAssetProperty(registerResourceMessage, "asset:prop:description");
    }

    private static String getAssetTitle(RegisterResourceMessage registerResourceMessage) {
        return getAssetProperty(registerResourceMessage, "asset:prop:name");
    }

    private static String getAssetVersion(RegisterResourceMessage registerResourceMessage) {
        return getAssetProperty(registerResourceMessage, "asset:prop:version");
    }

    private static String getAssetProperty(RegisterResourceMessage registerResourceMessage, String property) {
        var result = "";
        if (checkPropertyExists(registerResourceMessage, property)) {
            result = registerResourceMessage.asset().getProperties().get(property).toString();
        }
        return result;
    }

    private static boolean checkPropertyExists(RegisterResourceMessage registerResourceMessage, String property) {
        return registerResourceMessage.asset().getProperties().get(property) != null;
    }

    @Override
    public MultipartResponse<String> getResponseContent(IdsMultipartParts parts) throws Exception {
        return parseMultipartStringResponse(parts, getObjectMapper());
    }

    @Override
    public List<Class<? extends Message>> getAllowedResponseTypes() {
        return List.of(MessageProcessedNotificationMessageImpl.class);
    }

    @Override
    public Class<RegisterResourceMessage> getMessageType() {
        return RegisterResourceMessage.class;
    }
}

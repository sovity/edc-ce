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
import de.fraunhofer.iais.eis.*;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import org.eclipse.edc.protocol.ids.api.multipart.dispatcher.sender.MultipartSenderDelegate;
import org.eclipse.edc.protocol.ids.api.multipart.dispatcher.sender.response.IdsMultipartParts;
import org.eclipse.edc.protocol.ids.api.multipart.dispatcher.sender.response.MultipartResponse;
import org.eclipse.edc.protocol.ids.spi.domain.IdsConstants;
import org.eclipse.edc.protocol.ids.util.CalendarUtil;
import sender.message.RegisterResourceMessage;

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
        var language = Language.EN;
        var version = getVersion(registerResourceMessage);
        var keywords = getKeywords(registerResourceMessage)
                        .stream()
                        .map(k -> new TypedLiteral(k, "en"))
                        .toList();
        var mediaType = getMediaType(registerResourceMessage);

        var resource = new ResourceBuilder(registerResourceMessage.affectedResourceUri())
                ._title_(new TypedLiteral(assetTitle, "en"))
                ._description_(new TypedLiteral(assetDescription, "en"))
                ._language_(language)
                ._version_(version)
                ._keyword_(keywords)
                ._representation_(new RepresentationBuilder()
                        ._language_(language)
                        ._mediaType_(new IANAMediaTypeBuilder()._filenameExtension_(mediaType).build())
                        .build())
                .build();
        System.out.println(objectMapper.writeValueAsString(resource));
        return objectMapper.writeValueAsString(resource);
    }

    private static List<String> getKeywords(RegisterResourceMessage registerResourceMessage){
        var keywords = "";
        if(checkPropertyExists(registerResourceMessage, "asset:prop:keywords")) {
            keywords = registerResourceMessage.asset().getProperties().get("asset:prop:keywords").toString();
        }
        return new ArrayList<>(Arrays.asList(keywords.split(",")));
    }

    private static String getMediaType(RegisterResourceMessage registerResourceMessage) {
        return getAssetProperty(registerResourceMessage, "asset:prop:contenttype");
    }

    private static String getAssetDescription(RegisterResourceMessage registerResourceMessage) {
        return getAssetProperty(registerResourceMessage, "asset:prop:description");
    }

    private static String getAssetTitle(RegisterResourceMessage registerResourceMessage) {
        return getAssetProperty(registerResourceMessage, "asset:prop:name");
    }

    private static String getVersion(RegisterResourceMessage registerResourceMessage){
        return getAssetProperty(registerResourceMessage, "asset:prop:version");
    }

    private static String getAssetProperty(RegisterResourceMessage registerResourceMessage, String property){
        var result = "";
        if(checkPropertyExists(registerResourceMessage, property)) {
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

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

package de.sovity.edc.ext.brokerserver.services.refreshing.sender;

import de.fraunhofer.iais.eis.DescriptionRequestMessageBuilder;
import de.fraunhofer.iais.eis.DescriptionResponseMessage;
import de.fraunhofer.iais.eis.DynamicAttributeToken;
import de.fraunhofer.iais.eis.Message;
import de.sovity.edc.ext.brokerserver.services.refreshing.sender.message.DescriptionRequestMessage;
import org.eclipse.edc.protocol.ids.api.multipart.dispatcher.sender.MultipartSenderDelegate;
import org.eclipse.edc.protocol.ids.api.multipart.dispatcher.sender.response.IdsMultipartParts;
import org.eclipse.edc.protocol.ids.api.multipart.dispatcher.sender.response.MultipartResponse;
import org.eclipse.edc.protocol.ids.spi.domain.IdsConstants;
import org.eclipse.edc.protocol.ids.util.CalendarUtil;

import java.net.URI;
import java.util.List;

import static org.eclipse.edc.protocol.ids.api.multipart.dispatcher.sender.util.ResponseUtil.parseMultipartStringResponse;
import static org.eclipse.edc.protocol.ids.jsonld.JsonLd.getObjectMapper;

public class DescriptionRequestSender implements MultipartSenderDelegate<DescriptionRequestMessage, String> {
    @Override
    public Message buildMessageHeader(DescriptionRequestMessage request, DynamicAttributeToken token) throws Exception {
        return new DescriptionRequestMessageBuilder()
                ._modelVersion_(IdsConstants.INFORMATION_MODEL_VERSION)
                ._issued_(CalendarUtil.gregorianNow())
                ._securityToken_(token)
                ._issuerConnector_(new URI(request.getConnectorAddress()))
                ._senderAgent_(new URI(request.getConnectorAddress()))
                .build();
    }

    @Override
    public String buildMessagePayload(DescriptionRequestMessage request) throws Exception {
        return null;
    }

    @Override
    public MultipartResponse<String> getResponseContent(IdsMultipartParts parts) throws Exception {
        return parseMultipartStringResponse(parts, getObjectMapper());
    }

    @Override
    public List<Class<? extends Message>> getAllowedResponseTypes() {
        return List.of(DescriptionResponseMessage.class);
    }

    @Override
    public Class<DescriptionRequestMessage> getMessageType() {
        return DescriptionRequestMessage.class;
    }
}

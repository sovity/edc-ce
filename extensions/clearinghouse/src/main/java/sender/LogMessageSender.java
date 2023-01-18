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

import de.fraunhofer.iais.eis.ConnectorUpdateMessageBuilder;
import de.fraunhofer.iais.eis.DynamicAttributeToken;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.MessageProcessedNotificationMessageImpl;
import org.eclipse.edc.protocol.ids.api.multipart.dispatcher.sender.MultipartSenderDelegate;
import org.eclipse.edc.protocol.ids.api.multipart.dispatcher.sender.response.IdsMultipartParts;
import org.eclipse.edc.protocol.ids.api.multipart.dispatcher.sender.response.MultipartResponse;
import org.eclipse.edc.protocol.ids.spi.domain.IdsConstants;
import org.eclipse.edc.protocol.ids.util.CalendarUtil;
import org.json.JSONObject;
import sender.message.LogMessage;

import java.util.List;

import static org.eclipse.edc.protocol.ids.api.multipart.dispatcher.sender.util.ResponseUtil.parseMultipartStringResponse;
import static org.eclipse.edc.protocol.ids.jsonld.JsonLd.getObjectMapper;

public class LogMessageSender implements MultipartSenderDelegate<LogMessage, String> {

    public LogMessageSender() {
    }

    @Override
    public Message buildMessageHeader(LogMessage logMessage,
                                      DynamicAttributeToken token) throws Exception {
        return new ConnectorUpdateMessageBuilder()
                ._modelVersion_(IdsConstants.INFORMATION_MODEL_VERSION)
                ._issued_(CalendarUtil.gregorianNow())
                ._securityToken_(token)
                ._issuerConnector_(logMessage.connectorBaseUrl())
                ._senderAgent_(logMessage.connectorBaseUrl())
                ._affectedConnector_(logMessage.connectorBaseUrl())
                .build();
    }

    @Override
    public String buildMessagePayload(LogMessage logMessage) throws Exception {
        JSONObject jo = new JSONObject();
        jo.put("AgreementId", logMessage.contractAgreement().getId());
        jo.put("AssetId", logMessage.contractAgreement().getAssetId());
        jo.put("ContractStartDate", logMessage.contractAgreement().getContractStartDate());
        jo.put("ContractEndDate", logMessage.contractAgreement().getContractEndDate());
        jo.put("ContractSigningDate", logMessage.contractAgreement().getContractSigningDate());
        jo.put("ConsumerAgentId", logMessage.contractAgreement().getConsumerAgentId());
        jo.put("ProviderAgentId", logMessage.contractAgreement().getProviderAgentId());
        jo.put("Policy", logMessage.contractAgreement().getPolicy());
        return jo.toString();
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
    public Class<LogMessage> getMessageType() {
        return LogMessage.class;
    }
}

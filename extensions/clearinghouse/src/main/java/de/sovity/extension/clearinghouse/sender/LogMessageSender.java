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
package de.sovity.extension.clearinghouse.sender;

import de.fraunhofer.iais.eis.DynamicAttributeToken;
import de.fraunhofer.iais.eis.LogMessageBuilder;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.MessageProcessedNotificationMessageImpl;
import de.sovity.extension.clearinghouse.sender.message.LogMessage;
import org.eclipse.edc.connector.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.connector.transfer.spi.types.TransferProcess;
import org.eclipse.edc.protocol.ids.api.multipart.dispatcher.sender.MultipartSenderDelegate;
import org.eclipse.edc.protocol.ids.api.multipart.dispatcher.sender.response.IdsMultipartParts;
import org.eclipse.edc.protocol.ids.api.multipart.dispatcher.sender.response.MultipartResponse;
import org.eclipse.edc.protocol.ids.spi.domain.IdsConstants;
import org.eclipse.edc.protocol.ids.util.CalendarUtil;
import org.eclipse.edc.spi.EdcException;
import org.json.JSONObject;

import java.util.List;

import static org.eclipse.edc.protocol.ids.api.multipart.dispatcher.sender.util.ResponseUtil.parseMultipartStringResponse;
import static org.eclipse.edc.protocol.ids.jsonld.JsonLd.getObjectMapper;

public class LogMessageSender implements MultipartSenderDelegate<LogMessage, String> {

    public LogMessageSender() {
    }

    @Override
    public Message buildMessageHeader(LogMessage logMessage,
                                      DynamicAttributeToken token) throws Exception {
        return new LogMessageBuilder()
                ._modelVersion_(IdsConstants.INFORMATION_MODEL_VERSION)
                ._issued_(CalendarUtil.gregorianNow())
                ._securityToken_(token)
                ._issuerConnector_(logMessage.connectorBaseUrl())
                ._senderAgent_(logMessage.connectorBaseUrl())
                .build();
    }

    @Override
    public String buildMessagePayload(LogMessage logMessage) throws Exception {
        if (logMessage.eventToLog() instanceof ContractAgreement contractAgreement) {
            return buildContractAgreementPayload(contractAgreement);
        } else if (logMessage.eventToLog() instanceof TransferProcess transferProcess) {
            return buildTransferProcessPayload(transferProcess);
        } else {
            throw new EdcException(String.format("ObjectType %s not supported in LogMessageSender",
                    logMessage.eventToLog().getClass()));
        }
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

    private String buildContractAgreementPayload(ContractAgreement contractAgreement) {
        var jo = new JSONObject();
        jo.put("AgreementId", contractAgreement.getId());
        jo.put("AssetId", contractAgreement.getAssetId());
        jo.put("ContractStartDate", contractAgreement.getContractStartDate());
        jo.put("ContractEndDate", contractAgreement.getContractEndDate());
        jo.put("ContractSigningDate", contractAgreement.getContractSigningDate());
        jo.put("ConsumerAgentId", contractAgreement.getConsumerAgentId());
        jo.put("ProviderAgentId", contractAgreement.getProviderAgentId());
        return jo.toString();
    }

    private String buildTransferProcessPayload(TransferProcess transferProcess) {
        var jo = new JSONObject();
        jo.put("transferProcessId", transferProcess.getId());
        var dataRequest = transferProcess.getDataRequest();
        jo.put("contractId", dataRequest.getContractId());
        jo.put("connectorId", dataRequest.getConnectorId());
        return jo.toString();
    }
}

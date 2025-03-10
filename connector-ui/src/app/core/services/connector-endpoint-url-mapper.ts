/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {Injectable} from '@angular/core';

@Injectable({providedIn: 'root'})
export class ConnectorEndpointUrlMapper {
  /**
   * Builds strings like "https://my-connector.com/api/dsp, participantId=CXL-1234567890".
   *
   * Connectors now require both the connector endpoint and the participant ID for catalog requests, but we
   * still want to be able to offer the "copy + paste" User Experience
   */
  separator = '?participantId=';

  mergeConnectorEndpointAndParticipantId(
    connectorEndpoint: string,
    participantId: string,
  ): string {
    return `${connectorEndpoint}${this.separator}${participantId}`;
  }

  extractConnectorEndpointAndParticipantId(combined: string): {
    connectorEndpoint: string;
    participantId: string;
  } {
    const parts = combined.split(this.separator);
    if (parts.length !== 2) {
      return {connectorEndpoint: combined, participantId: ''};
    }

    return {connectorEndpoint: parts[0], participantId: parts[1]};
  }
}

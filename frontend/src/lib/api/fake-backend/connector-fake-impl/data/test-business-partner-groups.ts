/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */

import type {BusinessPartnerGroupListPageEntry} from '@sovity.de/edc-client';

export namespace TestBusinessPartnerGroups {
  export const firstGroup: BusinessPartnerGroupListPageEntry = {
    groupId: 'first-group',
    businessPartnerNumbers: [...new Array(40).keys()].map(
      (idx) => `BPNL0000000PO${idx + 10}L`,
    ),
  };
  export const secondGroup: BusinessPartnerGroupListPageEntry = {
    groupId: 'second-group',
    businessPartnerNumbers: ['BPNL0000000AB1CD', 'BPNL0000000ZTE1L'],
  };
}

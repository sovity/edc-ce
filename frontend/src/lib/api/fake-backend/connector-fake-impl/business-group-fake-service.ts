/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import type {
  BusinessPartnerGroupCreateSubmit,
  BusinessPartnerGroupEditPage,
  BusinessPartnerGroupEditSubmit,
  BusinessPartnerGroupListPageEntry,
  BusinessPartnerGroupQuery,
  IdResponseDto,
} from '@sovity.de/edc-client';
import {TestBusinessPartnerGroups} from './data/test-business-partner-groups';

let groups = [
  TestBusinessPartnerGroups.firstGroup,
  TestBusinessPartnerGroups.secondGroup,
];

export const listBusinessPartnerGroupsPage = (
  query?: BusinessPartnerGroupQuery,
): BusinessPartnerGroupListPageEntry[] => {
  const filteredGroups = groups.filter(
    (s) => !query?.searchQuery || s.groupId.includes(query.searchQuery),
  );
  if (query?.limit === undefined) {
    return filteredGroups;
  } else {
    return filteredGroups.slice(0, query.limit);
  }
};

export const businessPartnerGroupEditPage = (
  groupId: string,
): BusinessPartnerGroupEditPage => {
  const group = groups.find((g) => g.groupId === groupId);
  if (!group) {
    throw new Error(`Group with id ${groupId} not found`);
  }
  return group;
};

export const businessPartnerGroupEditSubmit = (
  groupId: string,
  request: BusinessPartnerGroupEditSubmit,
): IdResponseDto => {
  groups = groups.map((g) => (g.groupId === groupId ? {...g, ...request} : g));

  return {
    id: groupId,
    lastUpdatedDate: new Date(),
  };
};

export const businessPartnerGroupCreateSubmit = (
  request: BusinessPartnerGroupCreateSubmit,
): IdResponseDto => {
  groups = [...groups, request];

  return {
    id: request.groupId,
    lastUpdatedDate: new Date(),
  };
};

export const deleteBusinessPartnerGroup = (groupId: string): IdResponseDto => {
  groups = groups.filter((g) => g.groupId !== groupId);

  return {
    id: groupId,
    lastUpdatedDate: new Date(),
  };
};

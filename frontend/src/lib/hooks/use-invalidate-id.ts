/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {useQueryClient} from '@tanstack/react-query';

export const useInvalidateId = (key: {
  all: () => string[];
  id: (...args: string[]) => string[];
}) => {
  const queryClient = useQueryClient();
  return async (...args: string[]) => {
    await queryClient.invalidateQueries(key.all());
    await queryClient.invalidateQueries(key.id(...args));
  };
};

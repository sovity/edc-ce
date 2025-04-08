/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import {useEffect} from 'react';
import {TITLE_TEMPLATE} from '../title-template';

export const useTitle = (title: string) => {
  useEffect(() => {
    document.title = TITLE_TEMPLATE.replace('%s', title);
  }, [title]);
};

/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {type TranslatedString} from '@/lib/utils/translation-utils';
import {type OperatorDto} from '@sovity.de/edc-client';

export interface PolicyOperatorConfig {
  id: OperatorDto;
  title: TranslatedString;
  description: TranslatedString;
}

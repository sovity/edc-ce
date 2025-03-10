/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {EDC_UI_PROFILE_DATA} from './edc-ui-profile-data';

/**
 * Available Configuration Profiles.
 */
export type EdcUiProfile = keyof typeof EDC_UI_PROFILE_DATA;

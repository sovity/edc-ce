/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {EdcUiProfileConfig} from './edc-ui-profile-config';

/**
 * Type utility for inferring the keys of EDC_UI_PROFILE_DATA as type.
 * see https://stackoverflow.com/a/74691877
 *
 * @param profiles Record<EdcUiProfile, EdcUiProfileUtils>
 */
export const inferEdcUiProfileType = <
  T extends Record<string, EdcUiProfileConfig>,
>(
  profiles: T,
) => profiles;

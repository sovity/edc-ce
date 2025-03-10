/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
export interface HttpDataAddressParams {
  /**
   * (Base) URL of the request
   */
  baseUrl: string;

  /**
   * Http-method
   */
  method: string | null;

  /**
   * Header-Name ("Authorization"), where the secrets are passed into
   */
  authHeaderName: string | null;

  /**
   * Header-Value ("Bearer ...")
   */
  authHeaderValue: string | null;

  /**
   * Secret referencing API Key
   */
  authHeaderSecretName: string | null;

  /**
   * Additional headers to be sent
   */
  headers: Record<string, string>;

  /**
   * Query Parameters
   */
  queryParams: string | null;
}

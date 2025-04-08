/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
export function ok(body: any): Promise<Response> {
  console.log('Fake Backend returns: ', body);
  return new Promise((resolve) => {
    const response = new Response(JSON.stringify(body), {status: 200});
    setTimeout(() => resolve(response), 400);
  });
}

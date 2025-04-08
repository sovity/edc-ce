/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import createNextIntlPlugin from 'next-intl/plugin';

await import('./src/env.js');

const withNextIntl = createNextIntlPlugin();

/** @type {import('next').NextConfig} */
const config = {output: 'standalone'};

export default withNextIntl(config);

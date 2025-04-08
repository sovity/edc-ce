/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
'use client';

import ExternalLink from './links/external-link';

const ContactSupport = () => {
  return (
    <ExternalLink
      href={'https://sovity.de'}
      className="text-sm font-semibold text-gray-900"
      noIcon>
      Contact Support <span aria-hidden="true">&rarr;</span>
    </ExternalLink>
  );
};

export default ContactSupport;

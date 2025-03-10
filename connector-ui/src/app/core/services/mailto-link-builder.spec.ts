/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
import {MailtoLinkBuilder} from './mailto-link-builder';

describe('mailto-link-builder', () => {
  const builder = new MailtoLinkBuilder();

  it('build link with all fields', () => {
    expect(
      builder.buildMailtoUrl(
        'test@test.com',
        'subject abc',
        'body',
        'cc',
        'bcc',
      ),
    ).toEqual(
      'mailto:test@test.com?subject=subject%20abc&body=body&cc=cc&bcc=bcc',
    );
  });
  it('build link with only email', () => {
    expect(builder.buildMailtoUrl('test@test.com')).toEqual(
      'mailto:test@test.com',
    );
  });
});

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

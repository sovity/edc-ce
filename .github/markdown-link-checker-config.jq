#!/usr/bin/env -S jq -nf
{
  "ignorePatterns": [
    {"pattern": "^https?://localhost"},
    {"pattern": "^https?://example"},
    {"pattern": "^https://checkstyle\\.sourceforge\\.io"},
    {"pattern": "^https://www\\.linkedin\\.com"},
    {"pattern": "https://(.*?)\\.azure\\.sovity\\.io"},
    {"pattern": "http://edc2?:"}
  ],
  "replacementPatterns": [
    {
      "pattern": "^/",
      "replacement": "https://github.com/sovity/edc-extensions/blob/\(env | .CI_SHA // ("CI_SHA was null" | halt_error))/"
    }
  ]
}

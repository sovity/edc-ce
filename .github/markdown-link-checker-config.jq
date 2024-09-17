#!/usr/bin/env -S jq -nf
{
  "ignorePatterns": [
    {"pattern": "^https?://localhost"},
    {"pattern": "^https?://example"},
    {"pattern": "^https://checkstyle\\.sourceforge\\.io"},
    {"pattern": "^https://www\\.linkedin\\.com"},
    {"pattern": "https://(.*?)\\.azure\\.sovity\\.io"},
    {"pattern": "http://edc2?:"},
    {"pattern": "^https?://connector:"},
    {"pattern": "^https://github.com/sovity/PMO-Software/issues/"}
  ],
  "replacementPatterns": [
    {
      "pattern": "^https://github.com/sovity/edc-ce/blob/main/",
      "replacement": "https://github.com/sovity/edc-ce/blob/\(env | .CI_SHA // ("CI_SHA was null" | halt_error))/"
    },
    {
      "pattern": "^https://github.com/sovity/edc-ce/tree/main/",
      "replacement": "https://github.com/sovity/edc-ce/tree/\(env | .CI_SHA // ("CI_SHA was null" | halt_error))/"
    },
    {
      "pattern": "^https://github.com/sovity/edc-ce/blob/main/",
      "replacement": "https://github.com/sovity/edc-ce/blob/\(env | .CI_SHA // ("CI_SHA was null" | halt_error))/"
    },
    {
      "pattern": "^https://github.com/sovity/edc-ce/tree/main/",
      "replacement": "https://github.com/sovity/edc-ce/tree/\(env | .CI_SHA // ("CI_SHA was null" | halt_error))/"
    }
  ]
}

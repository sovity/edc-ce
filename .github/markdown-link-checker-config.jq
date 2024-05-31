#!/usr/bin/env -S jq -nf
{
  "ignorePatterns": [
    {"pattern": "^https?://localhost"},
    {"pattern": "^https?://example"},
    {"pattern": "^https://checkstyle\\.sourceforge\\.io"},
    {"pattern": "^https://www\\.linkedin\\.com"},
    {"pattern": "https://(.*?)\\.azure\\.sovity\\.io"},
    {"pattern": "http://edc2?:"},
    {"pattern": "^https?://broker:"},
    {"pattern": "^https?://connector:"}
  ],
  "replacementPatterns": [
    {
      "pattern": "^https://github.com/sovity/edc-extensions/blob/main/",
      "replacement": "https://github.com/sovity/edc-extensions/blob/\(env | .CI_SHA // ("CI_SHA was null" | halt_error))/"
    },
    {
      "pattern": "^https://github.com/sovity/edc-extensions/tree/main/",
      "replacement": "https://github.com/sovity/edc-extensions/tree/\(env | .CI_SHA // ("CI_SHA was null" | halt_error))/"
    }
  ]
}

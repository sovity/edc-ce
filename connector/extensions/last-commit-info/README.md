<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/sovity/edc-extensions">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">EDC-Connector Extension:<br />Last Commit Info</h3>

  <p align="center">
    <a href="https://github.com/sovity/edc-extensions/issues/new?template=bug_report.md">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-extensions/issues/new?template=feature_request.md">Request Feature</a>
  </p>
</div>

## About this Extension
It adds an endpoint `/last-commit-info` on the Management API which returns the commit information of
the last commit of built EDC Connector. Example:
```
commit eabb87eb8c76a82e022ff0400b4b529348d902f4 (HEAD -> main, origin/main)
Merge: 15ece734e 225b18251
Author: First Last <mail>
Date: Mon Mar 13 08:09:15 2023 +0100

    Merge pull request #221 from sovity/feat/ms8

    chore: update to milestone-8

Jar Last Commit Info:
commit 2fe06beaf6027fb4cc06db2adb7d5b4c8ae61b05 (HEAD -> 2023-03-16-edc-extensions-cleanup, origin/2023-03-16-edc-extensions-cleanup)
Author: First Last <mail>
Date: Thu Mar 9 14:50:20 2023 +0100

    feat new image variants, rework documentation, better configuration via MY_EDC vars
```

## Why does this extension exist?

Building EDC Connectors requires one to build one's own EDC Connector and EDC Connector Images.

We need a way to find out the version of running EDC Connector instances.

We found that finding the last commit of the EDC Connector Image was the most accurate way we could judge the
running EDC Connector instance.

Since our EDC Images use our EDC Extensions from this repository we also embed a second "jar last commit info"
during build time of the edc-extensions, which represent all other EDC Extensions of this repository, since
they will always be used with the same version.

## Configuration

### Connector Version
The ENV var `EDC_LAST_COMMIT_INFO` should be set in the Dockerfile via build args.

### JAR Version
The contents of the file `src/main/resources/jar-last-commit-info.txt` will be adjusted by the
repository's GitHub pipeline to reflect the last commit info.

## License
Apache License 2.0 - see [LICENSE](../../../LICENSE)

## Contact
sovity GmbH - contact@sovity.de

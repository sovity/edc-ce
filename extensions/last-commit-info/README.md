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
commit f323f713f001bb5209fe536efb904c7a317b0aa1
Author: Firstname Lastname <first.last@sovity.de>
Date:   Mon Mar 20 15:40:38 2023 +0100

    chore update CHANGELOG.md for release (#196)
```

## Why does this extension exist?

Building EDC Connectors requires one to build one's own EDC Connector and EDC Connector Images.

We need a way to find out the version of running EDC Connector instances.

We found that finding the last commit of the EDC Connector Image was the most accurate way we could judge the
running EDC Connector instance.

Since our EDC Images often use our EDC Extensions from this repository we also embed a second "jar last commit info"
during build time of the edc-extensions, which can then represent all other EDC Extensions of this repository, since
they will always be used with the same version.

## Configuration

### Connector Version
The ENV var `EDC_LAST_COMMIT_INFO` should be set in the Dockerfile via build args.

### JAR Version
The contents of the file `src/main/resources/jar-last-commit-info.txt` will be adjusted by the
edc-extensions GitHub pipeline to reflect the last commit info.

## License
Apache License 2.0 - see [LICENSE](../../LICENSE)

## Contact
sovity GmbH - contact@sovity.de

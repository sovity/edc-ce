<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/sovity/edc-ui">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">EDC-Connector Extension:<br />Last Commit Info</h3>

  <p align="center">
    <a href="https://github.com/sovity/edc-extensions/issues">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-extensions/issues">Request Feature</a>
  </p>
</div>

## About this Extension
It adds an API endpoint `/last-commit-info` which returns the commit information of 
the last commits of built EDC Connector and the edc-extensions JAR.

## Why does this extension exist?

Building EDC Connectors always requires you to build your own EDC Connector and EDC Connector Image.

We needed a way to find out the versions of running EDC Connector instances.

We found that finding the last commit of the EDC Connector Image was the most accurate way we could judge the 
running EDC Connector instance.

Since our EDC Images often use our EDC Extensions from this repository we also embed a second "jar last commit info"
during build time of the edc-extensions, which can then represent all other EDC Extensions of this repository, since
they will always be used with the same version.

## Configuration

### Connector Version
The ENV Var `EDC_LAST_COMMIT_INFO` should be set in the Dockerfile via build args.

### JAR Version
The contents of the file `src/main/resources/jar-last-commit-info.txt` will be by the 
edc-extensions GitHub pipeline to reflect the last commit info.

## License
Apache License 2.0 - see [LICENSE](../../LICENSE)

## Contact
Sovity GmbH - contact@sovity.de 

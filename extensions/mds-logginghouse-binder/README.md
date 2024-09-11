<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/sovity/edc-ce">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">EDC-Connector Extension:<br />MDS Contract Termination - LoggingHouse binder</h3>

  <p align="center">
    <a href="https://github.com/sovity/edc-ce/issues/new?template=bug_report.md">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-ce/issues/new?template=feature_request.md">Request Feature</a>
  </p>
</div>


## About this Extension

It links the Contract Termination events with the LoggingHouse. 

## Why does this extension exist?

MDS needs to log the events generated when terminating a contract with their Logging House extension.
The Logging House is an external dependency and the linkage must only happen for the MDS variant.

This extension implements this specific task.

## Architecture

```mermaid
flowchart TD
    Binder(MDS LoggingHouse Binder) --> LoggingHouse(Logging House Extension)
    Binder(MDS LoggingHouse Binder) --> ContractTermination(Contract Termination Extension)
    MDS(MDS CE) --> Binder
```

## License

Apache License 2.0 - see [LICENSE](../../LICENSE)

## Contact

sovity GmbH - contact@sovity.de

<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/sovity/edc-ce">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">EDC-Connector Extension:<br />Contract Termination</h3>

  <p align="center">
    <a href="https://github.com/sovity/edc-ce/issues/new?template=bug_report.md">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-ce/issues/new?template=feature_request.md">Request Feature</a>
  </p>
</div>


## About this Extension

To allow contracts termination and cancellation while no official specification for this feature is available, we added our own extension, keeping the Core EDC intact.

## Why does this extension exist?

Contracts termination is not natively supported the in the EDC. Contracts in general can be terminated and this feature has been added here, waiting for an official implementation to be available.

## Details

When a User clicks "Terminate contract" on a contract agreement, a request is sent to the EDC to mark the contract agreement as terminated, followed by a notification and registration of that same termination on the counterpart's side.

The termination is saved in the EDC's database.
Any transfer started from this contract agreement will be rejected.

The contract agreements' implementation doesn't interfere with the existing EDC features besides forcefully preventing transfers that are started on terminated contract agreements.
1
## License

Apache License 2.0 - see [LICENSE](../../LICENSE)

## Contact

sovity GmbH - contact@sovity.de

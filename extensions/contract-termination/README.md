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

Using our [`sovity-messenger`](../sovity-messenger) extension, both providers and consumers can now terminate contracts in a transparent way. Contract termination information is persisted in its own table on both sides and terminated contracts will be prevented from being transferable.

## Why does this extension exist?

Contracts termination is not natively supported by the Data Space Protocol (DSP) or in the Eclipse EDC. Customer Feedback and real-world Dataspace projects have proven for there to be many reasons for a party to want to terminate a contract. We want to enable contract termination in a transparent way for all participating parties, the provider, the consumer, and if necessary, the authority via the Logging House.

## Details

When a User clicks "Terminate contract" on a contract agreement, a request is sent to the EDC to mark the contract agreement as terminated, followed by a notification and registration of that same termination on the counterpart's side.

The termination is saved in the EDC's database.
Any transfer started from this contract agreement will be rejected.

## License

Apache License 2.0 - see [LICENSE](../../LICENSE)

## Contact

sovity GmbH - contact@sovity.de

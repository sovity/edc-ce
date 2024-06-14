<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/sovity/edc-extensions">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">EDC-Connector Extension:<br />Sovity Messenger</h3>

  <p align="center">
    <a href="https://github.com/sovity/edc-extensions/issues/new?template=bug_report.md">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-extensions/issues/new?template=feature_request.md">Request Feature</a>
  </p>
</div>

## About this Extension

This extension provides a convenient way to exchange messages between connectors.

## Why does this extension exist?

To provide a simple way to exchange messages between EDCs without complying with the JsonLd conventions and with minimal setup.

## Demo

You can find a demo project in [sovity-messenger-demo](../sovity-messenger-demo).

The 2 key entry points are:
- The configuration of the backend, to receive messages, in [SovityMessengerDemo.java](../sovity-messenger-demo/src/main/java/de/sovity/edc/extension/sovitymessenger/demo/SovityMessengerDemo.java)
- The usage of the custom messages, implemented as a client e2e test [SovityMessengerDemoTest.java](../sovity-messenger-demo/src/test/java/de/sovity/edc/extension/sovitymessenger/demo/SovityMessengerDemoTest.java)

For more information, check the documentation in that project and in the the Sovity Messenger's [api](src%2Fmain%2Fjava%2Fde%2Fsovity%2Fedc%2Fextension%2Fmessenger%2Fapi).

## License

Apache License 2.0 - see [LICENSE](../../LICENSE)

## Contact

sovity GmbH - contact@sovity.de

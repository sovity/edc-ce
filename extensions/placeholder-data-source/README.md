<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/sovity/edc-ce">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">EDC-Connector Extension:<br />Sovity Messenger</h3>

  <p align="center">
    <a href="https://github.com/sovity/edc-ce/issues/new?template=bug_report.md">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-ce/issues/new?template=feature_request.md">Request Feature</a>
  </p>
</div>

## About this Extension

Provides a placeholder endpoint for on-request offers.

## Why does this extension exist?

This extension exists to inform the asset's consumer upon data retrieval that they should contact the provider and take extra steps to access the data.

## Configuration

`MY_EDC_DATASOURCE_PLACEHOLDER_BASEURL` / `my.edc.datasource.placeholder.baseurl` must be set to point to the placeholder endpoint's base URL.
`/data-source/placeholder/asset/` will be appended to this base URL to make it the placeholder data source endpoint. This will be the address as seen by the consumer.

---

On a production system, the base URL it could be: 

`https://mycompany.com/path/to/backend` 

with a placeholder value:

`https://mycompany.com/path/to/backend/data-source/placeholder/asset/` 

and a full path to the asset's data:

`https://mycompany.com/path/to/backend/data-source/placeholder/asset?email=foo%40example.com&subject=Contact+us+now`.

---

On a system started with docker-compose, it will be pointing to the DSP port on the provider's EDC

`http://edc:11003/`

`http://edc:11003/data-source/placeholder/asset/`

`http://edc:11003/data-source/placeholder/asset?email=foo%40example.com&subject=Contact+us+now`

---

During local/dev/unit test execution, it will be pointing to the DSP port on the provider's EDC

`http://localhost:12345/`

where `12345` would be chosen at random

`http://localhost:12345/data-source/placeholder/asset/`

`http://localhost:12345/data-source/placeholder/asset?email=foo%40example.com&subject=Contact+us+now`

## License

Apache License 2.0 - see [LICENSE](../../LICENSE)

## Contact

sovity GmbH - contact@sovity.de


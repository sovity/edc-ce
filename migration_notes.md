
# Migration notes

### Assigner added in policy when negotiating

`ContractNegotiationRequest` now needs a `counterPartyId` because it's missing from the offerId's policy and must later be patched in `de.sovity.edc.ext.wrapper.api.ui.pages.contract_negotiations.ContractOfferMapper.buildContractOffer`

### Changes in the DCat prop

[http://www.w3.org/ns/dcat`#`]

no more

[http://www.w3.org/ns/dcat`/`]

### dataSinkProperties

Must NOT use the shorthand `baseUrl`, `type`, ...

Must use the FQDN `https://w3id.org/edc/v0.0.1/ns/baseUrl`, `https://w3id.org/edc/v0.0.1/ns/type`, ...

### DB reset

Some tests depended on the DB's reset `@BeforeEach` method.

In some cases if was fixed with `@Order`ing, in others with deletion of the problematic asset, or changing queries to be more selective.

### Port re-use

```
CanGetAgreementPageForTerminatedContractTest > initializationError FAILED
  org.eclipse.edc.spi.EdcException: Failed to start EDC runtime
    org.eclipse.edc.spi.EdcException: org.eclipse.edc.spi.EdcException: Error starting Jetty service
      org.eclipse.edc.spi.EdcException: Error starting Jetty service
        java.io.IOException: Failed to bind to 0.0.0.0/0.0.0.0:49678
          java.net.BindException: Address already in use
```

Why? The port seems to be allocated randomly.

### Provider push parameterization is deprecated

ProviderPushTransferDataFlowController is deprecated since 0.5.1.

See `git show aa30b4805268821c0852d50950b69e78a3f7efcb` in core EDC.

### Management API v2/v3

#### Asset

`api/management/v2/assets` seems gone.

`api/management/v3/assets` must be used.

#### Policy

`api/management/v2/assets` is deprecated.

### Distribution key

The distribution key was moved from `http://www.w3.org/ns/dcat#distribution` to `https://semantic.sovity.io/dcat-ext#distribution`.

## TODOs

### Media type went missing

Play whack-a-mole between the new EDC usage of distribution, the `Media Type` assertion and the catalog fetching.


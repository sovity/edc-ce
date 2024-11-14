
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

All the management API calls should be done on `v3`.

### Distribution key

The distribution key was moved from `http://www.w3.org/ns/dcat#distribution` to `https://semantic.sovity.io/dcat-ext#distribution`.

### Different answers

in contract negotiation answer:

`edc:contractAgreementId` -> `contractAgreementId`, same for other fields.

in contract negotiation state:

`edc:state` -> `state`, same for other fields.

### EDC conflicting tables declarations

`edc_lease` is declared 6 times.

Once with `lease_duration INTEGER DEFAULT 60000 NOT NULL`
5 times with `lease_duration INTEGER NOT NULL`

Using the second option for the baseline.

## TODOs

### Media type went missing

Play whack-a-mole between the new EDC usage of distribution, the `Media Type` assertion and the catalog fetching.

### Attribute deprecation

The attribute https://w3id.org/edc/v0.0.1/ns/providerId has been deprecated in type https://w3id.org/edc/v0.0.1/ns/ContractRequest, please use http://www.w3.org/ns/odrl/2/assigner

###

Release as 0.0.1-20241025-2408-alpha1


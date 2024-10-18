
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

## TODOs

### Media type went missing

Play whack-a-mole between the new EDC usage of distribution, the `Media Type` assertion and the catalog fetching.

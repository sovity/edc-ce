# Example Use Case App Tutorial - Chat App

## Chapter 3: Establishing Connections

### Negotiating a Contract

To exchange notifications, the sovity EDCs need to establish contracts with each other.

> While bi-directional contracts are planned, this tutorial currently uses one contract for each connection direction.

The backend already registers counterparties in in-memory storage upon API calls.
Here, our focus is on the direct interaction with the EDC.

We initiate contract negotiation between the current sovity EDC and the counterparty.

```markdown
```kotlin
// File:
// backend/src/main/kotlin/de/sovity/chatapp/services/CounterpartyService.kt

// Replace:
// TODO negotiate contract with EDC

// With:
// connect via EDC
// callback will continue the connection establishment process
edcService.negotiateContract(
    participantId = dto.participantId,
    connectorEndpoint = dto.connectorEndpoint,
)
```
```

```markdown
```kotlin
// File:
// backend/src/main/kotlin/de/sovity/chatapp/services/edc/EdcService.kt

// Replace:
// TODO fun negotiateContract(...) { ... }

// With:
fun negotiateContract(participantId: String, connectorEndpoint: String) {
    val negotiations = edcClient.useCaseApi().negotiateAll(
        NegotiateAllQuery.builder()
            .participantId(participantId)
            .connectorEndpoint(connectorEndpoint)
            .filter(
                listOf(
                    AssetFilterConstraint.builder()
                        .assetPropertyPath(listOf(Prop.Edc.ID))
                        .operator(AssetFilterConstraintOperator.EQ)
                        .value("chat-app")
                        .build()
                )
            )
            .callbackAddresses(
                listOf(
                    CallbackAddressDto.builder()
                        .url(notificationCallbackUrls.getContractNegotiationFinalizedUrl())
                        .events(listOf(CallbackAddressEventType.CONTRACT_NEGOTIATION_FINALIZED))
                        .build(),
                    CallbackAddressDto.builder()
                        .url(notificationCallbackUrls.getContractNegotiationTerminatedUrl())
                        .events(listOf(CallbackAddressEventType.CONTRACT_NEGOTIATION_TERMINATED))
                        .build(),
                )
            )
            .build()
    )
    require(negotiations.size >= 1) {
        "No asset 'chat-app' found."
    }
}
```
```

The `negotiateAll` endpoint helps to quickly negotiate contracts by:
- Negotiating all matching data offers, e.g. the data offer with the ID `chat-app`
- Being useful for use case applications that handle access control within the app rather than relying solely on EDC policies

Also, notice the two callbacks provided - these notify us when the asynchronous process is complete, so we don't have to poll the EDC.

```markdown
```kotlin
// File:
// backend/src/main/kotlin/de/sovity/chatapp/api/NotificationResource.kt

@POST
@Consumes(MediaType.APPLICATION_JSON)
@Path("negotiation-finalized")
fun onNegotiationFinalized(
    event: EdcEventWrapper<EdcEventContractNegotiationFinalized>
) {
    eventService.onContractNegotiationFinalized(event.payload)
}

@POST
@Consumes(MediaType.APPLICATION_JSON)
@Path("negotiation-terminated")
fun onNegotiationTerminated(
    event: EdcEventWrapper<EdcEventContractNegotiationTerminated>
) {
    eventService.onContractNegotiationTerminated(event.payload)
}
```
```

### Initiating the Transfer

Once the contract negotiation is successful, we have a valid contract agreement. To transfer data, we need to start a transfer process.

To save resources, it's best to have one permanently running transfer process per partner we exchange data with.

We initiate the transfer process, which will stay in the "STARTED" status to allow authentication with the other EDC, by calling the `initiateTransfer` endpoint.

```markdown
```kotlin
// File:
// backend/src/main/kotlin/de/sovity/chatapp/services/EventService.kt

// Replace:
// TODO call EDC to initiate transfer

// With:
edcService.initiateTransfer(event.counterPartyId)
```
```

```markdown
```kotlin
// File:
// backend/src/main/kotlin/de/sovity/chatapp/services/edc/EdcService.kt

// Replace:
// TODO fun initiateTransfer(...) { ... }

// With:
fun initiateTransfer(participantId: String) {
    val counterparty = counterpartyStore.findByIdOrThrow(participantId)
    edcClient.uiApi().initiateTransferV2(
        UiInitiateTransferRequest.builder()
            .contractAgreementId(counterparty.contractAgreementId!!)
            .type(UiInitiateTransferType.HTTP_DATA_PROXY)
            .callbackAddresses(
                listOf(
                    CallbackAddressDto.builder()
                        .url(notificationCallbackUrls.getTransferStartedUrl())
                        .events(listOf(CallbackAddressEventType.TRANSFER_PROCESS_STARTED))
                        .build()
                )
            )
            .build()
    )
}
```
```

### Ready to send

We wait for the notification indicating the transfer is ready:

```markdown
```kotlin
// File:
// backend/src/main/kotlin/de/sovity/chatapp/api/NotificationResource.kt

@POST
@Consumes(MediaType.APPLICATION_JSON)
@Path("transfer-started")
fun onTransferStarted(
    event: EdcEventWrapper<EdcEventTransferProcessStarted>
) {
    eventService.onTransferStarted(event.payload)
}
```
```

Then we mark the counterparty as online, causing our UI to update automatically:

```markdown
```kotlin
// File:
// backend/src/main/kotlin/de/sovity/chatapp/services/EventService.kt

fun onTransferStarted(event: EdcEventTransferProcessStarted) {
    Log.info("Transfer process started event received for transfer ${event.transferProcessId}")
    val counterpartyId = counterpartyStore.findByContractAgreementIdOrThrow(event.contractId).participantId

    // Use Transfer Process, Mark Connection as online
    counterpartyStore.update(counterpartyId) {
        it.copy(
            status = ConnectionStatusDto.ONLINE,
            transferProcessId = event.transferProcessId,
            lastUpdate = OffsetDateTime.now()
        )
    }
}
```
```

### Notes

- Currently, the counterparty only establishes a counter-connection after the first message is received. This will be addressed in the next chapter. 
- Entering a Counterparty URL and Participant ID is not user-friendly yet - expect integrated lookup sources in the near future.

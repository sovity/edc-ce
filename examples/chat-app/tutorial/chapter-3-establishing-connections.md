# Example Use Case App Tutorial - Chat App

## Chapter 3: Establishing Connections

### Negotiating a Contract

To be able to exchange notifications, the EDCs must establish contracts with each other.

> While bi-directional contracts are underway, this tutorial currently works with one contract for each direction of connection.

Existing code in the backend already handles registering a counterparty upon API call in the In-Memory Storage. What we want to focus on is the direct interactions with the EDC.

We initiate a contract negotiation that will take place between the current EDC and the counterparty.

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

The `negotiateAll` endpoint helps quick negotiate contracts:
- It negotiates all matching data offers, in this case, the data offer with the ID `chat-app`
- The negotiateAll endpoint is useful for use case applications that achieve access control in the use case application itself rather than relying on EDC policies.

Also notice the two given callbacks, we want to be notified upon completion of the asynchronous process and not have to poll the EDC.

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

### Initiating the Transfer

Upon successful contract negotiation, we have a functioning contract agreement. To actually transfer data, we need to start a transfer process..

To save resources, we ideally want to have one permanently running transfer process for every partner we exchange data with.

To initiate the transfer process, which will remain open in the status "STARTED" to allow us to authenticate against the other EDC, we call the `initiateTransfer` endpoint.

```kotlin
// File:
// backend/src/main/kotlin/de/sovity/chatapp/services/EventService.kt

// Replace:
// TODO call EDC to initiate transfer

// With:
edcService.initiateTransfer(event.counterPartyId)
```

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

### Ready to send

We await the notification for the transfer to be ready:

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

Now we mark the counterparty as online, our UI will update itself.

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


### Notes

- Currently, the counterparty will only establish a counter-connection when the first message is received. We will look at this in the next chapter.
- Entering a Counterparty URL and a Participant ID is not exactly user-friendly - Expect proper integrations of look-up sources in the near future.

# Example Use Case App Tutorial - Chat App

## Chapter 5: Sending Messages

### About EDRs

An EDR (Endpoint Data Reference) contains an authentication token that allows direct calls to the counterparty's Data Plane.
A single EDR is valid for a few minutes and can be reused during that time to reduce EDC interactions and increase throughput.

Each EDR is created for a single transfer process.
Due to its limited lifetime for security reasons, it needs to be refreshed periodically.

To manage this efficiently, we implement an EDR Cache:

```markdown
```kotlin
// File:
// backend/src/main/kotlin/de/sovity/chatapp/services/edc/EdrCache.kt

// Replace:
// TODO getEdr(...) { ... }

// With:
fun getEdr(participantId: String): EdrDto {
    val counterparty = counterpartyStore.findByIdOrThrow(participantId)
    val transferProcessId = counterparty.transferProcessId
    require(transferProcessId != null) {
        "Participant is not online. No running transfer process for participant $participantId"
    }

    val oldEdr = counterparty.edr
    if (oldEdr != null) {
        val isExpired = oldEdr.expiresAt?.isAfter(
            OffsetDateTime.now().minusSeconds(5)
        ) ?: false
        if (!isExpired) {
            return oldEdr
        }
    }

    // Re-fetch EDR
    Log.info("Requesting new EDR for $participantId")
    val newEdr = edcClient.useCaseApi().getTransferProcessEdr(transferProcessId)

    // Save EDR for re-use
    Log.info("Got EDR for $participantId: ${newEdr.baseUrl} ${newEdr.authorizationHeaderValue}")
    counterpartyStore.update(participantId) {
        it.copy(edr = newEdr)
    }
    return newEdr
}
```
```

### Sending Messages

```markdown
```kotlin
// File:
// backend/src/main/kotlin/de/sovity/chatapp/services/MessageService.kt

// Replace:
// TODO edcService.sendMessage(...)

// With:
// Wrap in error handling
edcService.sendMessage(
    participantId = participantId,
    message = message
)
```
```

```markdown
```kotlin
// File:
// backend/src/main/kotlin/de/sovity/chatapp/services/edc/EdcService.kt

fun sendMessage(participantId: String, message: String) {
    val messageNotificationDto = MessageNotificationDto(
        message = message,
        senderConnectorEndpoint = ownConnectorEndpoint
    )
    val edr = edrCache.getEdr(participantId)
    counterpartyNotificationApiClient.sendMessage(edr, messageNotificationDto)
}
```
```

Because the use-case asset is configured with _body parametrization_ enabled, we send messages in the HTTP body of requests to the Data Plane.  
The endpoint URL can be found in the freshly fetched EDR.

### Counterparty API Client Implementation

We use Quarkus' Rest Client generation to build an API client from a JAX-RS interface.
This allows us to specify the base URL and authentication token dynamically for each call.

```markdown
```kotlin
// File:
// backend/src/main/kotlin/de/sovity/chatapp/api/CounterpartyNotificationApiClient.kt

// Replace:
// TODO sendMessage(...) { ... }

// With:
fun sendMessage(
    edrDto: EdrDto,
    messageNotificationDto: MessageNotificationDto,
) {
    val client = QuarkusRestClientBuilder.newBuilder()
        .baseUri(URI(edrDto.baseUrl))
        .build(CounterpartyNotificationApi::class.java)

    client.onReceiveMessage(
        messageNotificationDto,
        edrDto.authorizationHeaderValue
    )
}

/**
 * Quarkus will help us translate this interface into a REST client
 */
private interface CounterpartyNotificationApi {
    /**
     * Will hit [NotificationResource.onReceiveMessage] via EDR
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    fun onReceiveMessage(
        /**
         * Request body. For this we had to enable request body parameterization
         */
        messageNotificationDto: MessageNotificationDto,

        @HeaderParam("Authorization")
        authorizationHeaderValue: String,
    )
}
```
```

# Example Use Case App Tutorial - Chat App

## Chapter 5: Sending Messages

### About EDRs

An EDR contains an authentication token that allows one to directly call the counterparty's data plane. A single EDR has a lifetime of minutes and can thus be re-used and reduce EDC interactions, allowing for higher throughput.

An EDR is coined for a single transfer process, but due to its expiration time for security reasons, it will need to be re-fetched eventually.

To handle this, we implement an EDR Cache:

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

### Sending Messages

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

Because the use-case asset is configured to have _body parametrization_ enabled, we can send our messages in the HTTP body in a request to the Data Plane. The endpoint for that can be found in a freshly fetched EDR.

### Counterparty API Client implementation

We use Quarkus' Rest Client generation to help us build an API client from a JAX-RS interface. We need to be able to specify the base URL and the token for each call.

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






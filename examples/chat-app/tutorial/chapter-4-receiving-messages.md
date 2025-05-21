# Example Use Case App Tutorial - Chat App

## Chapter 4: Receiving Messages

### Saving the Message

Previously, we already added the endpoint for receiving messages. In the EDC context, a use case application is itself responsible for saving the data. Thus, in context of the chat app, both participants will be saving their own versions of the chat history. The Connector is used only to transfer data.

Saving each message in our in-memory storage is sufficient for tutorial purposes.

```kotlin
// File:
// backend/src/main/kotlin/de/sovity/chatapp/services/MessageService.kt

// Replace:
// TODO("Save received message so our UIs can show it")

// With:
// save message
messageStore.create(
    MessageDbRow(
        messageId = UUID.randomUUID().toString(),
        participantId = participantId,
        createdAt = OffsetDateTime.now(),
        message = notification.message,
        messageDirection = MessageDirectionDto.INCOMING,
        status = MessageStatusDto.OK
    )
)
counterpartyStore.update(participantId) {
    it.copy(lastUpdate = OffsetDateTime.now())
}
```

### Establishing the Counter-Connection

Upon receiving the first message, a counter connection shall be established. 

> The reason why this happens now and not at the incoming contract negotiation success is that we do not listen to _all_ - and thus not to incoming - contract finalization events
> 

```kotlin
// File:
// backend/src/main/kotlin/de/sovity/chatapp/services/MessageService.kt

// Replace:
// TODO establish counter-connection

// With:
// establish connection if not already done
if (counterpartyStore.findByIdOrNull(participantId) == null) {
    counterpartyService.create(
        CounterpartyAddDto(
            participantId = participantId,
            connectorEndpoint = notification.senderConnectorEndpoint
        )
    )
}
```





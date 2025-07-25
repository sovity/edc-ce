# Example Use Case App Tutorial - Chat App

## Chapter 4: Receiving Messages

### Saving the Message

We have already added the endpoint to receive messages.  
In the context of EDC, the use case application itself is responsible for saving the data.

For the Chat App, this means each participant saves their own version of the chat history.  
The Connector’s role is only to transfer data between participants.

For tutorial purposes, saving each message in our in-memory storage is sufficient.

```markdown
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
```

### Establishing the Counter-Connection

When the first message is received, a counter-connection will be established.

> Counter-connection happens earliest at message reception by the counterparty. Not earlier even if possible, e.g. when a contract negotiation is successfull completed by the counterparty beforehand.

```markdown
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
```

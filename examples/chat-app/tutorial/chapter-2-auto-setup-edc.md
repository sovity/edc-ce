# Example Use Case App Tutorial - Chat App

## Chapter 2: Automatically Setting Up the sovity EDC on Startup

### Preparing the Data Source

Requests from other participants need to land somewhere in our application.  
To achieve this, we will add an endpoint behind our reception asset that will be called via the Data Plane to receive messages.

```markdown
```kotlin
// File:
// backend/src/main/kotlin/de/sovity/chatapp/api/NotificationResource.kt

@POST
@Consumes(MediaType.APPLICATION_JSON)
@Path("receive-message")
fun onReceiveMessage(
    @HeaderParam("Edc-Bpn") edcBpn: String,
    notification: MessageNotificationDto
) {
    messageService.onMessageReceived(edcBpn, notification)
}
```
```

```markdown
```kotlin
// File:
// backend/src/main/kotlin/de/sovity/chatapp/api/NotificationCallbackUrls.kt

// Replace:
// TODO on receive message callback

// With:
fun getOnMessageReceivedUrl(): String =
    "$chatAppBackendUrl/api/notifications/receive-message"
```
```

### Creating the Asset

Creating the data offer requires creating the asset first. If the asset already exists, we will update its data source to ensure it points to the correct endpoint in our chat app backend.

In this example, we identify the Use Case Application's asset by its asset ID, not by any nested property.

```markdown
```kotlin
// File:
// backend/src/main/kotlin/de/sovity/chatapp/ChatApplication.kt

// Replace:
// TODO auto-setup edc resources

// With:
// Recreate chat app relevant edc resources correctly
edcService.configureAsset()
```
```

```markdown
```kotlin
// File:
// backend/src/main/kotlin/de/sovity/chatapp/services/edc/EdcService.kt

// Replace:
// TODO create asset 'chat-app'

// With:
val onMessageReceiveNotification = UiDataSource.builder()
    .type(DataSourceType.HTTP_DATA)
    .httpData(
        UiDataSourceHttpData.builder()
            .method(UiDataSourceHttpDataMethod.POST)
            .baseUrl(notificationCallbackUrls.getOnMessageReceivedUrl())
            .enableBodyParameterization(true)
            .build()
    )
    .build()

if (edcClient.uiApi().isAssetIdAvailable("chat-app").available!!) {
    Log.info("Creating asset 'chat-app'")
    edcClient.uiApi().createAsset(
        UiAssetCreateRequest.builder()
            .id("chat-app")
            .dataSource(onMessageReceiveNotification)
            .build()
    )
} else {
    Log.info("Updating asset 'chat-app'")
    edcClient.uiApi().editAsset(
        "chat-app",
        UiAssetEditRequest.builder()
            .dataSourceOverrideOrNull(onMessageReceiveNotification)
            .build()
    )
}
```
```

### Publishing The Data Offer

For this demo use case, we create an unrestricted policy for our data offering.

In real-world applications, use case apps can either manage contract definitions dynamically to restrict which participants see which data offers - effectively hiding use case participation - or they can adopt an unrestricted data offer and enforce access control within the app by differentiating incoming requests.

```markdown
```kotlin
// File:
// backend/src/main/kotlin/de/sovity/chatapp/services/edc/EdcService.kt

// Replace:
// TODO publish data offer

// With:
if (edcClient.uiApi().isContractDefinitionIdAvailable("chat-app").available!!) {
    Log.info("Creating contract definition 'chat-app'")
    edcClient.uiApi().createContractDefinition(
        ContractDefinitionRequest.builder()
            .contractDefinitionId("chat-app")
            .accessPolicyId("always-true") // unrestricted access
            .contractPolicyId("always-true") // unrestricted access
            .assetSelector(
                listOf(
                    UiCriterion.builder()
                        .operandLeft(Prop.Edc.ID)
                        .operator(UiCriterionOperator.EQ)
                        .operandRight(
                            UiCriterionLiteral.builder()
                                .type(UiCriterionLiteralType.VALUE)
                                .value("chat-app")
                                .build()
                        )
                        .build()
                )
            )
            .build()
    )
} else {
    Log.info("Already existing contract definition 'chat-app'. Skipping creation")
}
```
```

If the contract definition already exists, we do **not** override it.
This allows customizations, for example limiting the contract to certain participants or timeframes.

The policy named "always-true" is created when the sovity EDC starts, enabling easy publishing of unrestricted data offers.

### Recovering contracts

Coming soon: In practice, existing contracts, negotiations, and transfer processes can be recovered on application startup. This improves the cleanliness and reliability of interactions between sovity EDCs.

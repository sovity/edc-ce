# Example Use Case App Tutorial - Chat App

## Chapter 2: Automatically Setting Up the EDC on Startup

### Preparing the Data Source

Requests from other participants will need to land somewhere in our application. For this we shall add an endpoint behind our reception asset that will be called via the Data Plane to receive messages.

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

```kotlin
// File:
// backend/src/main/kotlin/de/sovity/chatapp/api/NotificationCallbackUrls.kt

// Replace:
// TODO on receive message callback

// With:
fun getOnMessageReceivedUrl(): String =
    "$chatAppBackendUrl/api/notifications/receive-message"
```

### Creating the Asset

Part of creating the data offer is creating the asset. We will create the asset, but should it already exist, we shall only update the data source, ensuring it points at the right endpoint in our chat app backend.

In this example we do not identify the Use Case Application's asset by any nested property, but by the asset's ID.

```kotlin
// File:
// backend/src/main/kotlin/de/sovity/chatapp/ChatApplication.kt

// Replace:
// TODO auto-setup edc resources

// With:
// Recreate chat app relevant edc resources correctly
edcService.configureAsset()
```

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

### Publishing The Data Offer

For our demo use-case, we can create an unrestricted policy for our data offering. 

> In practice, use case applications can either micro manage contract definitions on the fly to restrict visible data offers to certain participants and hide the use case participation, or also adopt an unrestricted data offer and decide to enforce access control in the app itself by differentiating the incoming requests.

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

> In case the contract definition already exists, we don't override it, so custom changes to the contract definition, e.g. to limit it to certain participants or timespans can be done.

> The policy "always-true" gets created on EDC startup to allow easily publishing unrestricted data offers.

### Recovering contracts

> Coming soon. In practice, existing contracts, negotiations and transfer processes can be recovered on application startup to achieve more clean interactions between the EDCs.

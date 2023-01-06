rootProject.name = "broker-extension"
include("extensions:catalog-transfer-extension")
findProject(":extensions:catalog-transfer-extension")?.name = "catalog-transfer-extension"
include("extensions:event-asset-provider")
findProject(":extensions:event-asset-provider")?.name = "event-asset-provider"
include("connector")

rootProject.name = "mds-edc-broker"
include("extensions:policies")
findProject(":extensions:policies")?.name = "policies"
include("extensions:catalog-transfer-extension")
findProject(":extensions:catalog-transfer-extension")?.name = "catalog-transfer-extension"
include("extensions:event-asset-provider")
findProject(":extensions:event-asset-provider")?.name = "event-asset-provider"
include("centralized-mds-broker-edc")

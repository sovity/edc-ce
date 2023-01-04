rootProject.name = "mds-extension"
include("extensions:broker")
findProject(":extensions:broker")?.name = "broker"
include("extensions:clearinghouse")
findProject(":extensions:clearinghouse")?.name = "clearinghouse"
include("extensions:event-provider")
findProject(":extensions:event-provider")?.name = "event-provider"
include("connector")

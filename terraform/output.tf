output "dashboard-url" {
    value = "http://${azurerm_container_group.edc-dashboard.dns_name_label}.${var.location}.azurecontainer.io"
}
# Configure the Azure provider
terraform {

  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = ">= 2.98.0"
    }
  }
}

provider "azurerm" {
  features {
  }
}

data "azurerm_client_config" "current" {}
data "azurerm_subscription" "primary" {}

variable "resourcegroup" {
  default = "edc-ui-resources"
}

# registration service = ion crawler
resource "azurerm_container_group" "edc-dashboard" {
  name                = "edc-data-dashboard"
  location            = "westeurope"
  resource_group_name = var.resourcegroup
  os_type             = "Linux"
  ip_address_type     = "public"
  dns_name_label      = "edc-dashboard"
  image_registry_credential {
    password = var.docker_repo_password
    server   = var.docker_repo_url
    username = var.docker_repo_username
  }

  container {
    cpu    = 2
    image  = "${var.docker_repo_url}/edc-demo-client:latest"
    memory = "2"
    name   = "edc-data-dashboard"

    ports {
      port     = 4200
      protocol = "TCP"
    }

    environment_variables = {
    }
  }
}

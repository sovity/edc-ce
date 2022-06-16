variable "docker_repo_password" {
  type = string
}

variable "location"{
  type = string
  default = "westeurope"
}

variable "docker_repo_username" {
  type = string
}

variable "docker_repo_url" {
  type    = string
  default = "edcdemo.azurecr.io"
}
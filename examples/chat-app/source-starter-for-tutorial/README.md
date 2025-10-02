# Chat App

Chat app serving as a developer tutorial how to develop EDC apps and as a demo app for EDCs 

## Why

Onboarding of use case developers follows a steep learning curve, since EDCs follows a complex terminology, processes and domain. At the same time demonstration of the technology is difficult, since it is only a tooling.

## Requirements

- Java 21
- Node 22 + Yarn
- Bash / Git Bash
- Docker

You need to provide a GitHub access token with the `packages:read` scope to access Maven repositories for dependencies.
To add the token, create a file named `gradle.properties` in `HOME_DIRECTORY/.gradle/` with the following content:

```
gpr.user=your-github-username
gpr.key=your-github-access-token
```

## Start Two Chat Apps Locally

```shell script
./start.sh
```

This starts a docker-compose:

<table width="100%">
<thead>
<tr>

<td></td>
<th>"Provider"</th>
<th>"Consumer""</th>

</tr>
</thead>

<tbody>

<tr>
<td>Chat App</td>
<td>

- UI: http://localhost:23000
- Backend: http://localhost:28080

</td>
<td>

- UI: http://localhost:13000
- Backend: http://localhost:18080

</td>
</tr>
<tr>
<td>Connector</td>
<td>

- UI: http://localhost:11000
- Management API Access:
  - Management API URL: http://localhost:11000/api/management
  - Management API Key: `SomeOtherApiKey`
- Dataspace Identity:
  - Participant ID: `provider`
  - Connector Endpoint: http://provider/api/v1/dsp

</td>
<td>

- UI: http://localhost:22000
- Management API Access:
  - Management API URL: http://localhost:22000/api/management
  - Management API Key: `SomeOtherApiKey`
- Dataspace Identity:
  - Connector Endpoint: http://consumer/api/v1/dsp
  - Participant ID: `consumer`

</td>
</tr>
</tbody>
</table>

---
icon: gear-complex-code
---

## Callbacks

### Static Callback

The **Static Callback** feature enables external systems to be notified upon completion of specific events or operations.
This mechanism is essential for workflows that depend on real-time updates or require coordination between multiple services.
Callbacks are triggered when certain control-plane events occur, such as contract negotiation updates or transfer process state changes.
Instead of polling for status, external systems will be invoked automatically with relevant event data.

#### How it works

- The external system provides a static callback URL
- When a relevant event occurs, an HTTP POST request is sent to this URL
- The payload contains event-specific data in JSON format

#### Important Notes

- The callback endpoint must be reachable and able to handle incoming POST requests with JSON payloads
- Authentication and security measures (e.g. TLS) should be managed by the external system

#### Activation and Configuration

**Static Callback functionality is not enabled by default.**

For CaaS users, please contact our service desk team to have the callback feature activated and properly configured for your environment.

#### Additional Resources

- [EDC: Callback Extension](https://github.com/eclipse-edc/Connector/blob/main/extensions/control-plane/callback/callback-static-endpoint/README.md)


### Dynamic Callbacks

Dynamic callbacks enable flexible and runtime-configurable handling of transfer process events, allowing callback URLs to be specified dynamically per transfer rather than being fixed at deployment time.

#### Overview

Unlike static callbacks, which are predefined and configured in the system, **dynamic callbacks** are provided as part of the transfer request, typically via the Management API when initiating a transfer process.
This allows to specify where and how to send notifications for that particular transfer.

#### How Dynamic Callbacks Work

- When creating a transfer process, the callback URL(s) are included dynamically in the request payload
- These URLs are used by the EDC to notify external systems about state changes in the transfer process (e.g. transfer started, completed, failed)
- The callback URLs are stored and invoked by the EDC during the lifecycle of the transfer
- Dynamic callbacks can coexist with static callbacks
- Ensure the callback endpoints are secured and can handle POST requests with event data

#### Example Usage

When starting a transfer process via the Management API, include the callback URL in the request body under the `callbackAddress` field:

```json
{
  "callbackAddress": "{{CALLBACK_URL}}",
  "dataDestination": {
    "type": "HttpProxy"
  },
  "managedResources": true
}
```

The EDC will send event notifications as HTTP POST requests to this URL, enabling real-time integration with external monitoring systems.


#### Additional Resources

- [EDC: Callback](https://github.com/eclipse-edc/Connector/blob/f936e46543f1817336eb566bcbdef7bfdaa8d139/docs/developer/decision-records/2023-02-28-processing-callbacks/README.md)
- [Tractus-X: Callback](https://github.com/eclipse-tractusx/tractusx-edc/blob/372865597890198ae08d55bb1716bcc34610cf20/docs/usage/management-api-walkthrough/06_transferprocesses.md)

---
icon: file-excel
---

# Contract Termination

In the latest Connector versions, both the provider and the consumer have the option of terminating an existing contract. To initiate a termination, simply click on an existing contract in the Contracts area, regardless of whether it is Providing or Consuming, and the red button at the bottom left of the Contract detail view. Please be sure that you want to terminate the contract, because this process is irreversible and a new contract would have to be negotiated if a contract was terminated by mistake.

![Terminate Contract](/docs/images/edc-ui-terminate-1.png)

As soon as the Terminate button is clicked, a new dialog appears as a pop-up in which the reason for termination must be specified, as well as a detailed description. In addition, a checkbox must be ticked to confirm that you are aware of the consequences of terminating the contract. At this point, the contract has not yet been terminated, only when all the required fields have been filled in and then Terminate button has been clicked again at the bottom right of this dialog, the terminate-process will be triggered inevitably.

![Termination Reason Dialog](/docs/images/edc-ui-terminate-1-reason.png)

If a contract has been terminated, regardless of from which side, each side can see this very clearly in the Contracts area by the status "Terminated" and the red icon.
For this purpose, there is also a separate sub-area in the Contracts area via the "Terminated Contracts" tab, which only lists terminated contracts.

![Treminated Contracts Overview](/docs/images/edc-ui-terminate-1-terminated.png)

By clicking on a terminated contract in the Contracts area, you can also see what the other side has entered in the termination details area, i.e. why the contract was terminated and by whom. In addition, a timestamp is provided when the contract was terminated.

![Terminated Contracts Insights](/docs/images/edc-ui-terminate-2.png)

Please note: Only sovity connectors support the functionality of terminating contracts on both sides. If the other connector does not know the functionality of terminating contracts and you nevertheless trigger a contract termination because you use a sovity Connector, the contract is terminated on your side and the other side will ignore the message to terminate the contract, which means that the status of the contract shows as still active in their Connector while being terminated on your side. 
 

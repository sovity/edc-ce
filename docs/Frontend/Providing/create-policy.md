# 3. Create a Policy

Start by clicking on the tab ```Policies``` in the Providing area in the navigation.

By default, a policy has already been created at Connector start, which is always fulfilled and can be used to offer Data-Offers to any other Connector in the same dataspace without restrictions.
To create a new policy click on ```Create policy``` button.

![Create Policy](/docs/images/edc-ui-create-policy.png)

A new policy can now be defined in the window, which can later be used for data offerings.

Three different types of policies are available:
- **Evaluation Time**: If this policy type is selected, a date can be specified so that the data offers are only valid until this date.
- **Participant ID**: With this policy, the data offering can be offered only for specific connectors based on their Connector ID. This Connector ID must be known at this time.
- **Timespan Restriction**: This policy can be used to specify a time period during which the data offering can be accessed.

Policies can also be linked. In other words, several policies can have to be fulfilled at the same time, as well as different parallels, independently of each other, depending on the selection.

![Create Policy Dialog](/docs/images/edc-ui-create-policy-dialog.png)

Click on the ```Create``` button to create the policy.

## Hints
- A policy can only be deleted if it is not used in a Contract Definition.
- Specific Catena-X policies have to be created via Management-APIs.
 

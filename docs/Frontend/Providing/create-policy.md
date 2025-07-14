# 3. Create a Policy

Start by clicking on the tab `Policies` in the `Provide` area in the navigation.

By default, a policy has already been created at Connector start, which is always fulfilled and can be used to offer Data-Offers to any other Connector in the same dataspace without restrictions.
To create a new policy click on `New policy` button.

![Create Policy](/docs/images/provider-policy-overview-1.png)

A new policy can now be defined in the window, which can later be used for data offerings.

Two different types of policies are available:
- **Consumer's Participant ID**: With this policy, the data offering can be offered only for specific connectors based on their Participant ID. This Participant ID must be known at this time.
- **Time Restriction**: This policy can be used to specify a time period during which the data offering can be accessed or negotiated for.

Policies can also be linked. In other words, several policies can have to be fulfilled at the same time, as well as different parallels, independently of each other, depending on the selection.

![Create Policy Dialog](/docs/images/provider-policy-create-1.png)

Click on the `Create Policy` button to create the policy.

## Hints
- A policy can only be deleted if it is not used in a Contract Definition.
- Specific Catena-X policies have to be created via Management-APIs.
 

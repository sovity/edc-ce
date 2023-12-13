What is a Data Space?
========

If data is to be exchanged across companies and aspects such as data sovereignty and interoperability play a role, a data space needs to be crated.
Different technologies and components are used in a Data Space.
With the aim of transferring data from one company to another, so-called connectors are operated as software components in the respective companies, be it on-premises in the company's infrastructure or as a service, managed by a third company.
These connectors allow to systematically query the data offered by the other company and enable subsequent data exchange.
The data provider can restrict who can see its data offers and under what conditions it can be consumed.
The potential consumer of the data must agree to these conditions in order to consume the data.
This process of negotiating the conditions under which the data may be used is called contract negotiation.
If this automated contract negotiation between two connectors and thus between two companies ends successfully, a technical contract is concluded.
Only after the technical contract has been concluded, the data can be transferred from one connector to the other connector.
Different data source systems and data sink systems can be connected to a connector.
It is also important that this is a decentralized approach, there is no large data lake across a data space in which the data is stored, but rather it remains on the data provider's side and is transferred directly to a consumer in their target system.
Communication between the connectors happens with standardized messages according to a specified protocol so that different connectors can talk to each other.
A data space can consist of several connectors from different companies, so that they all form the data space.
Identity providers exist as an additional component to ensure that such a data space only contains connectors from companies that should also be part of the data space.
Regardless of the identity provider's specific technology, a connector is issued tokens by an identity provider containing certain information about the connector and the company behind the connector.
These identity tokens are transmitted from one connector to another connector as part of a message between those connectors.
The connector that receives a message with such a token can therefore understand who the other connector is and validate its identity.
This identity provider can, for example, be managed by a central service-provider of the Data Space that decides which companies and their connectors should be part of the data space.
This could, for example, mean that companies first have to register with this identity provider and are subject to a process there in order to become participant in the data space.
If a connector receives messages without valid proof of identity, it will discard these messages and not process them further.
With an identity provider and connectors we would now have the definition of a minimum data space.
Depending on the requirements of the data space and its purpose, additional components can exist in the data space.
For example, a data space may decide to introduce a central component in which all data offerings in the data space are displayed to a human user like in a kind of shop window.
If a connector does not prohibit it, metadata about its offers will also be displayed in this central component, called broker, in addition to a possible direct query of its data catalog by other connectors.
If there is no central broker, a request about the offers of a providing connector from a consuming connector can be done.
The consuming connector can then decide whether it wants to start the contract negotiation and consume the data or not.
Depending on the use case, there are additional components that either have to be operated as central components or around the connector deployed for a company on its side.

---
icon: file-lines
---

## Key Concepts and their Relationships

This section defines important terms in the context of the EDC and explains their relationships between them.

### 1. Asset

An **Asset** represents any data or resource that can be shared, consumed, or transferred between parties. Assets are at the core of data exchanges within the EDC. They could represent APIs or other different data sources and include the metadata that describes the resource.

**Cardinality**:
- An asset can be associated with multiple policies and contract definitions / data offers.

### 2. Policy

A **Policy** governs the usage of an asset by defining the conditions or restrictions. Policies ensure that access control rules, privacy considerations, and compliance requirements are respected during catalog crawling and data exchange.

**Cardinality**:
- The same policy can be applied to multiple assets within a contract definition/data offer.

### 3. Contract Definition / Data Offer

A **Contract Definition**, also referred to as a **Data Offer**, formalizes the terms and conditions of catalog crawling, negotiating for an asset and transferring the actual data. It includes the applicable policies and identifies the specific assets available for access.

**Related Concepts**:
- **Asset**: The contract definition specifies which assets are available to be crawled and negotiated for.
- **Policy**: Access Policy: Defines the constraints under which the asset may be seen when crawling the catalog. Contract Policy: Defines the constraints under which a contract negotiation can end successful and under which a data transfer can be initiated.
- **Contract Negotiation**: The negotiation process determines if both parties agree to the terms outlined in the contract definitions contract policy.

**Cardinality**:
- A contract definition can cover multiple assets and policies as access policy and contract policy.
- A contract definition may be offered to multiple consumers depending on the access policy chosen.

### 4. Catalog

The **Catalog** is a collection of assets that a provider offers for sharing to a specific consumer requesting the catalog. It serves as a directory where consumers can browse available data offers.

**Related Concepts**:
- **Contract Definition / Data Offer**: Each entry in a catalog corresponds to a specific data offer, that the specific requesting consumer is allowed to see depending on the access policy of the data offer.
- **Asset**: The catalog lists assets that are part of the data offer.

**Cardinality**:
- A catalog can contain many contract definitions / data offers.

### 5. Contract Negotiation

**Contract Negotiation** is the process through which two parties agree on the terms for concluding a contract. The negotiation involves validating contract policies to ensure mutual agreement.

**Related Concepts**:
- **Contract Definition**: The focus of the negotiation, determining how assets can be accessed by a contract policy set in the contract definition/data offer.
- **Data Transfer**: Once a contract is agreed upon, data transfer can be initiated.

**Cardinality**:
- A negotiation involves one contract definition/data offer at a time.
- Negotiation results in either a successful agreement or a failure, if for example the contract policy was not fulfilled by the consumer.

### 6. Data Transfer

**Data Transfer** refers to the actual movement of data between parties after a successful contract negotiation. Before the transfer is started, the contract policy is re-evaluated in this process. In this process, the consumer must also specify where the data is to be transferred to. 

**Related Concepts**:
- **Contract Definition/Data Offer**: Governs the terms under which the data transfer occurs by its contract policy.
- **Contract Negotiation**: The agreement reached through negotiation enables data transfer.

**Cardinality**:
- A single data transfer follows from one contract agreement.
- Multiple transfers can occur from one contract.

### 7. Contract Termination

**Contract Termination** marks the end of the data sharing relationship as defined in the contract agreement. Termination can occur because either party may terminate the contract agreement at any given time.

**Related Concepts**:
- **Data Transfer**: Data transfer ceases after the contract is terminated.

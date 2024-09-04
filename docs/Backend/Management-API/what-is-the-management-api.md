---
icon: gear-complex-code
---

## Management-API

Our CaaS is mostly controlled by our users via the UI, i.e., assets are created, policies, contract definitions, and contracts are negotiated, and data transfers are initiated. In addition to the UI, there are also APIs that can be used to operate the connector.

### Endpoints

The `{{Management-API}}` specifies the URL of the Management-API, and the prefixes `v2` and `v3` indicate that the endpoints are currently versioned independently of each other.

| Resource            | Endpoint                                      |
|---------------------|-----------------------------------------------|
| **Asset**           | `{{Management-API}}/v2/assets` or `{{Management-API}}/v3/assets` |
| **Policy Definition** | `{{Management-API}}/v2/policydefinitions`    |
| **Contract Definition** | `{{Management-API}}/v2/contractdefinitions` |
| **Catalog**         | `{{Management-API}}/v2/catalog`               |
| **Contract Negotiation** | `{{Management-API}}/v2/contractnegotiations` |
| **Contract Agreement** | `{{Management-API}}/v2/contractagreements`   |
| **Transfer Process** | `{{Management-API}}/v2/transferprocesses`     |

### Brief JSON-LD Introduction

The EDC implements the Dataspace Protocol (DSP), as specified by the IDSA. As the DSP uses JSON-LD for all payloads, the Management-API reflects this as well, even though it is not a part of the DSP. JSON-LD (JSON for Linked Data) is an extension of JSON that introduces a set of principles and mechanisms to serialize RDF-graphs, thus opening new opportunities for interoperability. As such, there is a clear separation into identifiable resources (IRIs) and literals holding primitive data like strings or integers. For developers used to working with JSON, JSON-LD can behave in unexpected ways; for example, a list with one entry will always unwrap to an object, which may cause schema validation to fail on the client side.

### Keywords

JSON-LD includes several important keywords that play a crucial role in defining the structure, semantics, and relationships within a JSON-LD document. Since some keys required in requests for the new management API aren't self-explanatory when you first see them, here are some of the most commonly used and important keywords in JSON-LD. These keys are generally part of the JSON-LD spec and serve as identification on a larger scope.

| Key         | Description                                                                                                                                                       |
|-------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **@context** | Specifies the context for interpreting the meaning of terms and properties within a JSON-LD document. It associates terms with namespaces, vocabularies, or URLs.  |
| **@vocab**   | Sets a default namespace or vocabulary for expanding terms within a JSON-LD document. It allows for a more concise representation of properties by omitting the namespace prefix for commonly used terms. |
| **@id**      | Represents the unique identifier (URI or IRI) for a node or resource within a JSON-LD document. It allows for linking and referencing resources.                  |
| **@type**    | Indicates the type(s) of a node or resource. It is used to specify the class or classes that the resource belongs to, typically using terms from a vocabulary or ontology. |

### Namespaces

A namespace is defined by associating a prefix with a URI or IRI in the @context of a JSON-LD document. The prefix is typically a short string, while the URI or IRI represents a namespace or vocabulary where the terms or properties are defined. Some namespaces are known to the EDC internally. This means the EDC will resolve all resources to non-prefixed IRIs unless they are part of the following list:

| Key   | Description                                                                                                                                                   |
|-------|---------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **dct**   | Defines the prefix "dct" and associates it with the URI `https://purl.org/dc/terms/`. The prefix "dct" can now be used in the JSON-LD document to represent terms from the Dublin Core Metadata Terms vocabulary. |
| **edc**   | Defines the prefix "edc" and associates it with the URI `https://w3id.org/edc/v0.0.1/ns/`. The prefix "edc" can now be used to represent terms from the EDC (Eclipse Dataspace Connect) vocabulary. |
| **dcat**  | Defines the prefix "dcat" and associates it with the URI `https://www.w3.org/ns/dcat/`. The prefix "dcat" can now be used to represent terms from the DCAT (Data Catalog Vocabulary) vocabulary.       |
| **odrl**  | Defines the prefix "odrl" and associates it with the URI `http://www.w3.org/ns/odrl/2/`. The prefix "odrl" can now be used to represent terms from the ODRL (Open Digital Rights Language) vocabulary.  |
| **dspace**| Defines the prefix "dspace" and associates it with the URI `https://w3id.org/dspace/v0.8/`. The prefix "dspace" can now be used to represent terms from the DSpace vocabulary.                       |

### Notice

Original source of documentation: [Management API Walkthrough](https://github.com/eclipse-tractusx/tractusx-edc/blob/ea38686f449651a833fb62e1733e0424a2f9b224/docs/usage/management-api-walkthrough/README.md), the original documentation is licensed under [Creative Commons License (CC BY 4.0)](https://creativecommons.org/licenses/by/4.0/legalcode). Minor adjustments to the text have been made.

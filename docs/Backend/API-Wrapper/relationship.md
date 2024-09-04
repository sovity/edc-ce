---
icon: gear-complex-code
---

## Relationship and Difference Between API-Wrapper and Management-API

### Introduction

In the EDC ecosystem, two critical components help manage and interact with the EDC backend and manage its data and processes: the Management-API and the API-Wrapper. Understanding their relationship and differences is essential for effective usage and integration.

### Management-API

The Management-API is a core component of the EDC, providing direct access to the internal functionalities of the EDC. It allows for comprehensive control over the metadata, data transfers, and other tasks within the EDC. This API is robust and offers detailed access to various features of the EDC, making it suitable for complex operations and in-depth management.

**Key Characteristics:**
- Provides detailed control over EDC configurations and data processes.
- Designed for deep integration with EDC's core functionalities.
- Suitable for users who need full access to all features with detailed capabilities.
- Standard API for all connectors based on the EDC.

### API-Wrapper

The API-Wrapper, introduced in the sovity EDC Community Edition, serves as a higher-level abstraction over the Management-API. It simplifies interaction with the EDC backend by providing a more user-friendly and stable interface. The API-Wrapper aims to streamline common tasks and reduce the complexity of direct Management-API interactions, making it easier for developers to integrate EDC functionalities into their applications.

**Key Characteristics:**
- Simplifies interaction with EDC, making it more accessible via API.
- Offers stable and simplified API endpoints, reducing the learning curve.
- Ideal for quick integration and use in common scenarios without needing in-depth EDC knowledge.
- The UI of our EDC CE is based on the API-Wrapper.

### Relationship and Differences

While both the Management-API and the API-Wrapper allow interaction with the EDC, they cater to different needs and user expertise levels. The Management-API is for advanced users who need complete control over the EDC's capabilities. In contrast, the API-Wrapper is tailored for users who require a more straightforward and less detailed interface, enabling quicker and easier integration.

**In Summary:**
- The **Management-API** provides comprehensive and detailed access to EDC functionalities, suitable for users needing full control.
- The **API-Wrapper** offers a simplified, user-friendly interface that abstracts the complexity, ideal for developers looking to integrate EDC capabilities with minimal effort.

Both APIs are available in our Postman collection and can be used for our CaaS.
{% hint style="info" %} 
Link: <a href="https://github.com/sovity/edc-ce/blob/main/docs/api/postman_collection.json">sovity Postman collection</a>
{% endhint %}

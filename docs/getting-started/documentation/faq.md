Welcome to our Frequently Asked Questions (FAQ) collection!
========

This section is designed to provide quick and clear answers to the most common queries we receive. Whether you're looking for information on our products, services, policies, or need help with troubleshooting, you'll find the answers here.
If you can't find the answer to your question, don't hesitate to open a [dicussion](https://github.com/sovity/edc-ce/discussions).

### How does the EDC API-key work for backend and frontend for API-authentication?

**Backend (EDC):**
The variable ```EDC_API_AUTH_KEY``` in the EDC backend is used to define the API-key for the Management-API and API-Wrapper in the first place. External requests from external services to the EDC backend and its Management-API/API-Wrapper are then authenticated against this value and the requests must contain this API-key accordingly.
 
**Frontend (EDC UI):**
The frontend is such an external service whose API requests to the Management-API/API-Wrapper of the EDC backend must be authenticated like any other API request, hence the variable ```EDC_UI_MANAGEMENT_API_KEY``` in the EDC UI. It therefore tells the UI which API-key it should use for its own requests to the EDC backend in order to be successfully authenticated there, so that the UI can query and display data from the EDC backend or create assets etc.

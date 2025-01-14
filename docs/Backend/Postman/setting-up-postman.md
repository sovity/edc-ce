---
icon: rectangle-history
---

# Basics: Setting Up Postman

To access the APIs and to install the API environment in Postman on Windows or Linux, please follow these steps.

## 1. Installing Postman on Linux/Windows

To access the backend APIs, you first need to install Postman on your Linux/Windows machine. Follow these steps:

- Visit the [Postman Download page](https://www.postman.com/downloads/)
- The version suitable for the operating system is offered for download directly
- Download the suitable version of Postman
- Install Postman using the standard procedure for installing software in your company
  - **Linux:** Launch Postman by typing ```postman``` in the terminal, if that is the usual method for starting applications on your system
  - **Windows:** Double-click on the new Postman icon on the desktop or launch the application directly via the Windows menu

## 2. Setting Up the API Environment in Postman

Once Postman is installed and running, set up the API environment as follows:

- Click on ```Import``` in the top left corner of Postman
- Select the downloaded Postman Collection JSON file or directly input the URL of our Postman Collection
- Follow the prompts to complete the import process

{% hint style="info" %} 
Direct Link to Postman collection: <a href="https://raw.githubusercontent.com/sovity/edc-ce/main/docs/api/postman_collection.json">https://raw.githubusercontent.com/sovity/edc-ce/main/docs/api/postman_collection.json</a>
{% endhint %}


## 3. Executing API Requests

After importing the Postman Collection, you can proceed with executing API requests:

- The imported collection will display prepared API requests under the folders for the API-Wrapper and the Management-API
- The API requests require specifying the API base-path, which depends on your specific connector. This base-path can easily be found in the CaaS UI.
- Authentication against the Management-API/API-Wrapper of your Connector is necessary to execute API calls:
  - **On-Premises**: This depends on your on-premises Connector setup. By default setup, an API-key is used and required, which can be found and customized in the Connector configuration. We always recommend setting up a real IAM solution here instead for productive usage.   
  - **CaaS**: Detailed instructions on how to authenticate can be found under the ```CaaS Usage``` section in the Customer Documentation
- The APIs of the API-Wrapper or the Management-API available in the collection can then be used to control the connector via API calls

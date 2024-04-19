How to share parameterized HTTP data sources to expose entire APIs using our UI
========

This guide will help you understand how to provide and consume parameterized HTTP data offers.

## What are Parameterized Assets?

Assets with Parameterized HTTP Data Sources are asset where you can change parts of the HTTP Request, e.g. choose a
different method, change the path, add query params or provide a custom request body.

## Settings You Can Change
![parameterized-asset.png](screenshots/parameterized-asset.png)

When you're choosing a HTTP Data asset in the user interface (UI), there are options you can turn on or off to let you
change certain parts of the HTTP request. These are called "overridable" fields because you can change, or "override,"
the default settings. Here's what each option does:

### Method Overridability:
When you're using an asset, and you've enabled "Method Overridability," you're required to specify a "Custom HTTP Method."

A "method" is a type of request you make to a server. Common examples include GET (asking a server to show you a webpage), POST (sending information to a server, like filling out a form), PUT (updating information on a server), and DELETE (asking a server to remove information). So, when "Method Overridability" is enabled, you need to tell the system which of these types of requests to make to the server. This specified request type is your "Custom HTTP Method."

### Path Overridability:
When "Path Overridability" is turned on, you have the ability to append additional paths to the URL specified by the provider. The path is the part of the URL that comes after the domain name. However, keep in mind that you're not replacing the entire path provided by the URL, but just adding to it at the end.

### Query Parameter Overridability:
When "Query Parameter Overridability" is enabled, you can add or modify parameters in your HTTP request's URL. This is particularly useful when you need to customize your request. Simply put, the system will merge the default parameters of the HTTP Data asset and the ones you provide. In case of any conflicts, your parameters will have the upper hand. For example, if the default parameter is 'color=blue' and you provide 'color=red', your 'color' preference 'red' will be used.

### Request Body Overridability:
When "Request Body Overridability" is enabled, you can alter the data you're sending to the server in the request body. This comes in handy when you're using methods like POST or PUT to send new data or update existing ones. If this feature is turned on, you can replace the default request body data, that comes with the HTTP Data asset, with your own. However, if it's not turned on, the system will use the default request body and you won't be able to modify it.

#### Rules to Follow

- `GET` **can't** have a request body.
- `POST`, `PUT`, `PATCH`, (and WebDAV's `PROPPATCH`, `REPORT`),  **must** have a request body.
  - When sending a body, the *Custom request body content type* **must** also be set, otherwise nothing will be sent.
- The other methods may or may not have a body.
- If you break these rules or forget to choose a method, the process will finish, but no data will be sent.
- `HEAD` is not supported.

# Understanding Parameter Validation and Its Limitations

When you're using our HTTP Data asset with parameterization enabled, it's important to understand how parameter validation works and the limitations you might encounter.

## Invalid Parameters

The HTTP Data asset validates parameters when making a request. If parameters are invalid, a notable issue is that the system reports a complete data transfer even when no data has been exchanged. For example, a GET request can never have a request body, and a PUT / PATCH request requires a request body. This happens because the validation checks parameters at the data plane stage, not at the control plane stage.

## Missing Method

If you don't provide a method for your request, even though you've turned on method overridability, your request can't be completed correctly. The system might not catch this issue because of the validation limitations mentioned above.

## Asset Properties

The parameterization process adds metadata to the asset that lets you know what kind of parameterization is enabled for the HTTP Data asset. However, due to an issue with asset metadata not getting persisted for consuming contract agreements, the available / required options aren't shown when initiating a transfer. Thus, currently, this information needs to be noted down manually from the catalog.


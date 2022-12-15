# EDC-Connector Broker Extension

To get started, a sample docker-compose file is located in the resources/docs folder.

To be able to start an EDC-Connector with the broker-extensions, the `AKI` and `SKI` of the connector certificate must be entered as `client-ID` in the docker-compose and the .jks must be placed under the path specified in the docker-compose (in the example in the folder `resources/vault/edc/`, see `EDC_KEYSTORE` setting).

### What should the client ID entry look like?
Example of a client-ID entry:

`EDC_OAUTH_CLIENT_ID: 7X:7Y:...:B2:94:keyid:6A:2B:...:28:80`

### How do you get the AKI and SKI of a p12 and how do you convert it to a jks?

Here we show the way via the tool `KeyStore Explorer` using Windows, for direct commands see other examples online.

1. Convert the `p12` to a `jks`
- Open the `p12` in the Explorer by entering the password
- `File` -> `Save as` -> Select `KeyStore Files` at `Files of Type` -> Directly save the file again as file of type `KeyStore Files` with a `.jks` extension in its name under `File Name`, e.g. `test.jks`
2. Get AKI and SKI
- Right click on the KeyPair -> `Export` -> `Export Certificate Chain` -> Confirm the dialog with `Export` Button
- Double-click on the certificate under the specified storage path
- Under the `Details` section you will find the details of the AKI and SKI of both the applicant and the issuer
- Finally, both specifications have to be manually rewritten into the required format, see the above example of the client-ID

## License

This project is licensed under the Apache License 2.0 - see [here](LICENSE) for details.

import { EdcClientOptions } from "./EdcClientOptions";
import { Configuration, ExampleApi, UseCaseApi } from "./generated";

/**
 * Configure & Build new EDC Client
 * @param opts opts
 */
export function buildEdcClient(opts: EdcClientOptions) {
  const config = buildConfiguration(opts);
  return {
    exampleApi: new ExampleApi(config),
    useCaseApi: new UseCaseApi(config),
  };
}

function buildConfiguration(opts: EdcClientOptions): Configuration {
  return new Configuration({
    basePath: opts.managementApiUrl,
    headers: {
      "x-api-key": opts.managementApiKey ?? "ApiKeyDefaultValue",
    },
    credentials: "include",
  });
}

import { ExampleApi, UseCaseApi } from "./generated";

/**
 * API Client for our sovity EDC
 */
export interface EdcClient {
  exampleApi: ExampleApi;
  useCaseApi: UseCaseApi;
}

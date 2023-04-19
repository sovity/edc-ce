import {ExampleApi, UIApi, UseCaseApi} from "./generated";

/**
 * API Client for our sovity EDC
 */
export interface EdcClient {
  exampleApi: ExampleApi;
  uiApi: UIApi;
  useCaseApi: UseCaseApi;
}

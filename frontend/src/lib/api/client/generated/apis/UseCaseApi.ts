/*
 * Copyright 2025 sovity GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 */
/* eslint-disable */
/**
 * sovity EDC API Wrapper
 * sovity\'s EDC API Wrapper contains a selection of APIs for multiple consumers, e.g. our EDC UI API, our generic Use Case API, our Commercial Edition APIs, etc. We bundled these APIs, so we can have an easier time generating our API Client Libraries.
 *
 * The version of the OpenAPI document: 0.0.0
 * Contact: contact@sovity.de
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


import * as runtime from '../runtime';
import type {
  CatalogQuery,
  CatalogQueryV2,
  ContractNegotiationStateResult,
  EdrDto,
  IdResponseDto,
  KpiResult,
  NegotiateAllQuery,
  NegotiateAllResult,
  TransferProcessStateResult,
  UiDataOffer,
} from '../models/index';
import {
    CatalogQueryFromJSON,
    CatalogQueryToJSON,
    CatalogQueryV2FromJSON,
    CatalogQueryV2ToJSON,
    ContractNegotiationStateResultFromJSON,
    ContractNegotiationStateResultToJSON,
    EdrDtoFromJSON,
    EdrDtoToJSON,
    IdResponseDtoFromJSON,
    IdResponseDtoToJSON,
    KpiResultFromJSON,
    KpiResultToJSON,
    NegotiateAllQueryFromJSON,
    NegotiateAllQueryToJSON,
    NegotiateAllResultFromJSON,
    NegotiateAllResultToJSON,
    TransferProcessStateResultFromJSON,
    TransferProcessStateResultToJSON,
    UiDataOfferFromJSON,
    UiDataOfferToJSON,
} from '../models/index';

export interface GetContractNegotiationStatesRequest {
    requestBody: Array<string>;
}

export interface GetTransferProcessEdrRequest {
    transferId: string;
}

export interface GetTransferProcessStatesRequest {
    requestBody: Array<string>;
}

export interface NegotiateAllRequest {
    negotiateAllQuery: NegotiateAllQuery;
}

export interface QueryCatalogRequest {
    catalogQuery: CatalogQuery;
}

export interface QueryCatalogV2Request {
    catalogQueryV2: CatalogQueryV2;
}

export interface TerminateTransferProcessRequest {
    transferId: string;
}

/**
 * 
 */
export class UseCaseApi extends runtime.BaseAPI {

    /**
     * Fetch contract negotiation states as batch
     */
    async getContractNegotiationStatesRaw(requestParameters: GetContractNegotiationStatesRequest, initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<runtime.ApiResponse<Array<ContractNegotiationStateResult>>> {
        if (requestParameters['requestBody'] == null) {
            throw new runtime.RequiredError(
                'requestBody',
                'Required parameter "requestBody" was null or undefined when calling getContractNegotiationStates().'
            );
        }

        const queryParameters: any = {};

        const headerParameters: runtime.HTTPHeaders = {};

        headerParameters['Content-Type'] = 'application/json';

        const response = await this.request({
            path: `/wrapper/use-case-api/contract-negotiations/states`,
            method: 'POST',
            headers: headerParameters,
            query: queryParameters,
            body: requestParameters['requestBody'],
        }, initOverrides);

        return new runtime.JSONApiResponse(response, (jsonValue) => jsonValue.map(ContractNegotiationStateResultFromJSON));
    }

    /**
     * Fetch contract negotiation states as batch
     */
    async getContractNegotiationStates(requestParameters: GetContractNegotiationStatesRequest, initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<Array<ContractNegotiationStateResult>> {
        const response = await this.getContractNegotiationStatesRaw(requestParameters, initOverrides);
        return await response.value();
    }

    /**
     * Basic KPIs about the running EDC Connector.
     */
    async getKpisRaw(initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<runtime.ApiResponse<KpiResult>> {
        const queryParameters: any = {};

        const headerParameters: runtime.HTTPHeaders = {};

        const response = await this.request({
            path: `/wrapper/use-case-api/kpis`,
            method: 'GET',
            headers: headerParameters,
            query: queryParameters,
        }, initOverrides);

        return new runtime.JSONApiResponse(response, (jsonValue) => KpiResultFromJSON(jsonValue));
    }

    /**
     * Basic KPIs about the running EDC Connector.
     */
    async getKpis(initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<KpiResult> {
        const response = await this.getKpisRaw(initOverrides);
        return await response.value();
    }

    /**
     * List available functions in policies, prohibitions and obligations.
     */
    async getSupportedFunctionsRaw(initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<runtime.ApiResponse<Array<string>>> {
        const queryParameters: any = {};

        const headerParameters: runtime.HTTPHeaders = {};

        const response = await this.request({
            path: `/wrapper/use-case-api/supported-policy-functions`,
            method: 'GET',
            headers: headerParameters,
            query: queryParameters,
        }, initOverrides);

        return new runtime.JSONApiResponse<any>(response);
    }

    /**
     * List available functions in policies, prohibitions and obligations.
     */
    async getSupportedFunctions(initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<Array<string>> {
        const response = await this.getSupportedFunctionsRaw(initOverrides);
        return await response.value();
    }

    /**
     * Fetch the EDR for a given transfer process. Refreshes EDR if expired. The transfer must have been started as HttpData-PROXY.
     */
    async getTransferProcessEdrRaw(requestParameters: GetTransferProcessEdrRequest, initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<runtime.ApiResponse<EdrDto>> {
        if (requestParameters['transferId'] == null) {
            throw new runtime.RequiredError(
                'transferId',
                'Required parameter "transferId" was null or undefined when calling getTransferProcessEdr().'
            );
        }

        const queryParameters: any = {};

        const headerParameters: runtime.HTTPHeaders = {};

        const response = await this.request({
            path: `/wrapper/use-case-api/transfers/{transferId}/edr`.replace(`{${"transferId"}}`, encodeURIComponent(String(requestParameters['transferId']))),
            method: 'POST',
            headers: headerParameters,
            query: queryParameters,
        }, initOverrides);

        return new runtime.JSONApiResponse(response, (jsonValue) => EdrDtoFromJSON(jsonValue));
    }

    /**
     * Fetch the EDR for a given transfer process. Refreshes EDR if expired. The transfer must have been started as HttpData-PROXY.
     */
    async getTransferProcessEdr(requestParameters: GetTransferProcessEdrRequest, initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<EdrDto> {
        const response = await this.getTransferProcessEdrRaw(requestParameters, initOverrides);
        return await response.value();
    }

    /**
     * Fetch transfer process states as batch
     */
    async getTransferProcessStatesRaw(requestParameters: GetTransferProcessStatesRequest, initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<runtime.ApiResponse<Array<TransferProcessStateResult>>> {
        if (requestParameters['requestBody'] == null) {
            throw new runtime.RequiredError(
                'requestBody',
                'Required parameter "requestBody" was null or undefined when calling getTransferProcessStates().'
            );
        }

        const queryParameters: any = {};

        const headerParameters: runtime.HTTPHeaders = {};

        headerParameters['Content-Type'] = 'application/json';

        const response = await this.request({
            path: `/wrapper/use-case-api/transfers/states`,
            method: 'POST',
            headers: headerParameters,
            query: queryParameters,
            body: requestParameters['requestBody'],
        }, initOverrides);

        return new runtime.JSONApiResponse(response, (jsonValue) => jsonValue.map(TransferProcessStateResultFromJSON));
    }

    /**
     * Fetch transfer process states as batch
     */
    async getTransferProcessStates(requestParameters: GetTransferProcessStatesRequest, initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<Array<TransferProcessStateResult>> {
        const response = await this.getTransferProcessStatesRaw(requestParameters, initOverrides);
        return await response.value();
    }

    /**
     * Negotiate all assets with the given nested asset property filter. Only negotiates assets without active agreements. Returns existing agreements if found.
     */
    async negotiateAllRaw(requestParameters: NegotiateAllRequest, initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<runtime.ApiResponse<Array<NegotiateAllResult>>> {
        if (requestParameters['negotiateAllQuery'] == null) {
            throw new runtime.RequiredError(
                'negotiateAllQuery',
                'Required parameter "negotiateAllQuery" was null or undefined when calling negotiateAll().'
            );
        }

        const queryParameters: any = {};

        const headerParameters: runtime.HTTPHeaders = {};

        headerParameters['Content-Type'] = 'application/json';

        const response = await this.request({
            path: `/wrapper/use-case-api/negotiations/quick-initiate`,
            method: 'POST',
            headers: headerParameters,
            query: queryParameters,
            body: NegotiateAllQueryToJSON(requestParameters['negotiateAllQuery']),
        }, initOverrides);

        return new runtime.JSONApiResponse(response, (jsonValue) => jsonValue.map(NegotiateAllResultFromJSON));
    }

    /**
     * Negotiate all assets with the given nested asset property filter. Only negotiates assets without active agreements. Returns existing agreements if found.
     */
    async negotiateAll(requestParameters: NegotiateAllRequest, initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<Array<NegotiateAllResult>> {
        const response = await this.negotiateAllRaw(requestParameters, initOverrides);
        return await response.value();
    }

    /**
     * Fetch a connector\'s data offers
     * @deprecated
     */
    async queryCatalogRaw(requestParameters: QueryCatalogRequest, initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<runtime.ApiResponse<Array<UiDataOffer>>> {
        if (requestParameters['catalogQuery'] == null) {
            throw new runtime.RequiredError(
                'catalogQuery',
                'Required parameter "catalogQuery" was null or undefined when calling queryCatalog().'
            );
        }

        const queryParameters: any = {};

        const headerParameters: runtime.HTTPHeaders = {};

        headerParameters['Content-Type'] = 'application/json';

        const response = await this.request({
            path: `/wrapper/use-case-api/catalog`,
            method: 'POST',
            headers: headerParameters,
            query: queryParameters,
            body: CatalogQueryToJSON(requestParameters['catalogQuery']),
        }, initOverrides);

        return new runtime.JSONApiResponse(response, (jsonValue) => jsonValue.map(UiDataOfferFromJSON));
    }

    /**
     * Fetch a connector\'s data offers
     * @deprecated
     */
    async queryCatalog(requestParameters: QueryCatalogRequest, initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<Array<UiDataOffer>> {
        const response = await this.queryCatalogRaw(requestParameters, initOverrides);
        return await response.value();
    }

    /**
     * Fetch a connector\'s data offers filtering the catalog by nested asset properties
     */
    async queryCatalogV2Raw(requestParameters: QueryCatalogV2Request, initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<runtime.ApiResponse<Array<UiDataOffer>>> {
        if (requestParameters['catalogQueryV2'] == null) {
            throw new runtime.RequiredError(
                'catalogQueryV2',
                'Required parameter "catalogQueryV2" was null or undefined when calling queryCatalogV2().'
            );
        }

        const queryParameters: any = {};

        const headerParameters: runtime.HTTPHeaders = {};

        headerParameters['Content-Type'] = 'application/json';

        const response = await this.request({
            path: `/wrapper/use-case-api/catalog-v2`,
            method: 'POST',
            headers: headerParameters,
            query: queryParameters,
            body: CatalogQueryV2ToJSON(requestParameters['catalogQueryV2']),
        }, initOverrides);

        return new runtime.JSONApiResponse(response, (jsonValue) => jsonValue.map(UiDataOfferFromJSON));
    }

    /**
     * Fetch a connector\'s data offers filtering the catalog by nested asset properties
     */
    async queryCatalogV2(requestParameters: QueryCatalogV2Request, initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<Array<UiDataOffer>> {
        const response = await this.queryCatalogV2Raw(requestParameters, initOverrides);
        return await response.value();
    }

    /**
     * Delete the EDR and terminate the given transfer process.
     */
    async terminateTransferProcessRaw(requestParameters: TerminateTransferProcessRequest, initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<runtime.ApiResponse<IdResponseDto>> {
        if (requestParameters['transferId'] == null) {
            throw new runtime.RequiredError(
                'transferId',
                'Required parameter "transferId" was null or undefined when calling terminateTransferProcess().'
            );
        }

        const queryParameters: any = {};

        const headerParameters: runtime.HTTPHeaders = {};

        const response = await this.request({
            path: `/wrapper/use-case-api/transfers/{transferId}/terminate`.replace(`{${"transferId"}}`, encodeURIComponent(String(requestParameters['transferId']))),
            method: 'POST',
            headers: headerParameters,
            query: queryParameters,
        }, initOverrides);

        return new runtime.JSONApiResponse(response, (jsonValue) => IdResponseDtoFromJSON(jsonValue));
    }

    /**
     * Delete the EDR and terminate the given transfer process.
     */
    async terminateTransferProcess(requestParameters: TerminateTransferProcessRequest, initOverrides?: RequestInit | runtime.InitOverrideFunction): Promise<IdResponseDto> {
        const response = await this.terminateTransferProcessRaw(requestParameters, initOverrides);
        return await response.value();
    }

}

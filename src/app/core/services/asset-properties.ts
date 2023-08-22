/**
 * Asset Property Names
 *
 * SSOT for asset property string names
 */

const edc = 'https://w3id.org/edc/v0.0.1/ns/';
const sovity = 'https://edc.jsonld.sovity.io/ns/';
const mds = 'http://w3id.org/mds#';

export const AssetProperties = {
  // edc asset properties
  id: `${edc}id`,
  name: `${sovity}name`,
  contentType: `${sovity}contenttype`,
  originator: `${sovity}originator`,
  originatorOrganization: `${sovity}originatorOrganization`,
  description: `${sovity}description`,
  version: `${sovity}version`,

  // our asset properties
  keywords: `${sovity}keywords`,
  curatorOrganizationName: `${sovity}curatorOrganizationName`,
  language: `${sovity}language`,
  publisher: `${sovity}publisher`,
  standardLicense: `${sovity}standardLicense`,
  endpointDocumentation: `${sovity}endpointDocumentation`,

  // mds specific asset properties
  dataCategory: `${mds}dataCategory`,
  dataSubcategory: `${mds}dataSubcategory`,
  dataModel: `${mds}dataModel`,
  geoReferenceMethod: `${mds}geoReferenceMethod`,
  transportMode: `${mds}transportMode`,

  /**
   * Whether this asset supports HTTP Method parameterization
   *
   * Example values: "true", "false"
   */
  httpProxyMethod: `${sovity}datasource:http:hints:proxyMethod`,

  /**
   * Whether this asset supports HTTP Path parameterization
   *
   * Example values: "true", "false"
   */
  httpProxyPath: `${sovity}datasource:http:hints:proxyPath`,

  /**
   * Whether this asset supports HTTP Query Param parameterization
   *
   * Example values: "true", "false"
   */
  httpProxyQueryParams: `${sovity}datasource:http:hints:proxyQueryParams`,

  /**
   * Whether this asset supports HTTP Body parameterization
   *
   * Example values: "true", "false"
   */
  httpProxyBody: `${sovity}datasource:http:hints:proxyBody`,
};

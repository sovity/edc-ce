/**
 * Asset Property Names
 *
 * SSOT for asset property string names
 */

export const AssetProperties = {
  // edc asset properties
  id: 'asset:prop:id', // needs to start with urn:artifact:
  name: 'asset:prop:name',
  contentType: 'asset:prop:contenttype',
  originator: 'asset:prop:originator',
  originatorOrganization: 'asset:prop:originatorOrganization',
  description: 'asset:prop:description',
  version: 'asset:prop:version',

  // our asset properties
  keywords: 'asset:prop:keywords',
  curatorOrganizationName: 'asset:prop:curatorOrganizationName',
  language: 'asset:prop:language',
  publisher: 'asset:prop:publisher',
  standardLicense: 'asset:prop:standardLicense',
  endpointDocumentation: 'asset:prop:endpointDocumentation',

  // mds specific asset properties
  dataCategory: 'http://w3id.org/mds#dataCategory',
  dataSubcategory: 'http://w3id.org/mds#dataSubcategory',
  dataModel: 'http://w3id.org/mds#dataModel',
  geoReferenceMethod: 'http://w3id.org/mds#geoReferenceMethod',
  transportMode: 'http://w3id.org/mds#transportMode',

  /**
   * Whether this asset supports HTTP Method parameterization
   *
   * Example values: "true", "false"
   */
  httpProxyMethod: 'asset:prop:datasource:http:hints:proxyMethod',

  /**
   * Whether this asset supports HTTP Path parameterization
   *
   * Example values: "true", "false"
   */
  httpProxyPath: 'asset:prop:datasource:http:hints:proxyPath',

  /**
   * Whether this asset supports HTTP Query Param parameterization
   *
   * Example values: "true", "false"
   */
  httpProxyQueryParams: 'asset:prop:datasource:http:hints:proxyQueryParams',

  /**
   * Whether this asset supports HTTP Body parameterization
   *
   * Example values: "true", "false"
   */
  httpProxyBody: 'asset:prop:datasource:http:hints:proxyBody',

  /**
   * If this asset supports HTTP Method parameterization, this is the default method
   *
   * Example values: "GET", "POST", "PUT", "DELETE"
   */
  httpDefaultMethod: 'asset:prop:datasource:http:hints:defaultMethod',

  /**
   * If this asset supports HTTP Path parameterization, this is the default path (appended after base path)
   *
   * Example values: /my-endpoint
   */
  httpDefaultPath: 'asset:prop:datasource:http:hints:defaultPath',

  /**
   * @deprecated use {@link AssetProperties.curatorOrganizationName} instead
   */
  _legacyCuratorOrganizationName: 'asset:prop:originatorOrganization',
};

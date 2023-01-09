/**
 * Asset Properties
 *
 * Collection of known properties for easier mapping / access
 */
export const AssetProperties = {
  // Properties try to be the same as IDS asset properties,
  // but IDS API won't be flat and will have a different structure
  // making a mapping necessary

  // IDS id, needs to be URL
  assetIdIds: "ids:id",

  // EDCC ID, needs to start with urn:artifact:
  assetIdEdcc: "edcc:id",

  name: "ids:name",
  version: "ids:version",
  contentType: "ids:mediaType",
  keywords: "ids:keyword",
  originator: "ids:originator", // guessed

  description: "ids:description", // guessed
  language: 'ids:language',
  publisher: "ids:publisher",
  standardLicense: "ids:standardLicense",
  endpointDocumentation: "ids:endpointDocumentation",

  // Custom fields (MDS Specific)
  dataCategory: 'http://w3id.org/mds#dataCategory',
  dataSubcategory: 'http://w3id.org/mds#dataSubcategory',
  dataModel: 'http://w3id.org/mds#dataModel', // guessed
  geoReferenceMethod: 'http://w3id.org/mds#geoReferenceMethod', // guessed
  transportMode: 'http://w3id.org/mds#transportMode',
}

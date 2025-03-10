/*
 * Copyright sovity GmbH and/or licensed to sovity GmbH under one or
 * more contributor license agreements. You may not use this file except
 * in compliance with the "Elastic License 2.0".
 *
 * SPDX-License-Identifier: Elastic-2.0
 */
/**
 * Common Properties between Asset Create and Edit Request
 */
export interface AssetRequestCommonMetadata {
  /**
   * Asset Title
   */
  title: string | undefined;

  /**
   * Asset Language
   */
  language: string | undefined;

  /**
   * Asset Description
   */
  description: string | undefined;

  /**
   * Asset Homepage
   */
  publisherHomepage: string | undefined;

  /**
   * License URL
   */
  licenseUrl: string | undefined;

  /**
   * Version
   */
  version: string | undefined;

  /**
   * Asset Keywords
   */
  keywords: Array<string> | undefined;

  /**
   * Asset MediaType
   */
  mediaType: string | undefined;

  /**
   * Landing Page URL
   */
  landingPageUrl: string | undefined;

  /**
   * Data Category
   */
  dataCategory: string | undefined;

  /**
   * Data Subcategory
   */
  dataSubcategory: string | undefined;

  /**
   * Data Model
   */
  dataModel: string | undefined;

  /**
   * Geo-Reference Method
   */
  geoReferenceMethod: string | undefined;

  /**
   * Transport Mode
   */
  transportMode: string | undefined;

  /**
   * The sovereign is distinct from the publisher by being the legal owner of the data.
   */
  sovereignLegalName: string | undefined;

  /**
   * Geo location
   */
  geoLocation: string | undefined;

  /**
   * Locations by NUTS standard which divides countries into administrative divisions
   */
  nutsLocations: Array<string> | undefined;

  /**
   * Data sample URLs
   */
  dataSampleUrls: Array<string> | undefined;

  /**
   * Reference file/schema URLs
   */
  referenceFileUrls: Array<string> | undefined;

  /**
   * Additional information on reference files/schemas
   */
  referenceFilesDescription: string | undefined;

  /**
   * Instructions for use that are not legally relevant e.g. information on how to cite the dataset in papers
   */
  conditionsForUse: string | undefined;

  /**
   * Data update frequency
   */
  dataUpdateFrequency: string | undefined;

  /**
   * Temporal coverage start date
   */
  temporalCoverageFrom: Date | undefined;

  /**
   * Temporal coverage end date (inclusive)
   */
  temporalCoverageToInclusive: Date | undefined;

  /**
   * Contains serialized custom properties in the JSON format.
   */
  customJsonAsString: string | undefined;

  /**
   * Contains serialized custom properties in the JSON LD format. Contrary to the customJsonAsString field, this string must represent a JSON LD object and will be affected by JSON LD compaction and expansion. Due to a technical limitation, the properties can't be booleans.
   */
  customJsonLdAsString: string | undefined;

  /**
   * Same as customJsonAsString but the data will be stored in the private properties.
   */
  privateCustomJsonAsString: string | undefined;

  /**
   * Same as customJsonLdAsString but the data will be stored in the private properties. The same limitations apply.
   */
  privateCustomJsonLdAsString: string | undefined;
}

import {UiAsset} from '@sovity.de/edc-client';

export namespace TestAssets {
  const markdownDescription = `Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod
  tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At
  vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren,
  no sea takimata sanctus est Lorem ipsum dolor sit amet.

  ![scenery2](https://images.pexels.com/photos/255419/pexels-photo-255419.jpeg?cs=srgb&dl=pexels-pixabay-255419.jpg&fm=jpg)

  Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod
  tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At
  vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren,

  Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod
  tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At
  vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren,

  Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod
  tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At
  vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren,

  Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod
  tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At
  vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren,

 ![scenery](https://images.rawpixel.com/image_800/cHJpdmF0ZS9sci9pbWFnZXMvd2Vic2l0ZS8yMDIyLTA1L3NrOTc5MS1pbWFnZS1rd3Z1amE5Ni5qcGc.jpg) Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren,




  # Omen

  This is **bold!** This is _italic_. This is inline \`code\`.

  > here we quote

  Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod
  tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At
  vero eos et accusam et justo duo dolores et ea rebum.

  ## Sage

  - list item 1
  - list item 2

  ### Raze

  1. list item 1
  2. list item 2

  #### Cypher

  \`\`\`javascript
  alert(1);
  \`\`\`

  ##### Jett

  [Sovity Website](https://sovity.de/)

  **Table**
| Item              | In Stock | Price | Description |
| :---------------- | :------: | ----: | :---------- |
| Python Hat        |   True   | 24.99 | This is a long description to test the scrolling behavior of the table. This is a long description to test the scrolling behavior of the table. |
| SQL Hat           |   True   | 24.99 | This is a long description to test the scrolling behavior of the table. This is a long description to test the scrolling behavior of the table. |
| Codecademy Tee    |  False   | 20.99 | This is a long description to test the scrolling behavior of the table. This is a long description to test the scrolling behavior of the table. |
| Codecademy Hoodie |  False   | 43.99 | This is a long description to test the scrolling behavior of the table. This is a long description to test the scrolling behavior of the table. |
`;

  const shortMarkdownDescription = `# Short Description

This is a short description text that should be fully rendered without being **collapsed**. No *show more* button should be visible.
`;

  export const boring: UiAsset = {
    assetId: 'data-sample-ckd-skd-demands-2023-Jan',
    title: 'data-sample-ckd-skd-demands-2023-Jan',
    description: '',
    descriptionShortText: '',
    connectorEndpoint: 'https://my-other-connector/api/dsp',
    participantId: 'MDSL1234XX.C1234XX',
    creatorOrganizationName: 'my-other-connector',
    temporalCoverageFrom: new Date('2024-01-01'),
    isOwnConnector: true,
  };

  export const short: UiAsset = {
    assetId: 'data-sample-ckd-skd-demands-2023-Feb',
    title: 'data-sample-ckd-skd-demands-2023-Feb',
    connectorEndpoint: 'https://my-other-connector/api/dsp',
    participantId: 'MDSL1234XX.C1235XX',
    creatorOrganizationName: 'my-other-connector',
    description: shortMarkdownDescription,
    descriptionShortText:
      'Short Description This is a short description text that should be fully rendered without being collapsed. No show more button should be visible.',
    isOwnConnector: false,
  };

  export const full: UiAsset = {
    assetId: 'ckd-skd-demands-2023-Jan',
    title: 'CKD / SKD Demands January 2023',
    connectorEndpoint: 'https://my-other-connector/api/dsp',
    participantId: 'MDSL1234XX.C1236XX',
    version: '2023-A-Program',
    creatorOrganizationName: 'My-German-OEM',
    keywords: ['automotive', 'part-demands', '2023', 'January'],
    mediaType: 'application/json',
    description: markdownDescription,
    descriptionShortText:
      'Part demands for CKD/SKD parts January 2023 Split by plant / day / model code. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum.',
    isOwnConnector: true,
    language: 'https://w3id.org/idsa/code/EN',
    publisherHomepage:
      'https://teamabc.departmentxyz.my-german-oem.de/offers/ckd-skd-demands',
    licenseUrl:
      'https://teamabc.departmentxyz.my-german-oem.de/offers/ckd-skd-demands#license',
    landingPageUrl:
      'https://teamabc.departmentxyz.my-german-oem.de/offers/ckd-skd-demands#documentation',
    dataCategory: 'Infrastructure and Logistics',
    dataSubcategory: 'General Information About Planning Of Routes',
    dataModel: 'unspecified',
    geoReferenceMethod: 'Lat/Lon',
    transportMode: 'Rail',
    sovereignLegalName: 'Data Owning Company GmbH',
    geoLocation: '40.741895,-73.989308',
    nutsLocation: ['DE', 'DE9', 'DE92', 'DE929'],
    dataSampleUrls: [
      'https://teamabc.departmentxyz.sample/a',
      'https://teamabc.departmentxyz.sample/b',
      'https://teamabc.departmentxyz.sample/c',
      'https://teamabc.departmentxyz.sample/d',
      'https://teamabc.departmentxyz.sample/e',
    ],
    referenceFileUrls: [
      'https://teamabc.departmentxyz.reference/a',
      'https://teamabc.departmentxyz.reference/b',
      'https://teamabc.departmentxyz.reference/c',
      'https://teamabc.departmentxyz.reference/d',
      'https://teamabc.departmentxyz.reference/e',
      'https://teamabc.departmentxyz.reference/f',
      'https://teamabc.departmentxyz.reference/g',
      'https://teamabc.departmentxyz.reference/h',
      'https://teamabc.departmentxyz.reference/i',
      'https://teamabc.departmentxyz.reference/j',
      'https://teamabc.departmentxyz.reference/k',
      'https://teamabc.departmentxyz.reference/l',
      'https://teamabc.departmentxyz.reference/m',
      'https://teamabc.departmentxyz.reference/n',
      'https://teamabc.departmentxyz.reference/o',
      'https://teamabc.departmentxyz.reference/p',
      'https://teamabc.departmentxyz.reference/q',
      'https://teamabc.departmentxyz.reference/r',
      'https://teamabc.departmentxyz.reference/s',
    ],
    referenceFilesDescription: 'This reference file is important',
    conditionsForUse:
      'If you use the dataset please cite it in your work and give attribution',
    dataUpdateFrequency: 'every month',
    temporalCoverageFrom: new Date('2024-01-01'),
    temporalCoverageToInclusive: new Date('2024-01-24'),
    httpDatasourceHintsProxyQueryParams: true,
    httpDatasourceHintsProxyPath: true,
    httpDatasourceHintsProxyMethod: true,
    httpDatasourceHintsProxyBody: true,
    additionalProperties: {
      'http://unknown/usecase': 'my-use-case',
    },
    privateProperties: {
      'http://unknown/internal-id': 'my-internal-id-123',
    },
  };

  export function toDummyAsset(entry: UiAsset): UiAsset {
    return {
      assetId: entry.assetId,
      title: entry.assetId,
      participantId: entry.participantId,
      connectorEndpoint: entry.connectorEndpoint,
      creatorOrganizationName: entry.participantId,
      isOwnConnector: entry.isOwnConnector,
    };
  }

  export function withSuffix(asset: UiAsset, suffix: string): UiAsset {
    return {
      ...asset,
      assetId: `${asset.assetId}-${suffix}`,
      title: `${asset.title} ${suffix}`,
    };
  }
}

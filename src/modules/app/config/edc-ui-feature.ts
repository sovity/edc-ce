export type EdcUiFeature =
  // Enables MDS Specific Asset Fields such as Data Category, Transport Mode
  | 'mds-fields'

  // Enables support functionalities of connectors commercially hosted by sovity.
  | 'sovity-zammad-integration'

  // Enables logout button to configured LOGOUT_URL
  | 'logout-button'

  // Enables marketing for sovity in open-source variants
  | 'open-source-marketing';

export type EdcUiFeature =
  // Enables MDS Specific Asset Fields such as Data Category, Transport Mode
  | 'mds-fields'

  // Enables MDS Specific Connector ID support
  | 'mds-connector-id'

  // Enables support functionalities of connectors commercially hosted by sovity.
  | 'sovity-zammad-integration'

  // Enables logout button to configured LOGOUT_URL
  | 'logout-button'

  // Enables marketing for sovity in open-source variants
  | 'open-source-marketing'

  // Enterprise Edition specific attribute to view limits enforced on consuming contract agreements
  | 'connector-limits'

  // Enterprise Edition specific flag to enable marketing for other Enterprise Edition variants in basic connectors
  | 'mds-marketing';

export const DATA_SOURCE_HTTP_METHODS = [
  'GET',
  'POST',
  'PUT',
  'PATCH',
  'DELETE',
  'OPTIONS',
  'HEAD',
];
export const DATA_SINK_HTTP_METHODS = DATA_SOURCE_HTTP_METHODS.filter(
  (it) => it !== 'GET',
);

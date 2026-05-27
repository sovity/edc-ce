-- set data plane callback_address to http instead of https
UPDATE edc_data_plane
SET callback_address = REPLACE(callback_address, 'https://', 'http://')
WHERE callback_address LIKE 'https://%';

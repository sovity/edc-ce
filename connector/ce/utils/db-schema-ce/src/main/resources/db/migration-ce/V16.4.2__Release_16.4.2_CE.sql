-- Transition all PULL transfers from RECEIVED (100) to STARTED (150)
update edc_data_plane set state = 150 where state = 100 and flow_type = 'PULL';

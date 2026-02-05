
-- Migrate the existing transfer processes' callback address to avoid global transfer processes stalling
update edc_data_plane set callback_address = 'http://localhost:11004/api/control';

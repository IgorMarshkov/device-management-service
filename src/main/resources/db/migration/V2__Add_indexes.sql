CREATE INDEX idx_device_lower_brand ON device(LOWER(brand));
CREATE INDEX idx_device_state ON device(state);
CREATE INDEX idx_device_creation_time ON device(creation_time);
ALTER TABLE events UPDATE id=generateUUIDv4() WHERE id IS NULL;
CREATE TABLE IF NOT EXISTS users
(
    id UUID DEFAULT generateUUIDv4(),
    email       VARCHAR,
    password       VARCHAR,
    salt       VARCHAR,
    created_at DATETIME('UTC') DEFAULT now()
)
    ENGINE = MergeTree()
        ORDER BY created_at;

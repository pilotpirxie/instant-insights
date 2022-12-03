CREATE TABLE IF NOT EXISTS sessions
(
    id UUID DEFAULT generateUUIDv4(),
    user_id       UUID,
    expires_at       DATETIME('UTC') DEFAULT INTERVAL 1 DAY + NOW(),
    refresh_token VARCHAR,
    ip   VARCHAR,
    created_at DATETIME('UTC') DEFAULT now()
)
    ENGINE = MergeTree()
        ORDER BY created_at;

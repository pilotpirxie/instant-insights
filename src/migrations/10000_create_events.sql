CREATE TABLE IF NOT EXISTS events
(
    type       VARCHAR,
    pathname       VARCHAR,
    fingerprint       VARCHAR,
    meta Map(String, String),
    params Map(String, String),
    created_at DATETIME('UTC') DEFAULT now()
)
    ENGINE = MergeTree()
        ORDER BY created_at;
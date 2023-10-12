CREATE TABLE IF NOT EXISTS link_hits
(
    id UUID DEFAULT generateUUIDv4(),
    name       VARCHAR,
    meta Map(String, String),
    params Map(String, String),
    created_at DATETIME('UTC') DEFAULT now()
)
    ENGINE = MergeTree()
        ORDER BY created_at;
CREATE TABLE IF NOT EXISTS redirect_links
(
    id UUID DEFAULT generateUUIDv4(),
    name       VARCHAR,
    links       Map(String, String),
    created_at DATETIME('UTC') DEFAULT now()
)
    ENGINE = MergeTree()
        ORDER BY created_at;
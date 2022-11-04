CREATE TABLE IF NOT EXISTS insights.apps
(
    id         UUID     DEFAULT generateUUIDv4(),
    name       VARCHAR,
    created_at DATETIME DEFAULT now(),
    updated_at DATETIME DEFAULT now()
)
    ENGINE = MergeTree()
        ORDER BY created_at;


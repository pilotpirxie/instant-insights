CREATE TABLE IF NOT EXISTS insights.events
(
    id         UUID     DEFAULT generateUUIDv4(),
    app_id     UUID,
    type       VARCHAR,
    user Map(String, String),
    params Map(String, String),
    created_at DATETIME DEFAULT now()
)
    ENGINE = MergeTree()
        ORDER BY created_at;
CREATE TABLE IF NOT EXISTS insights.events
(
    app_id     INT,
    type       VARCHAR,
    pathname       VARCHAR,
    meta Map(String, String),
    params Map(String, String),
    created_at DATETIME DEFAULT now()
)
    ENGINE = MergeTree()
        ORDER BY created_at;
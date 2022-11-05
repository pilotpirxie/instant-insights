CREATE TABLE IF NOT EXISTS insights.apps
(
    id         INT,
    name       VARCHAR,
    created_at DATETIME('UTC') DEFAULT now(),
    updated_at DATETIME('UTC') DEFAULT now()
)
    ENGINE = MergeTree()
        ORDER BY created_at;


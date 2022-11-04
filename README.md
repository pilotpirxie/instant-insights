# instant-insights
Track events from different systems

### .env

```shell
CLICKHOUSE_NAME=insights
CLICKHOUSE_URL=http://localhost:8123
CLICKHOUSE_USER=secretuser
CLICKHOUSE_PASS=mysecretpassword
CLICKHOUSE_CONNECT_TIMEOUT=10000
CLICKHOUSE_REQUEST_TIMEOUT=30000
CLICKHOUSE_MAX_OPEN_CONNECTIONS=0
BATCH_INSERT_EVERY=5
DROP_EVENTS_ON_INSERT_ERROR=1
MAX_EVENT_SIZE=1KB
```
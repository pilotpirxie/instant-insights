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
CRON_INSERT_PATTERN='* * * * * *'
DISCARD_EVENTS_ON_INSERT_ERROR=1
MAX_EVENT_SIZE=1KB
ONLINE_TIMESPAN=5
API_WRITE_TOKEN=1a2acdb9-b51e-4658-ab8e-c015a464362b
```
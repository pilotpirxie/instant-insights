CREATE TABLE "users"
(
    "id"                      uuid PRIMARY KEY NOT NULL,
    "email"                   varchar          NOT NULL UNIQUE,
    "password"                varchar          NOT NULL,
    "salt"                    varchar          NOT NULL,
    "email_verification_code" varchar          NOT NULL,
    "email_verified_at"       timestamp,
    "register_ip"             inet             NOT NULL,
    "is_disabled"             boolean          NOT NULL,
    "totp_token"              varchar,
    "created_at"              timestamp        NOT NULL,
    "updated_at"              timestamp        NOT NULL
);

CREATE TABLE "password_recovery"
(
    "id"         uuid PRIMARY KEY NOT NULL,
    "user_id"    uuid             NOT NULL REFERENCES "users" ("id") ON DELETE CASCADE,
    "code"       varchar          NOT NULL,
    "created_at" timestamp        NOT NULL,
    "updated_at" timestamp        NOT NULL
);

CREATE TABLE "sessions"
(
    "id"            uuid PRIMARY KEY NOT NULL,
    "user_id"       uuid             NOT NULL REFERENCES "users" ("id") ON DELETE CASCADE,
    "refresh_token" varchar          NOT NULL,
    "created_at"    timestamp        NOT NULL,
    "updated_at"    timestamp        NOT NULL
);

CREATE TYPE "user_role" AS ENUM ('owner', 'editor', 'viewer');

CREATE TABLE "teams"
(
    "id"         uuid PRIMARY KEY NOT NULL,
    "name"       varchar          NOT NULL,
    "created_at" timestamp        NOT NULL,
    "updated_at" timestamp        NOT NULL
);

CREATE TABLE "users_teams"
(
    "user_id"    uuid REFERENCES "users" ("id") ON DELETE CASCADE,
    "team_id"    uuid REFERENCES "teams" ("id") ON DELETE CASCADE,
    "role"       user_role NOT NULL DEFAULT 'viewer',
    "created_at" timestamp NOT NULL,
    "updated_at" timestamp NOT NULL,
    PRIMARY KEY ("user_id", "team_id")
);

CREATE TABLE "apps"
(
    "id"         uuid PRIMARY KEY NOT NULL,
    "team_id"    uuid             NOT NULL REFERENCES "teams" ("id") ON DELETE CASCADE,
    "name"       varchar          NOT NULL,
    "is_active"  boolean          NOT NULL,
    "created_at" timestamp        NOT NULL,
    "updated_at" timestamp        NOT NULL
);

CREATE TABLE "event_types"
(
    "id"          uuid PRIMARY KEY NOT NULL,
    "app_id"      uuid             NOT NULL REFERENCES "apps" ("id") ON DELETE CASCADE,
    "name"        varchar          NOT NULL,
    "description" text             NOT NULL,
    "schema"      JSONB            NOT NULL,
    "is_active"   boolean          NOT NULL,
    "created_at"  timestamp        NOT NULL,
    "updated_at"  timestamp        NOT NULL
);

CREATE TABLE "events"
(
    "id"         uuid PRIMARY KEY NOT NULL,
    "type_id"    uuid             NOT NULL REFERENCES "event_types" ("id") ON DELETE CASCADE,
    "app_id"     uuid             NOT NULL REFERENCES "apps" ("id") ON DELETE CASCADE,
    "meta"       JSONB            NOT NULL,
    "params"     JSONB            NOT NULL,
    "created_at" timestamp        NOT NULL
);

CREATE TABLE "tracked_links"
(
    "id"             uuid PRIMARY KEY NOT NULL,
    "app_id"         uuid             NOT NULL REFERENCES "apps" ("id") ON DELETE CASCADE,
    "url_slug"       varchar          NOT NULL,
    "is_active"      boolean          NOT NULL,
    "redirect_rules" JSONB            NOT NULL,
    "created_at"     timestamp        NOT NULL,
    "updated_at"     timestamp        NOT NULL
);

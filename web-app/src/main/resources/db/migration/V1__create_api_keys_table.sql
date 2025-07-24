CREATE TABLE api_keys (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_id bigint NOT NULL REFERENCES tenants(id),
    user_id bigint NOT NULL REFERENCES users(id),
    name varchar(255) NOT NULL,
    key_id varchar(32) NOT NULL UNIQUE,
    hashed_secret varchar(255) NOT NULL,
    marked_api_key varchar(255) NOT NULL,
    created_at timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_used_at timestamptz
);

CREATE INDEX idx_api_keys_key_id ON api_keys(key_id);
CREATE INDEX idx_api_keys_user_tenant ON api_keys(user_id, tenant_id);


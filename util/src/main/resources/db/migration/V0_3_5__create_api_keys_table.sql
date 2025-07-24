CREATE TABLE api_keys
(
    id              INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_id       INTEGER      NOT NULL,
    user_id         INTEGER      NOT NULL,
    name            VARCHAR(255) NOT NULL,
    key_id          VARCHAR(32)  NOT NULL UNIQUE,
    hashed_secret   VARCHAR(255) NOT NULL,
    marked_api_key  VARCHAR(255) NOT NULL,
    created_at      TIMESTAMP    NOT NULL,
    updated_at      TIMESTAMP    NOT NULL,
    last_used_at    TIMESTAMP,

    CONSTRAINT fk__api_keys__tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id) ON DELETE CASCADE,
    CONSTRAINT fk__api_keys__user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx__api_keys__key_id ON api_keys (key_id);
CREATE INDEX idx__api_keys__tenant_id ON api_keys (tenant_id);
CREATE INDEX idx__api_keys__user_tenant ON api_keys (user_id, tenant_id); 
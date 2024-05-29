CREATE TABLE IF NOT EXISTS audit_logs (
    id                          UUID            NOT NULL,
    created_timestamp_utc       TIMESTAMP       NOT NULL,
    log_type                    VARCHAR(255)    NOT NULL,
    message                     VARCHAR(255)    NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS urls (
    id                          UUID            NOT NULL,
    long_url                    VARCHAR(2048)   NOT NULL,
    stub                        CHAR(4)         NOT NULL,
    created_timestamp_utc       TIMESTAMP       NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (stub)
);

CREATE TABLE IF NOT EXISTS url_visits (
    id                          UUID            NOT NULL,
    url_id                      UUID            NOT NULL,
    timestamp_utc               TIMESTAMP       NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (url_id) REFERENCES urls(id)
)

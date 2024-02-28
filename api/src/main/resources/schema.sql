CREATE TABLE IF NOT EXISTS users (
    id          UUID            NOT NULL,
    email       VARCHAR(255)    NOT NULL,
    name        VARCHAR(255)    NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS urls (
    id          UUID            NOT NULL,
    long_url    VARCHAR(2048)   NOT NULL,
    stub        CHAR(4)         NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (stub)
);

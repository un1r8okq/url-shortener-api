CREATE TABLE IF NOT EXISTS users (
    id          UUID            NOT NULL,
    email       VARCHAR(255)    NOT NULL,
    name        VARCHAR(255)    NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS urls (
    id          UUID            NOT NULL,
    user_id     UUID            NOT NULL,
    long_url    VARCHAR(2048)   NOT NULL,
    short_url   VARCHAR(5)      NOT NULL,
    title       VARCHAR(80)     NOT NULL,
    description VARCHAR(255)    DEFAULT NULL,
    deleted     BOOLEAN         NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

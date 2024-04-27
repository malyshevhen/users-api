-- Addresses
CREATE TABLE addresses
(
    id         BIGSERIAL NOT NULL,
    country    VARCHAR(255),
    city       VARCHAR(255),
    street     VARCHAR(255),
    number     VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,

    PRIMARY KEY (id)
);

-- Users
CREATE TABLE users
(
    id         BIGSERIAL    NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,
    birth_date DATE         NOT NULL,
    address_id BIGINT,
    phone      VARCHAR(255),
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP,

    PRIMARY KEY (id),
    FOREIGN KEY (address_id) REFERENCES addresses (id)
);

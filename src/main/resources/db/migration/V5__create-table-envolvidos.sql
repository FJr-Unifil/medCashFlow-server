CREATE TABLE involveds
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(60)  NOT NULL,
    document   VARCHAR(14)  NOT NULL UNIQUE,
    phone      VARCHAR(11)  NOT NULL UNIQUE,
    email      VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active  BOOLEAN      NOT NULL DEFAULT TRUE,
    clinic_id  UUID         NOT NULL REFERENCES clinics (id)
);
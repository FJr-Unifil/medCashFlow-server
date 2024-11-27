CREATE TABLE account_planning
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    emoji       VARCHAR(4),
    clinic_id   UUID        NOT NULL REFERENCES clinics (id),
    UNIQUE (name, clinic_id)
);
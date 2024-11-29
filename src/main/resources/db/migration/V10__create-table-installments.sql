CREATE TABLE installments
(
    id                 BIGINT PRIMARY KEY,
    bill_id            BIGINT         NOT NULL REFERENCES bills (id),
    installment_number INT            NOT NULL,
    pricing            NUMERIC(10, 2) NOT NULL,
    due_date           TIMESTAMP      NOT NULL,
    is_paid            BOOLEAN        NOT NULL DEFAULT FALSE
);
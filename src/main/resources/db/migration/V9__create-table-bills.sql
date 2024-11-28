CREATE TABLE bills
(
    id                BIGSERIAL PRIMARY KEY,
    name              VARCHAR(50)                                      NOT NULL,
    pricing           NUMERIC(10, 2)                                   NOT NULL,
    type              VARCHAR(7) CHECK (type IN ('INCOME', 'OUTCOME')) NOT NULL,
    employee_id       BIGINT                                           NOT NULL REFERENCES employees (id),
    clinic_id         UUID                                             NOT NULL REFERENCES clinics (id),
    envolvido_id      BIGINT                                           NOT NULL REFERENCES involveds (id),
    acc_planning_id   BIGINT REFERENCES account_planning (id),
    payment_method_id BIGINT                                           NOT NULL REFERENCES payment_methods (id),
    created_at        TIMESTAMP                                        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    due_date          TIMESTAMP                                        NOT NULL,
    qntd_parcelas     INT                                              NOT NULL,
    is_paid           BOOLEAN                                          NOT NULL DEFAULT FALSE
);
CREATE TABLE clinics (
     id BIGSERIAL PRIMARY KEY,
     name VARCHAR(100) NOT NULL UNIQUE,
     cnpj CHAR(14) NOT NULL UNIQUE,
     phone CHAR(10) NOT NULL UNIQUE,
     is_active BOOLEAN DEFAULT TRUE
);
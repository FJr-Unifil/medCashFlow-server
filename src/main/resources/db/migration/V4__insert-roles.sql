INSERT INTO roles (id, name) VALUES
                                 (1, 'MANAGER'),
                                 (2, 'FINANCIAL_ANALYST'),
                                 (3, 'DOCTOR')
ON CONFLICT (id) DO NOTHING;
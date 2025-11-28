DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_class WHERE relname = 'installments_id_seq'
    ) THEN
        CREATE SEQUENCE installments_id_seq;
        ALTER TABLE installments ALTER COLUMN id SET DEFAULT nextval('installments_id_seq');
        ALTER SEQUENCE installments_id_seq OWNED BY installments.id;
        SELECT setval('installments_id_seq', COALESCE((SELECT MAX(id) FROM installments), 0) + 1, false);
    END IF;
END $$;


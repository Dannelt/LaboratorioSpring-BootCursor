-- OPTIONAL: run manually in pgAdmin Query Tool connected to laboratoriobanco.
-- Never loaded by Spring Boot. Repeated execution leaves existing rows unchanged.
BEGIN;

DO $$
BEGIN
    IF current_database() <> 'laboratoriobanco' THEN
        RAISE EXCEPTION 'This script is restricted to laboratoriobanco';
    END IF;
END $$;

INSERT INTO bank_customers (full_name, email)
VALUES ('Demo Customer Alpha', 'demo.alpha@example.invalid'),
       ('Demo Customer Beta', 'demo.beta@example.invalid')
ON CONFLICT (email) DO NOTHING;

INSERT INTO bank_accounts (account_number, balance, customer_id, version)
SELECT 'LAB-DEMO-001', 1000.00, id, 0
FROM bank_customers WHERE email = 'demo.alpha@example.invalid'
ON CONFLICT (account_number) DO NOTHING;

INSERT INTO bank_accounts (account_number, balance, customer_id, version)
SELECT 'LAB-DEMO-002', 500.00, id, 0
FROM bank_customers WHERE email = 'demo.beta@example.invalid'
ON CONFLICT (account_number) DO NOTHING;

COMMIT;

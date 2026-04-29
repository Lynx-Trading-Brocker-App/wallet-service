-- Seed wallet data for the demo user used by the API Gateway stub auth.
-- Demo user id:
-- 550e8400-e29b-41d4-a716-446655440000

INSERT INTO wallets (
    id,
    user_id,
    currency,
    available_balance,
    reserved_balance,
    created_at,
    updated_at,
    is_active
)
VALUES
    (
        '880e8400-e29b-41d4-a716-446655440111',
        '550e8400-e29b-41d4-a716-446655440000',
        'USD',
        10000.0000,
        500.0000,
        NOW(),
        NOW(),
        TRUE
    ),
    (
        '880e8400-e29b-41d4-a716-446655440222',
        '550e8400-e29b-41d4-a716-446655440000',
        'EUR',
        2500.0000,
        0.0000,
        NOW(),
        NOW(),
        TRUE
    )
ON CONFLICT (user_id, currency) DO NOTHING;


INSERT INTO wallet_transactions (
    id,
    wallet_id,
    reference_id,
    transaction_type,
    amount,
    created_at
)
VALUES
    (
        '123e4567-e89b-12d3-a456-426614174000',
        '880e8400-e29b-41d4-a716-446655440111',
        NULL,
        'DEPOSIT',
        10000.0000,
        NOW() - INTERVAL '5 days'
    ),
    (
        '123e4567-e89b-12d3-a456-426614174001',
        '880e8400-e29b-41d4-a716-446655440111',
        '999e4567-e89b-12d3-a456-426614174999',
        'ORDER_HOLD',
        500.0000,
        NOW() - INTERVAL '2 days'
    ),
    (
        '123e4567-e89b-12d3-a456-426614174002',
        '880e8400-e29b-41d4-a716-446655440222',
        NULL,
        'DEPOSIT',
        2500.0000,
        NOW() - INTERVAL '3 days'
    )
ON CONFLICT (id) DO NOTHING;
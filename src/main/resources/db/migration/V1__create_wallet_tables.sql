CREATE TABLE wallets
(
    id                UUID           NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id           UUID           NOT NULL,
    currency          VARCHAR(3)     NOT NULL,
    available_balance NUMERIC(19, 4) NOT NULL DEFAULT 0.0,
    reserved_balance  NUMERIC(19, 4) NOT NULL DEFAULT 0.0,
    updated_at        TIMESTAMP      NOT NULL DEFAULT NOW(),
    created_at        TIMESTAMP      NOT NULL DEFAULT NOW(),
    is_active         BOOLEAN        NOT NULL DEFAULT TRUE,

    CONSTRAINT uq_wallet_user_currency UNIQUE (user_id, currency)
);

CREATE INDEX idx_wallet_user_id ON wallets (user_id);

CREATE TYPE transaction_type AS ENUM (
    'DEPOSIT',
    'WITHDRAWAL',
    'ORDER_HOLD',
    'ORDER_RELEASE'
);

CREATE TABLE wallet_transactions
(
    id               UUID             NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    wallet_id        UUID             NOT NULL,
    reference_id     UUID,
    transaction_type transaction_type NOT NULL,
    amount           NUMERIC(19, 4)   NOT NULL,
    created_at       TIMESTAMP        NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_transaction_wallet FOREIGN KEY (wallet_id) REFERENCES wallets (id)
);

CREATE INDEX idx_transaction_wallet_id ON wallet_transactions (wallet_id);
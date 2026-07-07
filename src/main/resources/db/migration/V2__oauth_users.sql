ALTER TABLE users
    ADD COLUMN auth_provider VARCHAR(20) NOT NULL DEFAULT 'LOCAL',
    ADD COLUMN oauth_subject VARCHAR(255);

ALTER TABLE users
    ALTER COLUMN password_hash DROP NOT NULL;

ALTER TABLE users
    ADD CONSTRAINT users_auth_provider_check
        CHECK (auth_provider IN ('LOCAL', 'GOOGLE', 'APPLE'));

CREATE UNIQUE INDEX idx_users_oauth_provider_subject
    ON users (auth_provider, oauth_subject)
    WHERE oauth_subject IS NOT NULL;

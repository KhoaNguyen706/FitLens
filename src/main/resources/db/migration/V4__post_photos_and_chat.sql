ALTER TABLE fitness_posts
    ADD COLUMN photo_path TEXT;

CREATE TABLE direct_messages (
    id          BIGSERIAL PRIMARY KEY,
    sender_id   BIGINT NOT NULL REFERENCES users (id),
    receiver_id BIGINT NOT NULL REFERENCES users (id),
    body        TEXT NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_direct_messages_sender_receiver ON direct_messages (sender_id, receiver_id, created_at);
CREATE INDEX idx_direct_messages_receiver_sender ON direct_messages (receiver_id, sender_id, created_at);

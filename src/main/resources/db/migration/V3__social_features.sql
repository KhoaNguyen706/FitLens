CREATE TABLE friendships (
    id              BIGSERIAL PRIMARY KEY,
    requester_id    BIGINT NOT NULL REFERENCES users (id),
    receiver_id     BIGINT NOT NULL REFERENCES users (id),
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT friendships_status_check
        CHECK (status IN ('PENDING', 'ACCEPTED', 'BLOCKED')),
    CONSTRAINT friendships_no_self CHECK (requester_id <> receiver_id),
    CONSTRAINT friendships_unique_pair UNIQUE (requester_id, receiver_id)
);

CREATE TABLE fitness_posts (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users (id),
    meal_entry_id   BIGINT REFERENCES meal_entries (id) ON DELETE SET NULL,
    caption         TEXT,
    visibility      VARCHAR(20) NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fitness_posts_visibility_check
        CHECK (visibility IN ('PRIVATE', 'CLOSE_FRIENDS'))
);

CREATE TABLE post_reactions (
    id          BIGSERIAL PRIMARY KEY,
    post_id     BIGINT NOT NULL REFERENCES fitness_posts (id) ON DELETE CASCADE,
    user_id     BIGINT NOT NULL REFERENCES users (id),
    emoji       VARCHAR(16) NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT post_reactions_unique_user_post UNIQUE (post_id, user_id)
);

CREATE INDEX idx_friendships_requester ON friendships (requester_id);
CREATE INDEX idx_friendships_receiver ON friendships (receiver_id);
CREATE INDEX idx_friendships_status ON friendships (status);
CREATE INDEX idx_fitness_posts_user_id ON fitness_posts (user_id);
CREATE INDEX idx_fitness_posts_visibility ON fitness_posts (visibility);
CREATE INDEX idx_fitness_posts_created_at ON fitness_posts (created_at DESC);
CREATE INDEX idx_post_reactions_post_id ON post_reactions (post_id);

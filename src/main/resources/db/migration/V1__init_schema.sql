CREATE TABLE users (
    id              BIGSERIAL PRIMARY KEY,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    display_name    VARCHAR(255) NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE user_goals (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL REFERENCES users (id),
    daily_calorie_goal  INTEGER NOT NULL,
    protein_goal_grams  INTEGER,
    carbs_goal_grams    INTEGER,
    fat_goal_grams      INTEGER,
    starting_weight_kg  NUMERIC(6, 2),
    target_weight_kg    NUMERIC(6, 2),
    is_active           BOOLEAN,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE meal_entries (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users (id),
    meal_name       VARCHAR(255) NOT NULL,
    meal_type       VARCHAR(20) NOT NULL,
    calories        INTEGER NOT NULL,
    protein_grams   NUMERIC(6, 2),
    carbs_grams     NUMERIC(6, 2),
    fat_grams       NUMERIC(6, 2),
    notes           TEXT,
    logged_at       TIMESTAMPTZ NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT meal_entries_meal_type_check
        CHECK (meal_type IN ('BREAKFAST', 'LUNCH', 'DINNER', 'SNACK', 'OTHER'))
);

CREATE TABLE meal_photos (
    id                BIGSERIAL PRIMARY KEY,
    user_id           BIGINT NOT NULL REFERENCES users (id),
    meal_entry_id     BIGINT REFERENCES meal_entries (id),
    local_uri         TEXT,
    local_asset_id    VARCHAR(255),
    storage_provider  VARCHAR(20),
    storage_path      TEXT,
    photo_source      VARCHAR(10) NOT NULL,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT meal_photos_photo_source_check
        CHECK (photo_source IN ('LOCAL', 'CLOUD')),
    CONSTRAINT meal_photos_storage_provider_check
        CHECK (storage_provider IS NULL OR storage_provider IN ('SUPABASE', 'S3', 'CLOUDINARY'))
);

CREATE TABLE weight_logs (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL REFERENCES users (id),
    weight_kg   NUMERIC(6, 2) NOT NULL,
    logged_at   TIMESTAMPTZ NOT NULL,
    notes       TEXT,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_user_goals_user_id ON user_goals (user_id);
CREATE INDEX idx_meal_entries_user_id ON meal_entries (user_id);
CREATE INDEX idx_meal_entries_logged_at ON meal_entries (logged_at);
CREATE INDEX idx_meal_photos_user_id ON meal_photos (user_id);
CREATE INDEX idx_meal_photos_meal_entry_id ON meal_photos (meal_entry_id);
CREATE INDEX idx_weight_logs_user_id ON weight_logs (user_id);
CREATE INDEX idx_weight_logs_logged_at ON weight_logs (logged_at);

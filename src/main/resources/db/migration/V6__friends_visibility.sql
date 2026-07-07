ALTER TABLE fitness_posts
    DROP CONSTRAINT IF EXISTS fitness_posts_visibility_check;

ALTER TABLE fitness_posts
    ADD CONSTRAINT fitness_posts_visibility_check
        CHECK (visibility IN ('PRIVATE', 'FRIENDS', 'CLOSE_FRIENDS'));

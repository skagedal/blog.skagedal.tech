-- Trigger for updating updated_at columns

CREATE FUNCTION updated_at_trigger() RETURNS trigger
    LANGUAGE plpgsql AS
$$BEGIN
    NEW.updated_at := current_timestamp;
    RETURN NEW;
END;$$;

-- post table

CREATE TABLE post(
    -- Same as slog
    id TEXT NOT NULL PRIMARY KEY,

    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TRIGGER post_updated_at_trigger BEFORE UPDATE ON post
    FOR EACH ROW EXECUTE PROCEDURE updated_at_trigger();

-- comment table

CREATE TABLE comment(
    id uuid NOT NULL PRIMARY KEY DEFAULT gen_random_uuid(),
    post_id TEXT NOT NULL,
    author TEXT NOT NULL,
    content TEXT NOT NULL,

    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    approved_at TIMESTAMPTZ DEFAULT NULL,

    FOREIGN KEY (post_id) REFERENCES post(id)
);

CREATE TRIGGER comment_updated_at_trigger BEFORE UPDATE ON comment
    FOR EACH ROW EXECUTE PROCEDURE updated_at_trigger();

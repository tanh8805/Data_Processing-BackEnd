CREATE EXTENSION IF NOT EXISTS "pgcrypto";


CREATE TABLE users (
   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

   user_id UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),

   fullname VARCHAR(100),

   email VARCHAR(255) UNIQUE NOT NULL,

   password_hash TEXT NOT NULL,

   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE conversations (
   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

   conversation_id UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),

   user_id UUID NOT NULL,

   title VARCHAR(255),

   status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',

   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

   CONSTRAINT fk_conversations_user
       FOREIGN KEY (user_id)
           REFERENCES users(user_id)
           ON DELETE CASCADE
);

CREATE TABLE jobs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    conversation_id UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),

    user_id UUID NOT NULL,

    original_file_name VARCHAR(255),

    input_file_path TEXT NOT NULL,

    output_file_path TEXT,

    status VARCHAR(50) NOT NULL,

    impute_strategy VARCHAR(50),

    total_rows INT DEFAULT 0,

    valid_rows_count INT DEFAULT 0,

    invalid_rows_count INT DEFAULT 0,

    error_message TEXT,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_jobs_conversation
        FOREIGN KEY (conversation_id)
            REFERENCES conversations(conversation_id)
            ON DELETE CASCADE
);


CREATE TABLE job_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    conversation_id UUID NOT NULL,

    event_type VARCHAR(50) NOT NULL,

    payload JSONB,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_job_events_job
        FOREIGN KEY (conversation_id)
            REFERENCES jobs(conversation_id)
            ON DELETE CASCADE
);


CREATE INDEX idx_users_user_id
    ON users(user_id);

CREATE INDEX idx_users_email
    ON users(email);

CREATE INDEX idx_jobs_conversation_id
    ON jobs(conversation_id);

CREATE INDEX idx_jobs_user_id
    ON jobs(user_id);

CREATE INDEX idx_jobs_status
    ON jobs(status);

CREATE INDEX idx_jobs_created_at
    ON jobs(created_at DESC);

CREATE INDEX idx_job_events_conversation_id
    ON job_events(conversation_id);

CREATE INDEX idx_job_events_event_type
    ON job_events(event_type);

CREATE INDEX idx_job_events_created_at
    ON job_events(created_at DESC);

CREATE INDEX idx_job_events_payload_gin
    ON job_events
    USING GIN(payload);
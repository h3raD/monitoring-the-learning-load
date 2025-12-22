CREATE TYPE role_type AS ENUM ('STUDENT', 'CURATOR');
CREATE TYPE status_task_type AS ENUM ('TO_DO', 'IN_PROGRESS', 'DONE', 'OVERDUE');

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL  PRIMARY KEY NOT NULL,
    email TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    firstName TEXT NOT NULL,
    lastName TEXT NOT NULL,
    role role_type NOT NULL,
    curator_id BIGINT,
    telegram_id BIGINT,
    google_calendar_api_key TEXT,
    FOREIGN KEY (curator_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS tasks (
    id BIGSERIAL  PRIMARY KEY NOT NULL,
    title TEXT NOT NULL,
    description TEXT NOT NULL,
    add_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deadline TIMESTAMP NOT NULL,
    start_work TIMESTAMP,
    end_work TIMESTAMP,
    status status_task_type NOT NULL DEFAULT 'TO_DO',
    student_id BIGINT NOT NULL,
    curator_id BIGINT NOT NULL,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (curator_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS rests (
    id BIGSERIAL  PRIMARY KEY NOT NULL,
    date DATE NOT NULL,
    student_id BIGINT NOT NULL,
    hours INTEGER NOT NULL,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE
);


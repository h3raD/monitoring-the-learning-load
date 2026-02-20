CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    email TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    firstName TEXT NOT NULL,
    lastName TEXT NOT NULL,
    role TEXT NOT NULL CHECK (role IN ('STUDENT', 'CURATOR')),
    curator_id INTEGER,
    telegram_id INTEGER,
    google_calendar_api_key TEXT,
    FOREIGN KEY (curator_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS tasks (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    description TEXT NOT NULL,
    add_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deadline TIMESTAMP NOT NULL,
    start_work TIMESTAMP,
    end_work TIMESTAMP,
    status TEXT NOT NULL DEFAULT 'TO_DO' CHECK (status IN ('TO_DO', 'IN_PROGRESS', 'DONE', 'OVERDUE')),
    student_id INTEGER NOT NULL,
    curator_id INTEGER NOT NULL,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (curator_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS rests (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    date DATE NOT NULL,
    student_id INTEGER NOT NULL,
    hours INTEGER NOT NULL,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE
);


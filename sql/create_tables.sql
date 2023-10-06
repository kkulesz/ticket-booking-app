CREATE TABLE movies(
    id      VARCHAR(255) NOT NULL,
    title   VARCHAR(255) NOT NULL,
    length  INTEGER NOT NULL
);

CREATE TABLE rooms(
    id      VARCHAR(255) NOT NULL,
    rows    INTEGER,
    cols    INTEGER
);

CREATE TABLE screenings(
    id          VARCHAR(255) NOT NULL,
    movie_id    VARCHAR(255) NOT NULL,
    room_id     VARCHAR(255) NOT NULL,
    time        TIMESTAMP NOT NULL
);

CREATE TABLE reservations(
    screening_id    VARCHAR(255) NOT NULL,
    row             INTEGER NOT NULL,
    col             INTEGER NOT NULL,
    name            VARCHAR(32) NOT NULL,
    surname         VARCHAR(32) NOT NULL,
    ticket_type     VARCHAR(32) NOT NULL
);

CREATE TABLE vouchers(
    id      VARCHAR(255) NOT NULL,
    is_used BOOLEAN NOT NULL
)


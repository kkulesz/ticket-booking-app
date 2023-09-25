CREATE DATABASE ticket_booking;
\c ticket_booking;

CREATE TABLE movies(
    id      VARCHAR(255) NOT NULL,
    title   VARCHAR(255) NOT NULL,
    length  INTEGER NOT NULL
);

CREATE TABLE rooms(
    id      VARCHAR(255) NOT NULL,
    rows    INTEGER,
    columns INTEGER
);

CREATE TABLE screenings(
    id          VARCHAR(255) NOT NULL,
    movice_id   VARCHAR(255) NOT NULL,
    room_id     VARCHAR(255) NOT NULL,
    time        TIMESTAMP NOT NULL
);

CREATE TABLE reservations(
    screening_id    VARCHAR(255) NOT NULL,
    row             INTEGER NOT NULL,
    column          INTEGER NOT NULL,
    name            VARCHAR(32) NOT NULL,
    surname         VARCHAR(32) NOT NULL,
    ticket_type     VARCHAR(32) NOT NULL
);


INSERT INTO movies 
    (id, title, length)
VALUES 
    ('67f4b3d9-2451-422c-8366-ff5ca12ec72a', 'Szybcy i wściekli', 90),
    ('67f4b3d9-2451-422c-8366-ff5ca12ec72b', 'Piraci z karaibów', 120),
    ('67f4b3d9-2451-422c-8366-ff5ca12ec72c', 'Incepcja', 150);

INSERT INTO rooms
    (id, rows, cols)
VALUES
    ('pokój nr 1', 2, 2),
    ('pokój nr 2', 3, 1),
    ('pokój nr 3', 4, 3)    ;

INSERT INTO screenings
    (id, movie_id, room_id, time)
VALUES
    ('99f4b3d9-2451-422c-8366-ff5ca12ec72a', '67f4b3d9-2451-422c-8366-ff5ca12ec72a', 'pokój nr 1', '2023-09-22T12:00:00'), -- room0 movie0
    ('99f4b3d9-2451-422c-8366-ff5ca12ec72b', '67f4b3d9-2451-422c-8366-ff5ca12ec72b', 'pokój nr 1', '2023-09-22T12:30:00'), -- room0 movie1
    ('99f4b3d9-2451-422c-8366-ff5ca12ec72c', '67f4b3d9-2451-422c-8366-ff5ca12ec72a', 'pokój nr 2', '2023-09-22T12:30:00'), -- room1 movie0
    ('99f4b3d9-2451-422c-8366-ff5ca12ec72d', '67f4b3d9-2451-422c-8366-ff5ca12ec72b', 'pokój nr 2', '2023-09-22T12:35:00'), -- room1 movie1
    ('99f4b3d9-2451-422c-8366-ff5ca12ec72e', '67f4b3d9-2451-422c-8366-ff5ca12ec72b', 'pokój nr 3', '2023-09-22T12:30:00'), -- room2 movie1
    ('99f4b3d9-2451-422c-8366-ff5ca12ec72f', '67f4b3d9-2451-422c-8366-ff5ca12ec72c', 'pokój nr 3', '2023-09-22T12:35:00'); -- room2 movie2


INSERT INTO vouchers
    (id, is_used)
VALUES
    ('00000000-2451-422c-8366-ff5ca12ec72a', FALSE),
    ('00000000-2451-422c-8366-ff5ca12ec72b', TRUE);
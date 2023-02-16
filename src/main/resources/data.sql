DELETE FROM item_request;
DELETE FROM items;
ALTER TABLE items ALTER COLUMN item_id RESTART WITH 1;
DELETE FROM users;
ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1;
DELETE FROM bookings;
MERGE INTO booking_status (booking_status_id, booking_status_name)
    VALUES (1, 'WAITING'),
    (2, 'APPROVED'),
    (3, 'REJECTED'),
    (4, 'CANCELED');
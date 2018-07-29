CREATE TABLE local_key_store
(

key_id INTEGER PRIMARY KEY AUTO_INCREMENT,
key_data VARCHAR(32), --encrypted bits of key--
kek_id INTEGER --says what key was used to encrypt key--
);
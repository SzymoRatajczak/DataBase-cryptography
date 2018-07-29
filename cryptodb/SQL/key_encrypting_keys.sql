CREATE TABLE key_encrypting_keys
(
kek_id INTEGER PRIMARY KEY AUTO_INCREMENT,
key_data TINYBLOB NOT NULL, --stores key encryptinh keys
activation_date DATETIME NOT NULL--says when key wil be  alive 
);

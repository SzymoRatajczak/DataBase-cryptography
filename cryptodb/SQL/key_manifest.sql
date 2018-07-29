CREATE TABLE key_manifest
(

alias_id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
key_alias  VARCHAR(30) NOT NULL,
key_family VARCHAR(30) NOT NULL,
engine VARCHAR(30) NOT NULL,
key_id VARCHAR(30) NOT NULL,
key_activation_date DATETIME NOT NULL,
status VARCHAR(30) NOT NULL
);
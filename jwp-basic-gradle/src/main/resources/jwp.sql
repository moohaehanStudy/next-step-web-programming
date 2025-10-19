DROP TABLE IF EXISTS USERS;

CREATE TABLE USERS
(
    userId   varchar(12) NOT NULL,
    password varchar(12) NOT NULL,
    name     varchar(20) NOT NULL,
    email    varchar(50),

    PRIMARY KEY (userId)
);

INSERT INTO USERS VALUES('soyun', '1234', 'soyun', 'test@test.com');
INSERT INTO USERS VALUES('soyun2', '12341234', 'soyun2', 'test2@test2.com');
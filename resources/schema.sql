DROP TABLE IF EXISTS loan;
DROP TABLE IF EXISTS book;
DROP TABLE IF EXISTS reader;
DROP TABLE IF EXISTS author;

CREATE TABLE author (
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    name    VARCHAR(200) NOT NULL,
    country VARCHAR(100)
);

CREATE TABLE book (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    title     VARCHAR(300) NOT NULL,
    author_id BIGINT NOT NULL,
    available INTEGER NOT NULL DEFAULT 1,
    FOREIGN KEY (author_id) REFERENCES author(id)
);

CREATE TABLE reader (
    id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    name  VARCHAR(200) NOT NULL,
    email VARCHAR(200)
);

CREATE TABLE loan (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id     BIGINT NOT NULL,
    reader_id   BIGINT NOT NULL,
    loan_date   VARCHAR(20) NOT NULL,
    return_date VARCHAR(20),
    FOREIGN KEY (book_id)   REFERENCES book(id),
    FOREIGN KEY (reader_id) REFERENCES reader(id)
);

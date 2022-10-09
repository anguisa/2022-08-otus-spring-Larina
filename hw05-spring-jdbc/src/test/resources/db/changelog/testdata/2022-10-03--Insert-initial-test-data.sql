--liquibase formatted sql

--changeset olga:2022-10-03-001-author-data-test
insert into author (name)
values ('Катя Петрова'), ('Женя Максимова');

--changeset olga:2022-10-03-002-genre-data-test
insert into genre (title)
values ('Детектив'), ('Фантастика');

--changeset olga:2022-10-03-003-book-data-test
insert into book (title, author_id, genre_id)
values ('Мой детектив', 1, 1),
       ('Необычная фантастика', 2, 2),
       ('Смешной детектив', 2, 1)
;
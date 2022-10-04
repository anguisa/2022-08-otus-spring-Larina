--liquibase formatted sql

--changeset olga:2022-10-03-001-author-data
insert into author (name)
values ('Катя Петрова'), ('Женя Максимова'), ('Иван Алексеев');

--changeset olga:2022-10-03-002-genre-data
insert into genre (title)
values ('Детектив'), ('Фантастика'), ('Сказка');

--changeset olga:2022-10-03-003-book-data
insert into book (title, author_id, genre_id)
values ('Мой детектив', 1, 1),
       ('Необычная фантастика', 3, 2),
       ('Странная сказка', 2, 3),
       ('Смешной детектив', 2, 1),
       ('Страшная фантастика', 3, 2)
;
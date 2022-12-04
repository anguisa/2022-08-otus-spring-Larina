--liquibase formatted sql

--changeset olga:2022-10-10-001-author-data-test
insert into author (name)
values ('Катя Петрова'), ('Женя Максимова');

--changeset olga:2022-10-10-002-genre-data-test
insert into genre (title)
values ('Детектив'), ('Фантастика');

--changeset olga:2022-10-10-003-book-data-test
insert into book (title, author_id)
values ('Мой детектив', 1),
       ('Необычная фантастика', 2),
       ('Смешной детектив', 2)
;

--changeset olga:2022-10-10-004-book-genre_data
insert into book_genre (book_id, genre_id)
values (1, 1),
       (2, 2),
       (3, 1), (3, 2)
;

--changeset olga:2022-10-10-005-book-comment_data
insert into book_comment (book_id, comment_text)
values (1, 'Интересная'), (1, 'Увлекательная'),
       (2, 'Скучная'),
       (3, 'Захватывающая'), (3, 'Необычная')
;
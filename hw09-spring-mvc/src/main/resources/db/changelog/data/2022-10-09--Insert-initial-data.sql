--liquibase formatted sql

--changeset olga:2022-10-09-001-author-data
insert into author (name)
values ('Катя Петрова'), ('Женя Максимова'), ('Иван Алексеев');

--changeset olga:2022-10-09-002-genre-data
insert into genre (title)
values ('Детектив'), ('Фантастика'), ('Сказка'), ('Проза'), ('Поззия');

--changeset olga:2022-10-09-003-book-data
insert into book (title, author_id)
values ('Мой детектив', 1),
       ('Необычная фантастика', 3),
       ('Странная сказка', 2),
       ('Смешной детектив', 2),
       ('Страшная фантастика', 3)
;

--changeset olga:2022-10-09-004-book-genre_data
insert into book_genre (book_id, genre_id)
values (1, 1), (1, 4),
       (2, 2), (2, 4),
       (3, 3), (3, 5),
       (4, 1), (4, 4),
       (5, 2), (5, 5)
;

--changeset olga:2022-10-10-005-book-comment_data
insert into book_comment (book_id, comment_text)
values (1, 'Интересная'), (1, 'Увлекательная'),
       (2, 'Скучная'),
       (3, 'Захватывающая'), (3, 'Необычная'),
       (4, 'Смешная'),
       (5, 'Интересная'), (5, 'Захватывающая')
;
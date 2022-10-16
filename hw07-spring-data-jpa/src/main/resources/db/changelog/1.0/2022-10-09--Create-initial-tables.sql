--liquibase formatted sql

--changeset olga:2022-10-09-001-author
create table author (
    id bigserial,
    name varchar(255),
    constraint author_pkey primary key (id),
    constraint author_name_unq unique (name)
);

--changeset olga:2022-10-09-002-genre
create table genre (
    id bigserial,
    title varchar(255),
    constraint genre_pkey primary key (id),
    constraint genre_title_unq unique (title)
);

--changeset olga:2022-10-09-003-book
create table book (
    id bigserial,
    title varchar(255),
    author_id bigint,
    constraint book_pkey primary key (id),
    constraint book_author_id_fkey foreign key (author_id) references author (id) on delete set null
);

--changeset olga:2022-10-09-004-book-genre
create table book_genre (
    book_id bigint,
    genre_id bigint,
    constraint book_genre_pkey primary key (book_id, genre_id),
    constraint book_genre_book_id_fkey foreign key (book_id) references book (id) on delete cascade,
    constraint book_genre_genre_id_fkey foreign key (genre_id) references genre (id) on delete cascade
);

--changeset olga:2022-10-10-005-book-comment
create table book_comment (
    id bigserial,
    comment_text varchar(255),
    book_id bigint,
    constraint book_comment_pkey primary key (id),
    constraint book_comment_book_id_fkey foreign key (book_id) references book (id) on delete cascade
);
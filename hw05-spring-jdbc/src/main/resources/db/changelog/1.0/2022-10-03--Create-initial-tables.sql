--liquibase formatted sql

--changeset olga:2022-10-03-001-author
create table author (
    id bigserial,
    name varchar(255),
    constraint author_pkey primary key (id),
    constraint author_name_unq unique (name)
);

--changeset olga:2022-10-03-002-genre
create table genre (
    id bigserial,
    title varchar(255),
    constraint genre_pkey primary key (id),
    constraint genre_title_unq unique (title)
);

--changeset olga:2022-10-03-003-book
create table book (
    id bigserial,
    title varchar(255),
    author_id bigint,
    genre_id bigint,
    constraint book_pkey primary key (id),
    constraint book_author_id_fkey foreign key (author_id) references author (id) on delete set null,
    constraint book_genre_id_fkey foreign key (genre_id) references genre (id) on delete set null
);
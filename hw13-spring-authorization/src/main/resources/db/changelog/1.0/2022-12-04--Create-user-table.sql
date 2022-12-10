--liquibase formatted sql

--changeset olga:2022-12-04-006-user
create table user_info (
    id          bigserial,
    username    varchar(255),
    password    varchar(255),
    constraint user_info_pkey primary key (id),
    constraint user_info_username_unq unique (username)
);

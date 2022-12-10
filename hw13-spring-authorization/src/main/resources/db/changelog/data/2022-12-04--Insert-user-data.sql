--liquibase formatted sql

--changeset olga:2022-12-04-006-user-data
insert into user_info (username, password)
values ('admin', '$2a$10$3DHpnBxoYCmcUl2DtT8wDuMx3AeOZeqcnwsNCHGH40afWo6n.lT2q'),
       ('user', '$2a$10$3DHpnBxoYCmcUl2DtT8wDuMx3AeOZeqcnwsNCHGH40afWo6n.lT2q'),
       ('guest', '$2a$10$3DHpnBxoYCmcUl2DtT8wDuMx3AeOZeqcnwsNCHGH40afWo6n.lT2q')
;

--liquibase formatted sql

--changeset olga:2022-12-10-007-user-role-data
insert into user_role (user_id, authority)
values (1, 'ADMIN'),
       (2, 'USER'),
       (3, 'GUEST')
;

--changeset olga:2022-12-10-008-acl-data
insert into acl_sid (principal, sid)
values (1, 'admin'), (1, 'user');

insert into acl_class (class)
values ('ru.otus.dto.CommentDto');

insert into acl_object_identity (object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting)
values (1, 1, null, 1, 0),
       (1, 2, null, 1, 0),
       (1, 3, null, 1, 0),
       (1, 4, null, 1, 0),
       (1, 5, null, 1, 0),
       (1, 6, null, 1, 0),
       (1, 7, null, 1, 0),
       (1, 8, null, 1, 0)
;

insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
values (1, 1, 1, 31, 1, 1, 1), -- 11111
       (2, 1, 1, 31, 1, 1, 1),
       (3, 1, 1, 31, 1, 1, 1),
       (4, 1, 1, 31, 1, 1, 1),
       (5, 1, 1, 31, 1, 1, 1),
       (6, 1, 1, 31, 1, 1, 1),
       (7, 1, 1, 31, 1, 1, 1),
       (8, 1, 1, 31, 1, 1, 1),
       (1, 2, 2, 15, 1, 1, 1), -- 1111
       (2, 2, 2, 1, 1, 1, 1),
       (3, 2, 2, 15, 1, 1, 1),
       (4, 2, 2, 1, 1, 1, 1),
       (5, 2, 2, 15, 1, 1, 1),
       (6, 2, 2, 0, 1, 1, 1),
       (7, 2, 2, 15, 1, 1, 1),
       (8, 2, 2, 0, 1, 1, 1)
;

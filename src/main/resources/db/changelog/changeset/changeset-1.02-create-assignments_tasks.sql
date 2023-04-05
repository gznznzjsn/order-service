--liquibase formatted sql

--changeset gznznzjsn:create-assignments_tasks
create table assignments_tasks
(
    assignment_id bigint references assignments on delete cascade,
    task_id       varchar,
    constraint at_pkey primary key (task_id, assignment_id)
);
--rollback drop table assignments_tasks;
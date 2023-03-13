--liquibase formatted sql

--changeset gznznzjsn:create-assignments
create table assignments
(
    assignment_id        bigserial primary key,
    order_id             bigint references orders on delete cascade,
    specialization    varchar(40),
    employee_id          bigint,
    status varchar(40),
    start_time           timestamp,
    final_cost           numeric,
    user_commentary      varchar(255),
    employee_commentary  varchar(255),
    constraint a_un unique (order_id, specialization)
);
--rollback drop table assignments;
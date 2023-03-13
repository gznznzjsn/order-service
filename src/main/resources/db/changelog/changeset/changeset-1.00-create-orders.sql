--liquibase formatted sql

--changeset gznznzjsn:create-orders
create table orders
(
    order_id     bigserial primary key,
    status    varchar(40),
    user_id      bigint,
    arrival_time timestamp not null,
    created_at   timestamp not null,
    finished_at  timestamp
);
--rollback drop table orders;

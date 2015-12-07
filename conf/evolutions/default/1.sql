# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table tool (
  id                        bigserial not null,
  name                      varchar(255),
  description               varchar(255),
  type_id                   bigint,
  owner_id                  bigint,
  constraint pk_tool primary key (id))
;

create table tool_type (
  id                        bigserial not null,
  name                      varchar(255),
  constraint pk_tool_type primary key (id))
;

create table users (
  id                        bigserial not null,
  username                  varchar(255),
  email                     varchar(255),
  password_hash             varchar(255),
  constraint pk_users primary key (id))
;

alter table tool add constraint fk_tool_type_1 foreign key (type_id) references tool_type (id);
create index ix_tool_type_1 on tool (type_id);
alter table tool add constraint fk_tool_owner_2 foreign key (owner_id) references users (id);
create index ix_tool_owner_2 on tool (owner_id);



# --- !Downs

drop table if exists tool cascade;

drop table if exists tool_type cascade;

drop table if exists users cascade;


# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table tool (
  id                        bigserial not null,
  name                      varchar(255),
  owner_id                  bigint,
  borrower_id               bigint,
  description               varchar(255),
  type_id                   bigint,
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
  password_hash             varchar(255),
  constraint pk_users primary key (id))
;

alter table tool add constraint fk_tool_owner_1 foreign key (owner_id) references users (id);
create index ix_tool_owner_1 on tool (owner_id);
alter table tool add constraint fk_tool_borrower_2 foreign key (borrower_id) references users (id);
create index ix_tool_borrower_2 on tool (borrower_id);
alter table tool add constraint fk_tool_type_3 foreign key (type_id) references tool_type (id);
create index ix_tool_type_3 on tool (type_id);



# --- !Downs

drop table if exists tool cascade;

drop table if exists tool_type cascade;

drop table if exists users cascade;


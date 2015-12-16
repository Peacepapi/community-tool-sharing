# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table comment (
  id                        bigserial not null,
  body                      varchar(255),
  poster_id                 bigint,
  tool_id                   bigint,
  datetime_posted           varchar(255),
  constraint pk_comment primary key (id))
;

create table profile (
  id                        bigserial not null,
  user_id                   bigint,
  f_name                    varchar(255),
  l_name                    varchar(255),
  constraint uq_profile_user_id unique (user_id),
  constraint pk_profile primary key (id))
;

create table tool (
  id                        bigserial not null,
  name                      varchar(255),
  owner_id                  bigint,
  description               varchar(255),
  tool_type_id              bigint,
  borrower_id               bigint,
  borrowing_status          integer,
  constraint pk_tool primary key (id))
;

create table tool_type (
  id                        bigserial not null,
  name                      varchar(255),
  constraint uq_tool_type_name unique (name),
  constraint pk_tool_type primary key (id))
;

create table users (
  id                        bigserial not null,
  username                  varchar(255),
  email                     varchar(255),
  password_hash             varchar(255),
  user_profile_id           bigint,
  user_type                 varchar(255),
  constraint uq_users_username unique (username),
  constraint uq_users_email unique (email),
  constraint uq_users_user_profile_id unique (user_profile_id),
  constraint uq_users_1 unique (username,email),
  constraint pk_users primary key (id))
;

alter table comment add constraint fk_comment_poster_1 foreign key (poster_id) references users (id);
create index ix_comment_poster_1 on comment (poster_id);
alter table comment add constraint fk_comment_tool_2 foreign key (tool_id) references tool (id);
create index ix_comment_tool_2 on comment (tool_id);
alter table profile add constraint fk_profile_user_3 foreign key (user_id) references users (id);
create index ix_profile_user_3 on profile (user_id);
alter table tool add constraint fk_tool_owner_4 foreign key (owner_id) references users (id);
create index ix_tool_owner_4 on tool (owner_id);
alter table tool add constraint fk_tool_toolType_5 foreign key (tool_type_id) references tool_type (id);
create index ix_tool_toolType_5 on tool (tool_type_id);
alter table tool add constraint fk_tool_borrower_6 foreign key (borrower_id) references users (id);
create index ix_tool_borrower_6 on tool (borrower_id);
alter table users add constraint fk_users_userProfile_7 foreign key (user_profile_id) references profile (id);
create index ix_users_userProfile_7 on users (user_profile_id);



# --- !Downs

drop table if exists comment cascade;

drop table if exists profile cascade;

drop table if exists tool cascade;

drop table if exists tool_type cascade;

drop table if exists users cascade;


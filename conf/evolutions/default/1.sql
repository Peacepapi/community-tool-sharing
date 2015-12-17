# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table borrow_requests (
  id                        bigserial not null,
  owner_id                  bigint,
  message                   varchar(255),
  timestamp                 timestamp,
  requester_id              bigint,
  requested_tool_id         bigint,
  request_time              timestamp,
  constraint uq_borrow_requests_1 unique (requester_id,requested_tool_id),
  constraint pk_borrow_requests primary key (id))
;

create table comment (
  id                        bigserial not null,
  body                      varchar(255),
  poster_id                 bigint,
  tool_id                   bigint,
  datetime_posted           varchar(255),
  constraint pk_comment primary key (id))
;

create table notification (
  id                        bigserial not null,
  owner_id                  bigint,
  message                   varchar(255),
  timestamp                 timestamp,
  constraint pk_notification primary key (id))
;

create table profile (
  id                        bigserial not null,
  user_id                   bigint,
  f_name                    varchar(255),
  l_name                    varchar(255),
  constraint uq_profile_user_id unique (user_id),
  constraint pk_profile primary key (id))
;

create table request_notification (
  dtype                     varchar(10) not null,
  id                        bigserial not null,
  owner_id                  bigint,
  message                   varchar(255),
  timestamp                 timestamp,
  request_id                bigint,
  constraint uq_request_notification_request_ unique (request_id),
  constraint pk_request_notification primary key (id))
;

create table tool (
  id                        bigserial not null,
  name                      varchar(255),
  owner_id                  bigint,
  description               varchar(255),
  tool_type_id              bigint,
  borrower_id               bigint,
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

alter table borrow_requests add constraint fk_borrow_requests_owner_1 foreign key (owner_id) references users (id);
create index ix_borrow_requests_owner_1 on borrow_requests (owner_id);
alter table borrow_requests add constraint fk_borrow_requests_requester_2 foreign key (requester_id) references users (id);
create index ix_borrow_requests_requester_2 on borrow_requests (requester_id);
alter table borrow_requests add constraint fk_borrow_requests_requestedTo_3 foreign key (requested_tool_id) references tool (id);
create index ix_borrow_requests_requestedTo_3 on borrow_requests (requested_tool_id);
alter table comment add constraint fk_comment_poster_4 foreign key (poster_id) references users (id);
create index ix_comment_poster_4 on comment (poster_id);
alter table comment add constraint fk_comment_tool_5 foreign key (tool_id) references tool (id);
create index ix_comment_tool_5 on comment (tool_id);
alter table notification add constraint fk_notification_owner_6 foreign key (owner_id) references users (id);
create index ix_notification_owner_6 on notification (owner_id);
alter table profile add constraint fk_profile_user_7 foreign key (user_id) references users (id);
create index ix_profile_user_7 on profile (user_id);
alter table request_notification add constraint fk_request_notification_owner_8 foreign key (owner_id) references users (id);
create index ix_request_notification_owner_8 on request_notification (owner_id);
alter table request_notification add constraint fk_request_notification_reques_9 foreign key (request_id) references borrow_requests (id);
create index ix_request_notification_reques_9 on request_notification (request_id);
alter table tool add constraint fk_tool_owner_10 foreign key (owner_id) references users (id);
create index ix_tool_owner_10 on tool (owner_id);
alter table tool add constraint fk_tool_toolType_11 foreign key (tool_type_id) references tool_type (id);
create index ix_tool_toolType_11 on tool (tool_type_id);
alter table tool add constraint fk_tool_borrower_12 foreign key (borrower_id) references users (id);
create index ix_tool_borrower_12 on tool (borrower_id);
alter table users add constraint fk_users_userProfile_13 foreign key (user_profile_id) references profile (id);
create index ix_users_userProfile_13 on users (user_profile_id);



# --- !Downs

drop table if exists borrow_requests cascade;

drop table if exists comment cascade;

drop table if exists notification cascade;

drop table if exists profile cascade;

drop table if exists request_notification cascade;

drop table if exists tool cascade;

drop table if exists tool_type cascade;

drop table if exists users cascade;


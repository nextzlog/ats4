# --- !Ups

create table post (
	id int(10) not null auto_increment,
	call varchar(16),
	disp varchar(32),
	city varchar(64),
	sect varchar(64),
	name varchar(32),
	addr varchar(128),
	mail varchar(128),
	comm varchar(512),
	cnt int,
	mul int,
	elog varchar(23),
	primary key(id)
);

# --- !Downs

drop table post;

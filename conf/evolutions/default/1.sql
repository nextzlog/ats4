# --- !Ups

create table majors (
	id int(10) not null auto_increment,
	call varchar(32),
	name varchar(32),
	addr varchar(128),
	mail varchar(128),
	comm varchar(512),
	file varchar(64),
	primary key(id)
);

create table minors (
	id int(10) not null auto_increment,
	call varchar(32),
	sect varchar(64),
	city varchar(64),
	denom int,
	score int,
	mults int,
	primary key(id)
);

# --- !Downs

drop table majors;
drop table minors;

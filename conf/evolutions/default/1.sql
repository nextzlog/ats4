# --- !Ups

create table TEAMS (
	id int(10) not null auto_increment,
	call varchar(32),
	name varchar(32),
	addr varchar(128),
	mail varchar(128),
	comm varchar(512),
	primary key(id)
);

create table GAMES (
	id int(10) not null auto_increment,
	call varchar(32),
	sect varchar(64),
	city varchar(64),
	score int,
	total int,
	file varchar(64),
	primary key(id)
);

# --- !Downs

drop table TEAMS;
drop table GAMES;

# --- !Ups

create table entries (
	id int(10) not null auto_increment,
	call varchar(16),
	city varchar(64),
	mobi varchar(16),
	band varchar(16),
	mode varchar(16),
	name varchar(32),
	addr varchar(128),
	mail varchar(128),
	comm varchar(1024),
	time datetime,
	calls int,
	mults int,
	primary key(id)
);

# --- !Downs

drop table entries;

create table user
	(
	 id				varchar(20) not null,
	 password		varchar(20) not null,
	 name 			varchar(20) ,
	 address  		varchar(20) ,
	 account_number varchar(20) not null,
	 phone_number 	varchar(20) ,
	 birthday    	varchar(20) ,

	 date_joined 	date default current_date,
	 start_date 	date default current_date,
	 end_date		date default date_add(current_date(), interval 1 month),
	 primary key(id)
	);

create table provider
	(
	 id				varchar(20) not null,
	 password		varchar(20) not null,
	 name 			varchar(20) ,
	 address  		varchar(20) ,
	 account_number varchar(20) not null,
	 phone_number 	varchar(20) ,
	 birthday    	varchar(20) ,

	 date_joined 	date default current_date,
	 start_date 	date default current_date,
	 end_date		date default date_add(current_date(), interval 1 month),
	 primary key(id)
	);

create table item
	(
	 name			varchar(20) not null,
	 type			varchar(10),
	 author			varchar(20) not null,
	 category		varchar(20),
	 size			int(100),
	 description	varchar(100) not null,
	 last_updated   timestamp default current_timestamp on update current_timestamp,

	 primary key(name, author),
	 foreign key(author) references provider(id) on delete cascade
	);

create table history
	(
	 user_id				varchar(20) not null,
	 provider_id			varchar(20) not null,
	 item_name 				varchar(20) not null,
	 price					float(100, 5) not null,
	 time					timestamp default current_timestamp,
	 foreign key(user_id) references user(id) on delete cascade,
	 foreign key(provider_id) references provider(id) on delete cascade,
	 foreign key(item_name) references item(name) on delete cascade on update cascade
	);

create table prereq
	(
	 name			varchar(20) not null,
	 author			varchar(20) not null,
	 architecture	varchar(20) not null,
	 os				varchar(20) not null,
	 foreign key(name) references item(name) on delete cascade on update cascade,
	 foreign key(author) references provider(id) on delete cascade
	);

create table purged
	(
	 provider_id 			varchar(20) not null,
	 item_name				varchar(20) not null,
	 purged_time			timestamp default current_timestamp,
	 foreign key(provider_id) references provider(id) on delete cascade
	);


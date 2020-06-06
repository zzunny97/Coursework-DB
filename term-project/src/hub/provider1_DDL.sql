create table user
	(
	 id				varchar(20),
	 password		varchar(20),
	 name 			varchar(20),
	 address  		varchar(20),
	 account_number varchar(20),
	 phone_number 	varchar(20),
	 birthday    	varchar(20),

	 date_joined 	date default current_date,
	 start_date 	date default current_date,
	 end_date		date default date_add(current_date(), interval 1 month),
	 primary key(id)
	);

create table provider
	(
	 id				varchar(20),
	 password		varchar(20),
	 name 			varchar(20),
	 address  		varchar(20),
	 account_number varchar(20),
	 phone_number 	varchar(20),
	 birthday    	varchar(20),

	 date_joined 	date default current_date,
	 start_date 	date default current_date,
	 end_date		date default date_add(current_date(), interval 1 month),
	 primary key(id)
	);

create table item
	(
	 name			varchar(20),
	 type			varchar(10),
	 author			varchar(20),
	 category		varchar(20),
	 architecture	varchar(20),
	 os				varchar(10),
	 size			int(100),
	 description	varchar(100),
	 last_updated   timestamp default current_timestamp on update current_timestamp,

	 primary key(name, author),
	 foreign key(author) references provider(id) on delete cascade
	);

create table user_bill
	(
	 id				varchar(20),
	 start_date 	date default current_date,
	 end_date		date default date_add(current_date(), interval 1 month),
	 subscription_fee int(100) default 10,
	 amount_due_provider 	int(100) default 0,
	 foreign key(id) references user(id) on delete cascade
	);

create table provider_bill
	(
	 id				varchar(20),
	 start_date 	date default current_date,
	 end_date		date default date_add(current_date(), interval 1 month),

	 joining_fee	int(100) default 20,
	 earn 			float(100, 5) default 0,
	 amount_due_admin		float(100,5) default 0,

	 foreign key(id) references provider(id) on delete cascade
	);

create table history
	(
	 user_id				varchar(20),
	 provider_id			varchar(20),
	 item_name 				varchar(20),
	 price					float(100, 5),
	 time					timestamp default current_timestamp,
	 foreign key(user_id) references user(id) on delete cascade,
	 foreign key(provider_id) references provider(id) on delete cascade,
	 foreign key(item_name) references item(name) on delete cascade on update cascade
	);

	delimiter #
	create trigger user_trigger after insert on user
	for each row
	begin
	insert into user_bill(id) values (new.id);
	end#
	delimiter ;

	delimiter #
	create trigger provider_trigger after insert on provider
	for each row
	begin
	insert into provider_bill(id) values (new.id);
	end#
	delimiter ;


CREATE TABLE message_board (
	id serial primary key,
	nickname varchar(40) NOT NULL,
	message text NOT NULL,
	post_date timestamp default current_timestamp
);

DROP TABLE IF EXISTS message_board; 
CREATE TABLE message_board (
	id serial primary key,
	nickname varchar(40) COLLATE "C" NOT NULL,
	message text COLLATE "C" NOT NULL,
	post_date timestamp default current_timestamp
);

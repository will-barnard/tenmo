BEGIN TRANSACTION;

DROP TABLE IF EXISTS messages, transfer_comments, friends, transfer_log, tenmo_user, account;

DROP SEQUENCE IF EXISTS seq_message_id, seq_comment_id, seq_transfer_id, seq_user_id, seq_account_id;

-- Sequence to start user_id values at 1001 instead of 1
CREATE SEQUENCE seq_user_id
  INCREMENT BY 1
  START WITH 1001
  NO MAXVALUE;

CREATE TABLE tenmo_user (
	user_id int NOT NULL DEFAULT nextval('seq_user_id'),
	username varchar(50) NOT NULL,
	password_hash varchar(200) NOT NULL,
	CONSTRAINT PK_tenmo_user PRIMARY KEY (user_id),
	CONSTRAINT UQ_username UNIQUE (username)
);

-- Sequence to start account_id values at 2001 instead of 1
-- Note: Use similar sequences with unique starting values for additional tables
CREATE SEQUENCE seq_account_id
  INCREMENT BY 1
  START WITH 2001
  NO MAXVALUE;

CREATE TABLE account (
	account_id int NOT NULL DEFAULT nextval('seq_account_id'),
	user_id int NOT NULL,
	balance decimal(13, 2) NOT NULL,
	CONSTRAINT PK_account PRIMARY KEY (account_id),
	CONSTRAINT FK_account_tenmo_user FOREIGN KEY (user_id) REFERENCES tenmo_user (user_id)
);

CREATE SEQUENCE seq_transfer_id
  INCREMENT BY 1
  START WITH 3001
  NO MAXVALUE;

CREATE TABLE transfer_log (
	transfer_id int NOT NULL DEFAULT nextval('seq_transfer_id'),
	sender_id int NOT NULL REFERENCES tenmo_user(user_id),
	receiver_id int NOT NULL REFERENCES tenmo_user(user_id),
	transfer_amount decimal(13, 2) NOT NULL,
	send_time timestamp NOT NULL,
	receive_time timestamp,
	is_completed boolean NOT NULL,
	is_rejected boolean NOT NULL DEFAULT false,
	CONSTRAINT PK_transfer_log PRIMARY KEY (transfer_id)
);

CREATE TABLE friends (
	user_a int not null references tenmo_user(user_id),
	user_b int not null references tenmo_user(user_id),
	is_confirmed boolean not null,
	is_active boolean not null,
	CONSTRAINT PK_friends PRIMARY KEY (user_a, user_b)
);

CREATE SEQUENCE seq_comment_id
  INCREMENT BY 1
  START WITH 4001
  NO MAXVALUE;
  
CREATE TABLE transfer_comments (
	comment_id int not null DEFAULT nextval('seq_comment_id'),
	transfer_id int not null references transfer_log(transfer_id),
	commenter_id int not null references tenmo_user(user_id),
	comment_content varchar(200) not null,
	comment_time timestamp not null,	
	CONSTRAINT PK_transfer_comments primary key (comment_id)
);

CREATE SEQUENCE seq_message_id
  INCREMENT BY 1
  START WITH 5001
  NO MAXVALUE;

CREATE TABLE messages (
	message_id int not null DEFAULT nextval('seq_message_id'),
	user_a int not null references tenmo_user(user_id),
	user_b int not null references tenmo_user(user_id),
	messager_id int not null references tenmo_user(user_id),
	message_content varchar(200) not null,
	message_time timestamp not null,
	CONSTRAINT PK_messages primary key (message_id)
);

COMMIT;

create table fxschema.config_data(
	id				smallint		not null PRIMARY KEY,
	param_name		varchar(30)		not null,
	param_value		varchar(200)	not null
);

create table fxschema.currency_writed(
	id				smallint		not null PRIMARY KEY,
	write_time		timestamp		not null,
	symbol_list_id	smallint		not null
					REFERENCES fxschema.currency_symbol(id),
	time_frame_id	smallint		not null
					REFERENCES fxschema.time_frame(id)
);

create table fxschema.currency_symbol(
	id				smallint		not null PRIMARY KEY,
	symbol_name		varchar(10)	not null,
	is_active		boolean			not null,
	m5_tab_nr		smallint		not null
);

create table fxschema.data_m5_1(
	id				integer			not null PRIMARY KEY,
	bar_time		timestamp		not null,
	bar_low			numeric(10,5)	not null,
	bar_high		numeric(10,5)	not null,
	bar_close		numeric(10,5)	not null,
	bar_volume		integer			not null,
	ima_count		numeric(10,5)	not null,
	symbol_list_id	smallint		not null
					REFERENCES fxschema.currency_symbol(id)
);

create table fxschema.time_frame(
	id				smallint		not null PRIMARY KEY,
	time_frame		smallint		not null,
	time_frame_desc varchar(10)		not null,
	is_active		boolean			not null
);



CREATE SEQUENCE fxschema.currency_writed_seq 
	INCREMENT BY 1
    START WITH 1
	CACHE 100
	CYCLE;
	
CREATE SEQUENCE fxschema.data_m5_1_seq 
	INCREMENT BY 1
    START WITH 1
	CACHE 100
	CYCLE;

--nextval('fxschema.config_data_seq')

insert into fxschema.currency_symbol(id, symbol_name, is_active, m5_tab_nr)
values (1, 'EURUSD', true, 1);

insert into fxschema.config_data(id, param_name, param_value)
values (1, 'MT4_PATH', 'c:\Users\dgawinkowski\AppData\Roaming\MetaQuotes\Terminal\BEF0A9F90269E8DF733D1FE584305AC7\MQL4\Files');



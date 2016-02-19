
create table fxschema.config_data(
	id				smallint		not null PRIMARY KEY,
	param_name		varchar(30)		not null,
	param_value		varchar(200)	not null
);

create table fxschema.currency_symbol(
	id				smallint		not null PRIMARY KEY,
	symbol_name		varchar(10)		not null,
	is_active		boolean			not null,
	table_name		varchar(10)		not null
);

create table fxschema.data_m5(
	id					integer			not null PRIMARY KEY,
	bar_time			timestamp		not null,
	bar_low				numeric(10,5)	not null,
	bar_high			numeric(10,5)	not null,
	bar_close			numeric(10,5)	not null,
	bar_volume			integer			not null,
	ima_count			numeric(10,5)	not null,
	indicator_nr		integer			null,
	indicator_weight	integer			null,
	is_confirm			boolean			null,
	trend_indicator		varchar(1)		null,
	trend_weight		integer			null,
	process_phase		integer			not null,
	symbol_id			smallint		not null
						REFERENCES fxschema.currency_symbol(id)
);
create unique index data_m5_idx ON fxschema.data_m5 (bar_time, symbol_id);

create table fxschema.data_m15(
	id					integer			not null PRIMARY KEY,
	bar_time			timestamp		not null,
	bar_low				numeric(10,5)	not null,
	bar_high			numeric(10,5)	not null,
	bar_close			numeric(10,5)	not null,
	bar_volume			integer			not null,
	ima_count			numeric(10,5)	not null,
	indicator_nr		integer			null,
	indicator_weight	integer			null,
	is_confirm			boolean			null,
	trend_indicator		varchar(1)		null,
	trend_weight		integer			null,
	process_phase		integer			not null,
	symbol_id			smallint		not null
						REFERENCES fxschema.currency_symbol(id)
);
create unique index data_m15_idx ON fxschema.data_m15 (bar_time, symbol_id);

create table fxschema.data_h1(
	id					integer			not null PRIMARY KEY,
	bar_time			timestamp		not null,
	bar_low				numeric(10,5)	not null,
	bar_high			numeric(10,5)	not null,
	bar_close			numeric(10,5)	not null,
	bar_volume			integer			not null,
	ima_count			numeric(10,5)	not null,
	indicator_nr		integer			null,
	indicator_weight	integer			null,
	is_confirm			boolean			null,
	trend_indicator		varchar(1)		null,
	trend_weight		integer			null,
	process_phase		integer			not null,
	symbol_id			smallint		not null
						REFERENCES fxschema.currency_symbol(id)
);
create unique index data_h1_idx ON fxschema.data_h1 (bar_time, symbol_id);

create table fxschema.time_frame(
	id				smallint		not null PRIMARY KEY,
	time_frame		integer			not null,
	time_frame_desc varchar(10)		not null,
	is_active		boolean			not null
);


	
CREATE SEQUENCE fxschema.data_m5_seq 
	INCREMENT BY 1
    START WITH 1
	CACHE 100
	CYCLE;
CREATE SEQUENCE fxschema.data_m15_seq 
	INCREMENT BY 1
    START WITH 1
	CACHE 100
	CYCLE;
CREATE SEQUENCE fxschema.data_h1_seq 
	INCREMENT BY 1
    START WITH 1
	CACHE 100
	CYCLE;

--nextval('fxschema.config_data_seq')

insert into fxschema.currency_symbol(id, symbol_name, is_active, table_name)
values (1, 'EURUSD', false, 'EURUSD');
insert into fxschema.currency_symbol(id, symbol_name, is_active, table_name)
values (2, 'GOLD', true, 'GOLD');
insert into fxschema.currency_symbol(id, symbol_name, is_active, table_name)
values (3, 'GBPUSD', false, 'GBPUSD');
insert into fxschema.currency_symbol(id, symbol_name, is_active, table_name)
values (4, 'AUDUSD', false, 'AUDUSD');


insert into fxschema.time_frame(id, time_frame, time_frame_desc, is_active)
values (1, 1, 'M1', false);
insert into fxschema.time_frame(id, time_frame, time_frame_desc, is_active)
values (2, 5, 'M5', true);
insert into fxschema.time_frame(id, time_frame, time_frame_desc, is_active)
values (3, 15, 'M15', false);
insert into fxschema.time_frame(id, time_frame, time_frame_desc, is_active)
values (4, 30, 'M30', false);
insert into fxschema.time_frame(id, time_frame, time_frame_desc, is_active)
values (5, 60, 'H1', false);
insert into fxschema.time_frame(id, time_frame, time_frame_desc, is_active)
values (6, 240, 'H4', false);
insert into fxschema.time_frame(id, time_frame, time_frame_desc, is_active)
values (7, 1440, 'D1', false);
insert into fxschema.time_frame(id, time_frame, time_frame_desc, is_active)
values (8, 10080, 'W1', false);
insert into fxschema.time_frame(id, time_frame, time_frame_desc, is_active)
values (9, 43200, 'MN1', false);

insert into fxschema.config_data(id, param_name, param_value)
values (1, 'MT4_PATH', 'c:\Users\dgawinkowski\AppData\Roaming\MetaQuotes\Terminal\BEF0A9F90269E8DF733D1FE584305AC7\MQL4\Files\Actual');
insert into fxschema.config_data(id, param_name, param_value)
values (2, 'HOUR_SHIFT', '-1');

alter table fxschema.data_m5  ADD COLUMN trend_weight integer;
alter table fxschema.data_m15 ADD COLUMN trend_weight integer;
alter table fxschema.data_h1  ADD COLUMN trend_weight integer;


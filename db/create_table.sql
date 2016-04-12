
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
	bar_type			varchar(1)		null,
	indicator_nr		integer			null,
	indicator_weight	integer			null,
	is_confirm			boolean			null,
	trend_indicator		varchar(1)		null,
	trend_weight		integer			null,
	volume_thermometer	varchar(1)		null,
	volume_absorb		integer			null,
	volume_size			varchar(2)		null,
	spread_size			varchar(2)		null,
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
	bar_type			varchar(1)		null,	
	indicator_nr		integer			null,
	indicator_weight	integer			null,
	is_confirm			boolean			null,
	trend_indicator		varchar(1)		null,
	trend_weight		integer			null,
	volume_thermometer	varchar(1)		null,
	volume_absorb		integer			null,
	volume_size			varchar(2)		null,
	spread_size			varchar(2)		null,
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
	bar_type			varchar(1)		null,
	indicator_nr		integer			null,
	indicator_weight	integer			null,
	is_confirm			boolean			null,
	trend_indicator		varchar(1)		null,
	trend_weight		integer			null,
	volume_thermometer	varchar(1)		null,
	volume_absorb		integer			null,
	volume_size			varchar(2)		null,
	spread_size			varchar(2)		null,
	process_phase		integer			not null,
	symbol_id			smallint		not null
						REFERENCES fxschema.currency_symbol(id)
);
create unique index data_h1_idx ON fxschema.data_h1 (bar_time, symbol_id);

create table fxschema.data_h4(
	id					integer			not null PRIMARY KEY,
	bar_time			timestamp		not null,
	bar_low				numeric(10,5)	not null,
	bar_high			numeric(10,5)	not null,
	bar_close			numeric(10,5)	not null,
	bar_volume			integer			not null,
	ima_count			numeric(10,5)	not null,
	bar_type			varchar(1)		null,
	indicator_nr		integer			null,
	indicator_weight	integer			null,
	is_confirm			boolean			null,
	trend_indicator		varchar(1)		null,
	trend_weight		integer			null,
	volume_thermometer	varchar(1)		null,
	volume_absorb		integer			null,
	volume_size			varchar(2)		null,
	spread_size			varchar(2)		null,
	process_phase		integer			not null,
	symbol_id			smallint		not null
						REFERENCES fxschema.currency_symbol(id)
);
create unique index data_h4_idx ON fxschema.data_h4 (bar_time, symbol_id);

create table fxschema.time_frame(
	id				smallint		not null PRIMARY KEY,
	time_frame		integer			not null,
	time_frame_desc varchar(10)		not null,
	is_active		boolean			not null
);

create table fxschema.arch_data_m5_1(
	id					integer			not null PRIMARY KEY,
	bar_time			timestamp		not null,
	bar_low				numeric(10,5)	not null,
	bar_high			numeric(10,5)	not null,
	bar_close			numeric(10,5)	not null,
	bar_volume			integer			not null,
	ima_count			numeric(10,5)	not null,
	bar_type			varchar(1)		null,
	indicator_nr		integer			null,
	indicator_weight	integer			null,
	is_confirm			boolean			null,
	trend_indicator		varchar(1)		null,
	trend_weight		integer			null,
	volume_thermometer	varchar(1)		null,
	volume_absorb		integer			null,
	volume_size			varchar(2)		null,
	spread_size			varchar(2)		null,
	process_phase		integer			not null,
	symbol_id			smallint		not null
						REFERENCES fxschema.currency_symbol(id)
);
create unique index arch_data_m5_1_idx ON fxschema.arch_data_m5_1 (bar_time, symbol_id);

create table fxschema.arch_data_m15_1(
	id					integer			not null PRIMARY KEY,
	bar_time			timestamp		not null,
	bar_low				numeric(10,5)	not null,
	bar_high			numeric(10,5)	not null,
	bar_close			numeric(10,5)	not null,
	bar_volume			integer			not null,
	ima_count			numeric(10,5)	not null,
	bar_type			varchar(1)		null,	
	indicator_nr		integer			null,
	indicator_weight	integer			null,
	is_confirm			boolean			null,
	trend_indicator		varchar(1)		null,
	trend_weight		integer			null,
	volume_thermometer	varchar(1)		null,
	volume_absorb		integer			null,
	volume_size			varchar(2)		null,
	spread_size			varchar(2)		null,
	process_phase		integer			not null,
	symbol_id			smallint		not null
						REFERENCES fxschema.currency_symbol(id)
);
create unique index arch_data_m15_1_idx ON fxschema.arch_data_m15_1 (bar_time, symbol_id);

create table fxschema.arch_data_h1_1(
	id					integer			not null PRIMARY KEY,
	bar_time			timestamp		not null,
	bar_low				numeric(10,5)	not null,
	bar_high			numeric(10,5)	not null,
	bar_close			numeric(10,5)	not null,
	bar_volume			integer			not null,
	ima_count			numeric(10,5)	not null,
	bar_type			varchar(1)		null,
	indicator_nr		integer			null,
	indicator_weight	integer			null,
	is_confirm			boolean			null,
	trend_indicator		varchar(1)		null,
	trend_weight		integer			null,
	volume_thermometer	varchar(1)		null,
	volume_absorb		integer			null,
	volume_size			varchar(2)		null,
	spread_size			varchar(2)		null,
	process_phase		integer			not null,
	symbol_id			smallint		not null
						REFERENCES fxschema.currency_symbol(id)
);
create unique index arch_data_h1_1_idx ON fxschema.arch_data_h1_1 (bar_time, symbol_id);

create table fxschema.trade_alert(
	id					integer			not null PRIMARY KEY,
	alert_time			timestamp		not null,
	alert_message		varchar(500)	not null,
	symbol_id			smallint		not null
						REFERENCES fxschema.currency_symbol(id)
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
CREATE SEQUENCE fxschema.data_h4_seq 
	INCREMENT BY 1
    START WITH 1
	CACHE 100
	CYCLE;
CREATE SEQUENCE fxschema.trade_alert_seq 
	INCREMENT BY 1
    START WITH 1
	CACHE 10
	CYCLE;

--nextval('fxschema.config_data_seq')

insert into fxschema.currency_symbol(id, symbol_name, is_active, table_name)
values (1, 'EURUSD', true, 'EURUSD');
insert into fxschema.currency_symbol(id, symbol_name, is_active, table_name)
values (2, 'GOLD', true, 'GOLD');
insert into fxschema.currency_symbol(id, symbol_name, is_active, table_name)
values (3, 'GBPUSD', true, 'GBPUSD');
insert into fxschema.currency_symbol(id, symbol_name, is_active, table_name)
values (4, 'AUDUSD', true, 'AUDUSD');
insert into fxschema.currency_symbol(id, symbol_name, is_active, table_name)
values (5, 'OIL', true, 'OIL');
insert into fxschema.currency_symbol(id, symbol_name, is_active, table_name)
values (6, 'USDJPY', true, 'USDJPY');
insert into fxschema.currency_symbol(id, symbol_name, is_active, table_name)
values (7, 'USDCHF', true, 'USDCHF');
insert into fxschema.currency_symbol(id, symbol_name, is_active, table_name)
values (8, 'USDCAD', true, 'USDCAD');
insert into fxschema.currency_symbol(id, symbol_name, is_active, table_name)
values (9, 'GBPCAD', true, 'GBPCAD');
insert into fxschema.currency_symbol(id, symbol_name, is_active, table_name)
values (10, 'GBPAUD', true, 'GBPAUD');


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
values (2, 'HOUR_SHIFT', '1');
insert into fxschema.config_data(id, param_name, param_value)
values (3, 'ANALYSE_END_DATE', '2016/02/01 08:00');
insert into fxschema.config_data(id, param_name, param_value)
values (4, 'ANALYSE_BAR_COUNT', '100');
insert into fxschema.config_data(id, param_name, param_value)
values (5, 'VISIBILITY_END_DATE', '2016/02/01 08:00');
insert into fxschema.config_data(id, param_name, param_value)
values (6, 'VISIBILITY_BAR_COUNT', '25');
insert into fxschema.config_data(id, param_name, param_value)
values (11, 'IS_PROCESS_TREND', '1');
insert into fxschema.config_data(id, param_name, param_value)
values (12, 'IS_PROCESS_VOLUME', '1');
insert into fxschema.config_data(id, param_name, param_value)
values (13, 'IS_PROCESS_INDICATOR', '1');
insert into fxschema.config_data(id, param_name, param_value)
values (14, 'IS_BATCH_ANALYSE', '1');
insert into fxschema.config_data(id, param_name, param_value)
values (15, 'IS_BATCH_REWRITE', '1');
insert into fxschema.config_data(id, param_name, param_value)
values (16, 'IS_BATCH_BACKUP', '1');
insert into fxschema.config_data(id, param_name, param_value)
values (17, 'M5_DAYS_STAY', '14');
insert into fxschema.config_data(id, param_name, param_value)
values (18, 'M15_DAYS_STAY', '21');
insert into fxschema.config_data(id, param_name, param_value)
values (19, 'H1_DAYS_STAY', '60');
insert into fxschema.config_data(id, param_name, param_value)
values (20, 'M5_TABLE_NR', '1');
insert into fxschema.config_data(id, param_name, param_value)
values (21, 'M15_TABLE_NR', '1');
insert into fxschema.config_data(id, param_name, param_value)
values (22, 'H1_TABLE_NR', '1');
insert into fxschema.config_data(id, param_name, param_value)
values (23, 'IS_BATCH_TRADE_ALERT', '0');



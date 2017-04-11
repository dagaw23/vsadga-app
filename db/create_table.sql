
create table fxschema.config_data(
	id				smallint		not null PRIMARY KEY,
	param_name		varchar(30)		not null,
	param_value		varchar(200)	not null
);

create table fxschema.currency_symbol(
	id				smallint		not null PRIMARY KEY,
	symbol_name		varchar(10)		not null,
	is_active		boolean			not null,
	futures_symbol	varchar(10)		null
);

create table fxschema.data_m5(
	id					integer			not null PRIMARY KEY,
	bar_time			timestamp		not null,
	bar_low				numeric(10,5)	not null,
	bar_high			numeric(10,5)	not null,
	bar_close			numeric(10,5)	not null,
	bar_volume			integer			not null,
	ima_count			numeric(10,5)	not null,
	volume_type			varchar(1)		not null,
	bar_type			varchar(1)		null,
	indicator_nr		integer			null,
	is_confirm			boolean			null,
	trend_indicator		varchar(1)		null,
	trend_weight		integer			null,
	volume_absorb		integer			null,
	volume_size			varchar(2)		null,
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
	volume_type			varchar(1)		not null,
	bar_type			varchar(1)		null,	
	indicator_nr		integer			null,
	is_confirm			boolean			null,
	trend_indicator		varchar(1)		null,
	trend_weight		integer			null,
	volume_absorb		integer			null,
	volume_size			varchar(2)		null,
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
	volume_type			varchar(1)		not null,
	bar_type			varchar(1)		null,
	indicator_nr		integer			null,
	is_confirm			boolean			null,
	trend_indicator		varchar(1)		null,
	trend_weight		integer			null,
	volume_absorb		integer			null,
	volume_size			varchar(2)		null,
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
	volume_type			varchar(1)		not null,
	bar_type			varchar(1)		null,
	indicator_nr		integer			null,
	is_confirm			boolean			null,
	trend_indicator		varchar(1)		null,
	trend_weight		integer			null,
	volume_absorb		integer			null,
	volume_size			varchar(2)		null,
	process_phase		integer			not null,
	symbol_id			smallint		not null
						REFERENCES fxschema.currency_symbol(id)
);
create unique index data_h4_idx ON fxschema.data_h4 (bar_time, symbol_id);

create table fxschema.data_d1(
	id					integer			not null PRIMARY KEY,
	bar_time			timestamp		not null,
	bar_low				numeric(10,5)	not null,
	bar_high			numeric(10,5)	not null,
	bar_close			numeric(10,5)	not null,
	bar_volume			integer			not null,
	ima_count			numeric(10,5)	not null,
	volume_type			varchar(1)		not null,
	bar_type			varchar(1)		null,
	indicator_nr		integer			null,
	is_confirm			boolean			null,
	trend_indicator		varchar(1)		null,
	trend_weight		integer			null,
	volume_absorb		integer			null,
	volume_size			varchar(2)		null,
	process_phase		integer			not null,
	symbol_id			smallint		not null
						REFERENCES fxschema.currency_symbol(id)
);
create unique index data_d1_idx ON fxschema.data_d1 (bar_time, symbol_id);

create table fxschema.data_w1(
	id					integer			not null PRIMARY KEY,
	bar_time			timestamp		not null,
	bar_low				numeric(10,5)	not null,
	bar_high			numeric(10,5)	not null,
	bar_close			numeric(10,5)	not null,
	bar_volume			integer			not null,
	ima_count			numeric(10,5)	not null,
	volume_type			varchar(1)		not null,
	bar_type			varchar(1)		null,
	indicator_nr		integer			null,
	is_confirm			boolean			null,
	trend_indicator		varchar(1)		null,
	trend_weight		integer			null,
	volume_absorb		integer			null,
	volume_size			varchar(2)		null,
	process_phase		integer			not null,
	symbol_id			smallint		not null
						REFERENCES fxschema.currency_symbol(id)
);
create unique index data_w1_idx ON fxschema.data_w1 (bar_time, symbol_id);

create table fxschema.time_frame(
	id					smallint		not null PRIMARY KEY,
	time_frame			integer			not null,
	time_frame_desc 	varchar(10)		not null,
	is_file_frame		boolean			not null,
	is_logical_frame	boolean			not null,
	is_active			boolean			not null
);

create table fxschema.arch_data_m5_1(
	id					integer			not null PRIMARY KEY,
	bar_time			timestamp		not null,
	bar_low				numeric(10,5)	not null,
	bar_high			numeric(10,5)	not null,
	bar_close			numeric(10,5)	not null,
	bar_volume			integer			not null,
	ima_count			numeric(10,5)	not null,
	volume_type			varchar(1)		not null,
	bar_type			varchar(1)		null,
	indicator_nr		integer			null,
	is_confirm			boolean			null,
	trend_indicator		varchar(1)		null,
	trend_weight		integer			null,
	volume_absorb		integer			null,
	volume_size			varchar(2)		null,
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
	volume_type			varchar(1)		not null,
	bar_type			varchar(1)		null,	
	indicator_nr		integer			null,
	is_confirm			boolean			null,
	trend_indicator		varchar(1)		null,
	trend_weight		integer			null,
	volume_absorb		integer			null,
	volume_size			varchar(2)		null,
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
	volume_type			varchar(1)		not null,
	bar_type			varchar(1)		null,
	indicator_nr		integer			null,
	is_confirm			boolean			null,
	trend_indicator		varchar(1)		null,
	trend_weight		integer			null,
	volume_absorb		integer			null,
	volume_size			varchar(2)		null,
	process_phase		integer			not null,
	symbol_id			smallint		not null
						REFERENCES fxschema.currency_symbol(id)
);
create unique index arch_data_h1_1_idx ON fxschema.arch_data_h1_1 (bar_time, symbol_id);

create table fxschema.trade_alert(
	id					integer			not null PRIMARY KEY,
	alert_time			timestamp		not null,
	alert_message		varchar(500)	not null,
	alert_type			varchar(1)		not null,	
	symbol_id			smallint		not null
						REFERENCES fxschema.currency_symbol(id),
	time_frame_id		smallint		not null
						REFERENCES fxschema.time_frame(id),
	bar_time			timestamp		not null,
	bar_status			varchar(1)		not null
);
create unique index trade_alert_idx ON fxschema.trade_alert (bar_time, symbol_id, time_frame_id, alert_type);

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
CREATE SEQUENCE fxschema.data_d1_seq 
	INCREMENT BY 1
    START WITH 1
	CACHE 100
	CYCLE;
CREATE SEQUENCE fxschema.data_w1_seq 
	INCREMENT BY 1
    START WITH 1
	CACHE 10
	CYCLE;
CREATE SEQUENCE fxschema.trade_alert_seq 
	INCREMENT BY 1
    START WITH 1
	CACHE 10
	CYCLE;

--nextval('fxschema.config_data_seq')

insert into fxschema.currency_symbol(id, symbol_name, is_active, futures_symbol)
values (1, 'AUDUSD', true, '6A');
insert into fxschema.currency_symbol(id, symbol_name, is_active, futures_symbol)
values (2, 'GBPUSD', true, '6B');
insert into fxschema.currency_symbol(id, symbol_name, is_active, futures_symbol)
values (3, 'USDCAD', true, '6C');
insert into fxschema.currency_symbol(id, symbol_name, is_active, futures_symbol)
values (4, 'EURUSD', true, '6E');
insert into fxschema.currency_symbol(id, symbol_name, is_active, futures_symbol)
values (5, 'USDJPY', true, '6J');
insert into fxschema.currency_symbol(id, symbol_name, is_active, futures_symbol)
values (6, 'NZDUSD', true, '6N');
insert into fxschema.currency_symbol(id, symbol_name, is_active, futures_symbol)
values (7, 'USDCHF', true, '6S');
insert into fxschema.currency_symbol(id, symbol_name, is_active, futures_symbol)
values (8, 'OIL', true, 'CL');
insert into fxschema.currency_symbol(id, symbol_name, is_active, futures_symbol)
values (9, 'GOLD', true, 'GC');
insert into fxschema.currency_symbol(id, symbol_name, is_active, futures_symbol)
values (10, 'SILVER', true, 'SI');
insert into fxschema.currency_symbol(id, symbol_name, is_active, futures_symbol)
values (11, 'US500', true, 'ES');
insert into fxschema.currency_symbol(id, symbol_name, is_active, futures_symbol)
values (12, 'GER30', true, 'FDAX');


insert into fxschema.time_frame(id, time_frame, time_frame_desc, is_active)
values (1, 1, 'M1', false);
insert into fxschema.time_frame(id, time_frame, time_frame_desc, is_active)
values (2, 5, 'M5', true);
insert into fxschema.time_frame(id, time_frame, time_frame_desc, is_active)
values (3, 15, 'M15', true);
insert into fxschema.time_frame(id, time_frame, time_frame_desc, is_active)
values (4, 30, 'M30', false);
insert into fxschema.time_frame(id, time_frame, time_frame_desc, is_active)
values (5, 60, 'H1', true);
insert into fxschema.time_frame(id, time_frame, time_frame_desc, is_active)
values (6, 240, 'H4', true);
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
insert into fxschema.config_data(id, param_name, param_value)
values (24, 'M5_LEVELS', '50,100,200,500');
insert into fxschema.config_data(id, param_name, param_value)
values (25, 'M15_LEVELS', '32,64,160,320');
insert into fxschema.config_data(id, param_name, param_value)
values (26, 'H1_LEVELS', '25,50,100,200');
insert into fxschema.config_data(id, param_name, param_value)
values (27, 'H4_LEVELS', '25,50,100,200');
insert into fxschema.config_data(id, param_name, param_value)
values (28, 'ACCESS_KEY', '123');
insert into fxschema.config_data(id, param_name, param_value)
values (29, 'IS_HTTP_PROXY', '1');
insert into fxschema.config_data(id, param_name, param_value)
values (30, 'HTTP_PROXY_HOST', 'proxy');
insert into fxschema.config_data(id, param_name, param_value)
values (31, 'IS_BATCH_REPORT_PRINTER', '1');
insert into fxschema.config_data(id, param_name, param_value)
values (32, 'CHART_JPG_WRITE_PATH', '/Workspace/vsadga-workspace/work/');
insert into fxschema.config_data(id, param_name, param_value)
values (33, 'IS_BATCH_DATA_CALCULATE', '1');
insert into fxschema.config_data(id, param_name, param_value)
values (34, 'DATA_CALCULATE_MODE', 'ALL');
insert into fxschema.config_data(id, param_name, param_value)
values (35, 'CHART_PDF_WRITE_PATH', '/Workspace/vsadga-workspace/reports/');
insert into fxschema.config_data(id, param_name, param_value)
values (36, 'JASPER_XML_PATH', '/Workspace/vsadga-workspace/jreports/chartreport.jrxml');
insert into fxschema.config_data(id, param_name, param_value)
values (37, 'CHART_BAR_COUNT_D1', '80');
insert into fxschema.config_data(id, param_name, param_value)
values (38, 'CHART_BAR_COUNT_H4', '90');
insert into fxschema.config_data(id, param_name, param_value)
values (39, 'CHART_BAR_COUNT_H1', '100');
insert into fxschema.config_data(id, param_name, param_value)
values (40, 'CHART_BAR_COUNT_M15', '110');
insert into fxschema.config_data(id, param_name, param_value)
values (41, 'CHART_BAR_COUNT_M5', '120');
insert into fxschema.config_data(id, param_name, param_value)
values (42, 'ALERT_BY_VOLUME_SIZE', 'M5:300,M15:120,H1:50,H4:15');


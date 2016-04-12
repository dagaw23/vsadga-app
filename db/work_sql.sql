select * from fxschema.time_frame
order by id

update fxschema.time_frame
set is_active=false
where id=6

select * from fxschema.currency_symbol
order by id

update fxschema.currency_symbol
set is_active=true
where id=2

select * from fxschema.config_data
order by id

--"2016/04/05 21:15"
update fxschema.config_data
set param_value='0'
where id=23

select * from fxschema.data_h4
order by bar_time asc

select * from fxschema.trade_alert
order by alert_time

update fxschema.data_m5
set bar_type=null, indicator_nr=null, indicator_weight=null, 
   is_confirm=null, trend_indicator=null, trend_weight=null, 
   volume_thermometer=null, volume_absorb=null,
   volume_size=null, spread_size=null, process_phase=1


delete from fxschema.data_h4

select * from fxschema.arch_data_h1_1  
order by bar_time desc


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

create table fxschema.trade_alert(
	id					integer			not null PRIMARY KEY,
	alert_time			timestamp		not null,
	alert_message		varchar(500)	not null,
	symbol_id			smallint		not null
						REFERENCES fxschema.currency_symbol(id)
);

CREATE SEQUENCE fxschema.trade_alert_seq 
	INCREMENT BY 1
    START WITH 1
	CACHE 10
	CYCLE;


insert into fxschema.config_data(id, param_name, param_value)
values (23, 'IS_BATCH_TRADE_ALERT', '0');
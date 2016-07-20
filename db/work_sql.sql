select * from fxschema.time_frame
order by is_active desc, time_frame asc

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
set param_value='1'
where id=14

select * from fxschema.data_m5
order by bar_time desc

select * from fxschema.trade_alert
order by alert_time

update fxschema.data_m5
set bar_type=null, indicator_nr=null, indicator_weight=null, 
   is_confirm=null, trend_indicator=null, trend_weight=null, 
   volume_thermometer=null, volume_absorb=null,
   volume_size=null, spread_size=null, process_phase=1


drop table fxschema.data_m5;

select * from fxschema.arch_data_h1_1  
order by bar_time desc


1) 
drop table fxschema.currency_symbol

drop table fxschema.trade_alert

drop table fxschema.arch_data_h1_1

drop table fxschema.arch_data_m15_1

drop table fxschema.arch_data_m5_1

drop table fxschema.data_h4

drop table fxschema.data_h1

drop table fxschema.data_m15

drop table fxschema.data_m5

2) 

create table fxschema.currency_symbol

create table fxschema.data_m5

create table fxschema.data_m15

create table fxschema.data_h1

create table fxschema.data_h4

create table fxschema.data_d1

create table fxschema.data_w1

create table fxschema.arch_data_m5_1

create table fxschema.arch_data_m15_1

create table fxschema.arch_data_h1_1

create table fxschema.trade_alert


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

3) 
insert into fxschema.currency_symbol(id, symbol_name, is_active, futures_symbol)...

4) 

select * from fxschema.config_data
order by id

insert into fxschema.config_data(id, param_name, param_value)
values (28, 'ACCESS_KEY', '123');
insert into fxschema.config_data(id, param_name, param_value)
values (29, 'IS_HTTP_PROXY', '1');
insert into fxschema.config_data(id, param_name, param_value)
values (30, 'HTTP_PROXY_HOST', 'proxy');
insert into fxschema.config_data(id, param_name, param_value)
values (31, 'IS_BATCH_REPORT_PRINTER', '1');
insert into fxschema.config_data(id, param_name, param_value)
values (32, 'CHART_JPG_WRITE_PATH', '/My-workspaces/vsadga-workspace/work');


4H: 6*15
1H: 24*15
15M: 4*24*15
5M:  12*24*15

update fxschema.time_frame
set is_active=false

update fxschema.time_frame
set is_active=true
where time_frame_desc='H4'

update fxschema.currency_symbol
set is_active=true

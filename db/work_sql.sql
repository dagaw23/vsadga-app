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


nowe katalogi:
c:\My-workspaces\vsadga-workspace
 - jreports
 - reports
 - work

 KS Ursynów
 FutbolTalent

5M:  12*24*15

delete files.

4H: 6*17 --> 120
update fxschema.time_frame
set is_active=false

update fxschema.time_frame
set is_active=true
where time_frame_desc='H4'

update fxschema.currency_symbol
set is_active=true


1H: 24*17  --> 420
update fxschema.time_frame
set is_active=false

update fxschema.time_frame
set is_active=true
where time_frame_desc='H1'


15M: 4*24*15  --> 1650
update fxschema.time_frame
set is_active=false

update fxschema.time_frame
set is_active=true
where time_frame_desc='M15'

5M:  12*24*15  --> 1650
update fxschema.time_frame
set is_active=false

update fxschema.time_frame
set is_active=true
where time_frame_desc='M5'

------------------------------
update fxschema.time_frame
set is_active=false

update fxschema.config_data
set param_value='0'
where id=15

update fxschema.currency_symbol
set is_active=false

update fxschema.time_frame
set is_active=true
where time_frame_desc='M5' or time_frame_desc='M15' or time_frame_desc='H1' or time_frame_desc='H4'

update fxschema.config_data
set param_value='1650'
where id=4

update fxschema.config_data
set param_value='1'
where id=14

update fxschema.currency_symbol
set is_active=true
where id=1 or id=2

update fxschema.currency_symbol
set is_active=true
where id=3 or id=4

update fxschema.currency_symbol
set is_active=true
where id=5 or id=6

update fxschema.currency_symbol
set is_active=false

update fxschema.currency_symbol
set is_active=true
where id=7 or id=8

update fxschema.currency_symbol
set is_active=true
where id=9 or id=10

update fxschema.currency_symbol
set is_active=true
where id=11 or id=12

update fxschema.currency_symbol
set is_active=false

update fxschema.config_data
set param_value='100'
where id=4

update fxschema.currency_symbol
set is_active=true

-------------------------------------------------------------------------------


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

-------------------------------------------------------------------------------
PIVOT POINTS - w ujęciu dziennym lub tygodniowym - najlepsze daje rezultaty.

	Pivot point (PP) = (Cena maksymalna + Cena minimalna + Cena zamknięcia) / 3

﻿Pierwsze poziomy wsparcia i oporu:
	﻿Pierwszy opór (R1) = (2 x PP) – Cena minimalna
	﻿Drugie wsparcie (S1) = (2 x PP) – Cena maksymalna

Drugie poziomy wsparcia i oporu:
	﻿Drugi opór (R2) = PP + (Cena maksymalna – Cena minimalna)
	﻿Drugie wsparcie (S2) = PP - (Cena maksymalna – Cena minimalna)

﻿Trzecie poziomy wsparcia i oporu:
	﻿Trzeci opór (R3) = Cena maksymalna + 2(PP – Cena minimalna)
	Trzecie wsparcie (S3) = Cena minimalna - 2(Cena maksymalna – PP)

Pivot_AllLevels - MT4
-------------------------------------------------------------------------------

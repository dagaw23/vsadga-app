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


delete from fxschema.data_m5

select * from fxschema.arch_data_h1_1  
order by bar_time desc


ALTER TABLE fxschema.currency_symbol DROP COLUMN table_name

ALTER TABLE fxschema.currency_symbol ADD COLUMN futures_symbol	varchar(10) null;

update fxschema.currency_symbol set futures_symbol='6e' where symbol_name = 'EURUSD'

update fxschema.currency_symbol set futures_symbol='6b' where symbol_name = 'GBPUSD'

update fxschema.currency_symbol set futures_symbol='6a' where symbol_name = 'AUDUSD'

update fxschema.currency_symbol set futures_symbol='gc' where symbol_name = 'GOLD'

update fxschema.currency_symbol set futures_symbol='cl' where symbol_name = 'OIL'

update fxschema.currency_symbol set futures_symbol='6j' where symbol_name = 'USDJPY'

update fxschema.currency_symbol set futures_symbol='6s' where symbol_name = 'USDCHF'

update fxschema.currency_symbol set futures_symbol='6c' where symbol_name = 'USDCAD'



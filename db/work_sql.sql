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

update fxschema.config_data
set param_value='1'
where id=14

select * from fxschema.data_h4
order by bar_time asc

update fxschema.data_m5
set bar_type=null, indicator_nr=null, indicator_weight=null, 
   is_confirm=null, trend_indicator=null, trend_weight=null, 
   volume_thermometer=null, volume_absorb=null, volume_avg_short=null, 
   volume_avg_medium=null, volume_avg_long=null, process_phase=1



delete from fxschema.data_h4

select * from fxschema.arch_data_h1_1  
order by bar_time desc
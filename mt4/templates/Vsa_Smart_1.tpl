<chart>
id=131054558244435612
symbol=EURUSD
period=60
leftpos=4741
digits=5
scale=16
graph=0
fore=0
grid=1
volume=0
scroll=1
shift=1
ohlc=1
one_click=0
one_click_btn=1
askline=0
days=0
descriptions=0
shift_size=20
fixed_pos=0
window_left=225
window_top=225
window_right=1701
window_bottom=785
window_type=3
background_color=16777215
foreground_color=0
barup_color=0
bardown_color=0
bullcandle_color=16711680
bearcandle_color=255
chartline_color=0
volumes_color=0
grid_color=10061943
askline_color=255
stops_color=255

<window>
height=160
fixed_height=0
<indicator>
name=main
</indicator>
</window>

<window>
height=32
fixed_height=0
<indicator>
name=Custom Indicator
<expert>
name=SmartTradingVolume
flags=339
window_num=1
<inputs>
AccessKey=88301876068826
ExtraData=
</inputs>
</expert>
shift_0=0
draw_0=2
color_0=8519755
style_0=0
weight_0=2
min=0.00000000
period_flags=0
show_data=1
</indicator>
</window>

<window>
height=31
fixed_height=0
<indicator>
name=Custom Indicator
<expert>
name=VsaVolumeAccumulation
flags=339
window_num=2
<inputs>
AccessHttp=http://generatedata.biz/mt4/smart.php
AccessKey=88301876068826
EnableLogging=true
</inputs>
</expert>
shift_0=0
draw_0=2
color_0=3329330
style_0=0
weight_0=3
shift_1=0
draw_1=2
color_1=255
style_1=0
weight_1=3
shift_2=0
draw_2=2
color_2=0
style_2=0
weight_2=3
period_flags=0
show_data=1
</indicator>
</window>
</chart>


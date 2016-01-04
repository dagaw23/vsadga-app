//+------------------------------------------------------------------+
//|                                                      dagaw23.mq4 |
//|                                            Copyright 2015, DaGaw |
//|                                             https://www.mql5.com |
//+------------------------------------------------------------------+
#property copyright "Copyright 2015, DaGaw"
#property link      "https://www.mql5.com"
#property version   "1.00"
#property strict
#property indicator_separate_window
#property indicator_buffers 3

#property indicator_color1 clrRed
#property indicator_color2 clrGreen
#property indicator_color3 clrGray

#property indicator_width1 5
#property indicator_width2 5
#property indicator_width3 5

//--- plot Label1
//#property indicator_label1  "Label1"
//#property indicator_type1   DRAW_HISTOGRAM
//#property indicator_color1  , , 
//#property indicator_style1  STYLE_SOLID
//#property indicator_width1  5
//--- plot Label2
//#property indicator_label2  "Label2"
//#property indicator_type2   DRAW_LINE
//#property indicator_color2  clrBlue
//#property indicator_style2  STYLE_SOLID
//#property indicator_width2  1

//--- indicator buffers
double         ColorBuffer1[];
double         ColorBuffer2[];
double         ColorBuffer3[];

int         down_vol_idx;
int         up_vol_idx;

long         up_vol_1;
long         up_vol_2;
long         up_vol_3;

long         down_vol_1;
long         down_vol_2;
long         down_vol_3;

//+------------------------------------------------------------------+
//| Custom indicator initialization function                         |
//+------------------------------------------------------------------+
int OnInit()
  {
  IndicatorShortName("Volume Themometer");
  
  SetIndexStyle(0, DRAW_HISTOGRAM, 0, 7, clrOrangeRed);
  SetIndexBuffer(0, ColorBuffer1);
  SetIndexStyle(1, DRAW_HISTOGRAM, 0, 7, clrLawnGreen);
  SetIndexBuffer(1, ColorBuffer2);
  SetIndexStyle(2, DRAW_HISTOGRAM, 0, 7, clrSilver);
  SetIndexBuffer(2, ColorBuffer3);
  
  down_vol_idx = 1;
  up_vol_idx = 1;
  up_vol_1 = 0;
  up_vol_2 = 0;
  up_vol_3 = 0;
  down_vol_1 = 0;
  down_vol_2 = 0;
  down_vol_3 = 0;

//--- indicator buffers mapping
  // SetIndexBuffer(0,Label1Buffer);
   //SetIndexBuffer(1,ColorBuffer);
//---
   return(INIT_SUCCEEDED);
  }
//+------------------------------------------------------------------+
//| Custom indicator iteration function                              |
//+------------------------------------------------------------------+

int OnCalculate(const int rates_total,
                const int prev_calculated,
                const datetime &time[],
                const double &open[],
                const double &high[],
                const double &low[],
                const double &close[],
                const long &tick_volume[],
                const long &volume[],
                const int &spread[])
  {
//---
      
   if (rates_total != prev_calculated) {     
      draw(400, close, tick_volume, rates_total);
   }
   
//--- return value of prev_calculated for next call
   return(rates_total);
  }
//+------------------------------------------------------------------+

void draw(int barCount, const double &close[], const long &tick_volume[], const int rates_total)
{
   int i = 0;
   
   if ((barCount + 3) >= rates_total) {
      i = rates_total - 3;
   } else {
      i = barCount;
   }
   
   long up_volume = 0;
   long down_volume = 0;
   
   while(i>0) {
      ColorBuffer1[i] = 0;
      ColorBuffer2[i] = 0;
      ColorBuffer3[i] = 0;
   
      if (close[i+1] < close[i]) { // UP bar
         up_volume = getUpVolume(tick_volume[i]);
      } else if (close[i+1] == close[i]) {
         up_volume = getUpVolume(tick_volume[i]);
         down_volume = getDownVolume(tick_volume[i]);
      } else { // DOWN bar
         down_volume = getDownVolume(tick_volume[i]);
      }
      
      if (up_volume > down_volume) {
         ColorBuffer2[i] = 100;
      } else if (up_volume < down_volume) {
         ColorBuffer1[i] = 100;
      } else {
         ColorBuffer3[i] = 100;
      }
      
      i--;
   }
   
   ColorBuffer1[0] = 1;
}

long getUpVolume(long vol)
{
   int size = 1;
   long sum = 0;
   
   if (up_vol_idx == 1) {
      up_vol_1 = vol;
      up_vol_idx = 2;
   } else if (up_vol_idx == 2) {
      up_vol_2 = vol;
      up_vol_idx = 3;
   } else if (up_vol_idx == 3) {
      up_vol_3 = vol;
      up_vol_idx = 1;
   }
   
   sum = up_vol_1;
   if (up_vol_2 != 0) {
      sum = sum + up_vol_2;
      size = 2;
   }
   if (up_vol_3 != 0) {
      sum = sum + up_vol_3;
      size = 3;
   }
   
   return (sum / size);
}

long getDownVolume(long vol)
{
   int size = 1;
   long sum = 0;
   
   if (down_vol_idx == 1) {
      down_vol_1 = vol;
      down_vol_idx = 2;
   } else if (down_vol_idx == 2) {
      down_vol_2 = vol;
      down_vol_idx = 3;
   } else if (down_vol_idx == 3) {
      down_vol_3 = vol;
      down_vol_idx = 1;
   }
   
   sum = down_vol_1;
   if (down_vol_2 != 0) {
      sum = sum + down_vol_2;
      size = 2;
   }
   if (down_vol_3 != 0) {
      sum = sum + down_vol_3;
      size = 3;
   }
   
   return (sum / size);
}

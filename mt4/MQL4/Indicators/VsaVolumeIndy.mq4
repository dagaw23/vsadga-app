//+------------------------------------------------------------------+
//|                                                VsaVolumeIndy.mq4 |
//|                                            Copyright 2015, DaGaw |
//|                                             https://www.mql5.com |
//+------------------------------------------------------------------+
#property copyright "Copyright 2015, DaGaw"
#property link      "https://www.mql5.com"
#property version   "1.00"
#property strict
#property indicator_separate_window
#property indicator_buffers 4
#property indicator_plots   1
//--- plot DownBar
#property indicator_label1  "DownBar"
#property indicator_type1   DRAW_HISTOGRAM
#property indicator_color1  clrOrangeRed
#property indicator_style1  STYLE_SOLID
#property indicator_width1  3
//--- plot UpBar
#property indicator_label2  "UpBar"
#property indicator_type2   DRAW_HISTOGRAM
#property indicator_color2  clrLimeGreen
#property indicator_style2  STYLE_SOLID
#property indicator_width2  3
//--- plot EqualBar
#property indicator_label3  "EqualBar"
#property indicator_type3   DRAW_HISTOGRAM
#property indicator_color3  clrBlack
#property indicator_style3  STYLE_SOLID
#property indicator_width3  3
//--- plot Less2Bar
#property indicator_label4  "Less2Bar"
#property indicator_type4   DRAW_HISTOGRAM
#property indicator_color4  clrFuchsia
#property indicator_style4  STYLE_SOLID
#property indicator_width4  3
//--- input parameters
input int      Input1;
//--- indicator buffers
double         DownBarBuffer[];
double         UpBarBuffer[];
double         EqualBarBuffer[];
double         Less2BarBuffer[];
//+------------------------------------------------------------------+
//| Custom indicator initialization function                         |
//+------------------------------------------------------------------+
int OnInit()
  {
//--- indicator buffers mapping
   SetIndexBuffer(0,DownBarBuffer);
   SetIndexBuffer(1,UpBarBuffer);
   SetIndexBuffer(2,EqualBarBuffer);
   SetIndexBuffer(3,Less2BarBuffer);
   
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
    //Print("::OnCalculate:: M:",Minute(),"S:",Seconds(), ",", prev_calculated, ",", rates_total, ".");
    
   if (rates_total != prev_calculated) {
      drawVolumes(2000, tick_volume, close);
   } else {
      EqualBarBuffer[0] = tick_volume[0];
   }
   
//--- return value of prev_calculated for next call
   return(rates_total);
  }
//+------------------------------------------------------------------+

void OnTick()
{
   Print("M:",Minute(),"S:",Seconds());
   
}

void drawVolumes(int bar_count, const long &tick_volume[], const double &close[])
{
   int i = bar_count - 2;
   long prev_vol = 0;
   long prev_prev_vol = 0;
   
   while (i >= 0)
   {
      prev_vol = tick_volume[i+1];
      prev_prev_vol = tick_volume[i+2];
      
      if ((tick_volume[i] < prev_vol) && (tick_volume[i] < prev_prev_vol)) {
         Less2BarBuffer[i] = tick_volume[i];
      } else {     
         if (close[i+1] < close[i]) {
            UpBarBuffer[i] = tick_volume[i];
         } else if (close[i+1] > close[i]) {
            DownBarBuffer[i] = tick_volume[i];
         } else {
            EqualBarBuffer[i] = tick_volume[i];
         }
      }  
      i--;
   }
   
   //EqualBarBuffer[0] = tick_volume[0];

}
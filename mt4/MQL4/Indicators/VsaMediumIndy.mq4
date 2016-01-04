//+------------------------------------------------------------------+
//|                                                VsaMediumIndy.mq4 |
//|                                            Copyright 2015, DaGaw |
//|                                             https://www.mql5.com |
//+------------------------------------------------------------------+
#property copyright "Copyright 2015, DaGaw"
#property link      "https://www.mql5.com"
#property version   "1.00"
#property strict
#property indicator_separate_window
#property indicator_buffers 3
#property indicator_plots   1
//--- plot UpBarColor
#property indicator_label1  "UpBarColor"
#property indicator_type1   DRAW_HISTOGRAM
#property indicator_color1  clrLimeGreen
#property indicator_style1  STYLE_SOLID
#property indicator_width1  7
//--- plot DownBarColor
#property indicator_label2  "DownBarColor"
#property indicator_type2   DRAW_HISTOGRAM
#property indicator_color2  clrOrangeRed
#property indicator_style2  STYLE_SOLID
#property indicator_width2  7
//--- plot LevelBarColor
#property indicator_label3  "LevelBarColor"
#property indicator_type3   DRAW_HISTOGRAM
#property indicator_color3  clrBlack
#property indicator_style3  STYLE_SOLID
#property indicator_width3  7
//--- indicator buffers
double         UpBarColorBuffer[];
double         DownBarColorBuffer[];
double         LevelBarColorBuffer[];
//+------------------------------------------------------------------+
//| Custom indicator initialization function                         |
//+------------------------------------------------------------------+
int OnInit()
  {
//--- indicator buffers mapping
   IndicatorShortName("Medium Term Trend");
   
   SetIndexStyle(0, DRAW_HISTOGRAM);
   SetIndexBuffer(0,UpBarColorBuffer);
   SetIndexStyle(1, DRAW_HISTOGRAM);
   SetIndexBuffer(1,DownBarColorBuffer);
   SetIndexStyle(2, DRAW_HISTOGRAM);
   SetIndexBuffer(2,LevelBarColorBuffer);
   
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
      drawMedium(200, close, high, low, rates_total);
   }
   
//--- return value of prev_calculated for next call
   return(rates_total);
  }
//+------------------------------------------------------------------+

void drawMedium(int barCount,
         const double &close[],
         const double &high[],
         const double &low[],
         const int rates_total)
{  
   int i = 0;
   
   if ((barCount + 1) >= rates_total) {
      i = rates_total - 3;
   } else {
      i = barCount;
   }
   
   bool up_move = false;
   int up_count = 0;
   int up_not_yet = 0;
   bool down_move = false;
   int down_not_yet = 0;
   int down_count = 0;
   
   while (i > 0)
   {
      if (high[i+1] > high[i]) {
         down_move = true;
         down_not_yet = 0;
         down_count = down_count + 1;
      } else {
         down_not_yet = down_not_yet + 1;
         
         if (down_not_yet > 1) {
            down_move = false;
            down_count = 0;
         }
      }
      
      if (low[i+1] < low[i]) {
         up_move = true;
         up_not_yet = 0;
         up_count = up_count + 1;
      } else {
         up_not_yet = up_not_yet + 1;
         
         if (up_not_yet > 1) {
            up_move = false;
            up_count = 0;
         }
      }
      
      if (up_move && down_move) {
         if (up_count > down_count) {
            UpBarColorBuffer[i] = 100;
         } else if (down_count > up_count) {
            DownBarColorBuffer[i] = 100;
         } else {
            LevelBarColorBuffer[i] = 100;
         }
      }
      
      if (!up_move && !down_move) {
         LevelBarColorBuffer[i] = 100;
      }
      
      if (up_move) {
         UpBarColorBuffer[i] = 100;
      }
      
      if (down_move) {
         DownBarColorBuffer[i] = 100;
      }
      
      //Alert("up_move:", up_move, ", down_move:", down_move, ",up_count:", up_count, ", down_count:", down_count, ",up_not_yet:", up_not_yet, ",down_not_yet:", down_not_yet);
      
      i--;
   }
   
   LevelBarColorBuffer[0] = 1;
}
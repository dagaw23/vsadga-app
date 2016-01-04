//+------------------------------------------------------------------+
//|                                                        dagaw.mq4 |
//|                                                     dagaw23#2015 |
//|                                             https://www.mql5.com |
//+------------------------------------------------------------------+
#property copyright "dagaw23#2015"
#property link      "https://www.mql5.com"
#property version   "1.00"
#property strict
#property script_show_inputs
//--- input parameters
input int      bar_scan_nr=5;
input string   dir_path="Data";
//+------------------------------------------------------------------+
//| Script program start function                                    |
//+------------------------------------------------------------------+
void OnStart()
  {
//---
   writeSymbol("EURUSD");
   writeSymbol("GBPUSD");
   writeSymbol("AUDUSD");
  }
//+------------------------------------------------------------------+

void writeSymbol(string symbol)
{
   writeSymbol(symbol,PERIOD_H4,  60);
   writeSymbol(symbol,PERIOD_H1, 240);
   writeSymbol(symbol,PERIOD_M15, 96);
}

void writeSymbol(string symbol, int timeframe, int barCount)
{
   datetime time_buff[]; // array of indicator dates
   long vol_buff[];
   double hi_buff[];
   double low_buff[];
   double cls_buff[];
   
   ArraySetAsSeries(time_buff, true);
   ArraySetAsSeries(vol_buff, true);
   ArraySetAsSeries(hi_buff, true);
   ArraySetAsSeries(low_buff, true);
   ArraySetAsSeries(cls_buff, true);
   
   string fle_nam = "FILE_" + symbol + "_" + getTimeframeDesc(timeframe);
   
   // wypelnij tablice:
   CopyTime(symbol, timeframe, 0, barCount, time_buff);
   CopyTickVolume(symbol, timeframe, 0, barCount, vol_buff);
   CopyHigh(symbol, timeframe, 0, barCount, hi_buff);
   CopyLow(symbol, timeframe, 0, barCount, low_buff);
   CopyClose(symbol, timeframe, 0, barCount, cls_buff);
   
   // usun jesli plik istnieje:
   if (FileIsExist(fle_nam))
      FileDelete(fle_nam);
   
   // wpisz dane do pliku:
   ResetLastError();
   int file_handle=FileOpen(dir_path+"//"+fle_nam,FILE_WRITE|FILE_CSV);
   
   if(file_handle!=INVALID_HANDLE)
   {
      PrintFormat("%s file is available for writing.",fle_nam);
      PrintFormat("File path: %s\\Files\\",TerminalInfoString(TERMINAL_DATA_PATH));
      
      for(int i=0;i<barCount;i++)
         FileWrite(file_handle, time_buff[i],hi_buff[i],low_buff[i],cls_buff[i],vol_buff[i]);
      
      FileClose(file_handle);
      PrintFormat("Data is written, %s file is closed.",fle_nam);
   } else
      PrintFormat("Failed to open %s file, Error code = %d",fle_nam,GetLastError());
}

string getTimeframeDesc(int timeframe)
{
   if (timeframe == PERIOD_W1)
      return "WE";
   else if (timeframe == PERIOD_MN1)
      return "MN";
   else if (timeframe == PERIOD_D1)
      return "D";
   else if (timeframe == PERIOD_H4)
      return "H4";
   else if (timeframe == PERIOD_H1)
      return "H1";
   else if (timeframe == PERIOD_M30)
      return "M30";
   else if (timeframe == PERIOD_M15)
      return "M15";
   else if (timeframe == PERIOD_M5)
      return "M5";
   else if (timeframe == PERIOD_M1)
      return "M1";
   else
      return "PERIOD";
}


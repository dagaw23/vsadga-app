//+------------------------------------------------------------------+
//|                                                   DataExpert.mq4 |
//|                                                     dagaw23#2015 |
//|                                             https://www.mql5.com |
//+------------------------------------------------------------------+
#property copyright "dagaw23#2015"
#property link      "https://www.mql5.com"
#property version   "1.00"
#property strict

//---------------------------------------- Input parameters ------------------------------------------
input string   SYMBOL_1="AUDUSD";
input string   SYMBOL_2="GBPUSD";
input string   SYMBOL_3="USDCAD";
input string   SYMBOL_4="EURUSD";
input string   SYMBOL_5="USDJPY";
input string   SYMBOL_6="NZDUSD";
input string   SYMBOL_7="USDCHF";
input string   SYMBOL_8="OIL-SEP16";
input string   SYMBOL_9="GOLD";
input string   SYMBOL_10="SILVER";
input string   SYMBOL_11="US500-SEP16";
input string   SYMBOL_12="GER30-SEP16";


input string   FILE_DIR_PATH="Actual";
input int      ARRAY_WRITE_SIZE=5;

input bool     IS_FULL_LOAD=false;
input int      FULL_LOAD_SIZE=400;

//------------------------------------------ Global variables ----------------------------------------
int gLastMinute=100;
bool gIsDone=false;


//+------------------------------------------------------------------+
//| Expert initialization function                                   |
//+------------------------------------------------------------------+
int OnInit()
  {
   //Alert("#OnInit#"); 
   // EventSetTimer(60); // timer every 60 sec.
      
//---
   return(INIT_SUCCEEDED);
  }
//+------------------------------------------------------------------+
//| Expert deinitialization function                                 |
//+------------------------------------------------------------------+
void OnDeinit(const int reason)
  {
  //Alert("#OnDeinit#");
//--- destroy timer
  // EventKillTimer();
      
  }
//+------------------------------------------------------------------+
//| Expert tick function                                             |
//+------------------------------------------------------------------+
void OnTick()
{
   // czy przetwarzanie juz zostalo wykonane:
   if (gIsDone)
      return;
   
   if (IS_FULL_LOAD) {
      write_by_symbol(SYMBOL_1, PERIOD_M5, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_2, PERIOD_M5, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_3, PERIOD_M5, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_4, PERIOD_M5, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_5, PERIOD_M5, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_6, PERIOD_M5, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_7, PERIOD_M5, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_8, PERIOD_M5, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_9, PERIOD_M5, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_10, PERIOD_M5, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_11, PERIOD_M5, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_12, PERIOD_M5, FULL_LOAD_SIZE);
      
      write_by_symbol(SYMBOL_1, PERIOD_M15, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_2, PERIOD_M15, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_3, PERIOD_M15, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_4, PERIOD_M15, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_5, PERIOD_M15, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_6, PERIOD_M15, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_7, PERIOD_M15, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_8, PERIOD_M15, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_9, PERIOD_M15, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_10, PERIOD_M15, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_11, PERIOD_M15, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_12, PERIOD_M15, FULL_LOAD_SIZE);
      
      write_by_symbol(SYMBOL_1, PERIOD_H1, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_2, PERIOD_H1, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_3, PERIOD_H1, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_4, PERIOD_H1, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_5, PERIOD_H1, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_6, PERIOD_H1, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_7, PERIOD_H1, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_8, PERIOD_H1, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_9, PERIOD_H1, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_10, PERIOD_H1, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_11, PERIOD_H1, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_12, PERIOD_H1, FULL_LOAD_SIZE);
      
      write_by_symbol(SYMBOL_1, PERIOD_H4, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_2, PERIOD_H4, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_3, PERIOD_H4, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_4, PERIOD_H4, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_5, PERIOD_H4, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_6, PERIOD_H4, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_7, PERIOD_H4, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_8, PERIOD_H4, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_9, PERIOD_H4, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_10, PERIOD_H4, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_11, PERIOD_H4, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_12, PERIOD_H4, FULL_LOAD_SIZE);
      
      gIsDone=true;
   } else {
      datetime l_time = TimeCurrent();
   
      // czy zmienila sie minuta:
      if (gLastMinute != TimeMinute(l_time)) {
         Sleep(1000); // zatrzymanie jeszcze na 1 sekunde
         //Alert("OnTick():", gLastMinute, ",", TimeMinute(l_time), ".");
      
         gLastMinute = TimeMinute(l_time);
      
         write_all(l_time);
      }
   }
}
//+------------------------------------------------------------------+
//| Timer function                                                   |
//+------------------------------------------------------------------+
void OnTimer()
{
//---
   //Alert("OnTimer:", TimeToStr(TimeCurrent(), TIME_MINUTES), ".");
   
   //if (is_market_close()) {
   //   Alert("Market is closed.");
   //   return;
   //}
   
   //int week_day_nr = TimeDayOfWeek(TimeCurrent());
   //int d_hour = TimeHour(TimeCurrent());
   //datetime l_time = TimeCurrent();
   //Alert("OnTimer: ", week_day_nr, ",", d_hour, ",", TimeToStr(l_time, TIME_MINUTES), ".");
}
//+------------------------------------------------------------------+

void write_all(datetime date_time)
{   
   write_by_5_minutes(date_time);
   write_by_15_minutes(date_time);
   write_by_1_hour(date_time);
   write_by_4_hour(date_time);
}

/*
   Synchronizuje, do ktorej sekundy nastepuje start EA.
*/
void synchronize_init(int secondStart)
{
   int sec_val = 0;
   
   Alert("Synchro start.");
   
   do {
      Sleep(1000);
      sec_val = TimeSeconds(TimeCurrent());
      Alert("sec:", sec_val, ".");
   } while (sec_val != 2);
   
   Alert("Synchro done.");
}

void write_by_5_minutes(datetime date_time)
{
   int l_min = TimeMinute(date_time);
   //Alert("Writing by 5MIN [", l_min, "]");
   
   write_by_symbol(SYMBOL_1, PERIOD_M5, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_2, PERIOD_M5, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_3, PERIOD_M5, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_4, PERIOD_M5, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_5, PERIOD_M5, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_6, PERIOD_M5, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_7, PERIOD_M5, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_8, PERIOD_M5, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_9, PERIOD_M5, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_10, PERIOD_M5, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_11, PERIOD_M5, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_12, PERIOD_M5, ARRAY_WRITE_SIZE);
}

void write_by_15_minutes(datetime date_time)
{
   int l_min = TimeMinute(date_time);
   //Alert("Writing by 15MIN [", l_min, "]");
      
   write_by_symbol(SYMBOL_1, PERIOD_M15, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_2, PERIOD_M15, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_3, PERIOD_M15, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_4, PERIOD_M15, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_5, PERIOD_M15, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_6, PERIOD_M15, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_7, PERIOD_M15, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_8, PERIOD_M15, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_9, PERIOD_M15, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_10, PERIOD_M15, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_11, PERIOD_M15, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_12, PERIOD_M15, ARRAY_WRITE_SIZE);
}

void write_by_1_hour(datetime date_time)
{
   int l_min = TimeMinute(date_time);
   int l_hr = TimeHour(date_time);
   //Alert("Writing by 1 hour [", l_hr, ":", l_min, "]");
      
   write_by_symbol(SYMBOL_1, PERIOD_H1, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_2, PERIOD_H1, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_3, PERIOD_H1, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_4, PERIOD_H1, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_5, PERIOD_H1, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_6, PERIOD_H1, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_7, PERIOD_H1, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_8, PERIOD_H1, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_9, PERIOD_H1, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_10, PERIOD_H1, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_11, PERIOD_H1, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_12, PERIOD_H1, ARRAY_WRITE_SIZE);
}

void write_by_4_hour(datetime date_time)
{
   int l_min = TimeMinute(date_time);
   int l_hr = TimeHour(date_time);
   //Alert("Writing by 4 hour [", l_hr, ":", l_min, "]");
      
   write_by_symbol(SYMBOL_1, PERIOD_H4, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_2, PERIOD_H4, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_3, PERIOD_H4, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_4, PERIOD_H4, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_5, PERIOD_H4, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_6, PERIOD_H4, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_7, PERIOD_H4, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_8, PERIOD_H4, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_9, PERIOD_H4, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_10, PERIOD_H4, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_11, PERIOD_H4, ARRAY_WRITE_SIZE);
   write_by_symbol(SYMBOL_12, PERIOD_H4, ARRAY_WRITE_SIZE);
}

void write_by_symbol(string symbol, int tme_frm, int buff_size)
{
   write_to_file(
      getFileName(symbol, tme_frm),
      symbol,
      tme_frm,
      buff_size);
}

void write_to_file(string file_name, string symbol, int tme_frm, int buff_size)
{
   // zerowanie bledow:
   ResetLastError();
   
   int file_handle=FileOpen(FILE_DIR_PATH + "//" + file_name, FILE_WRITE|FILE_CSV);
   
   if(file_handle!=INVALID_HANDLE)
   {
      PrintFormat("%s file is available for writing.", file_name);
      PrintFormat("File path: %s\\%s\\", TerminalInfoString(TERMINAL_DATA_PATH), FILE_DIR_PATH);
      
      //flush data buffer:
      //flush_data(symbol, tme_frm, buff_size);
      
      write_data_to_file(file_handle, symbol, tme_frm, buff_size);
      PrintFormat("Data is written, %s file is closed.", file_name);          
      
      FileClose(file_handle);
      
   } else
      PrintFormat("Failed to open %s file, Error code = %d.", file_name, GetLastError());
}

void flush_data(string symbol, int tme_frm, int buff_size)
{
   for(int i=0; i<buff_size; i++) {
      iTime(symbol, tme_frm, i);
      iOpen(symbol, tme_frm, i);
      iHigh(symbol, tme_frm, i);
      iLow(symbol, tme_frm, i);
      iClose(symbol, tme_frm, i);
      iVolume(symbol, tme_frm, i);
   }
}

void write_data_to_file(int file_handle, string symbol, int tme_frm, int buff_size)
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
   
   CopyTime(symbol,        tme_frm, 0, buff_size, time_buff);
   CopyHigh(symbol,        tme_frm, 0, buff_size, hi_buff);
   CopyLow(symbol,         tme_frm, 0, buff_size, low_buff);
   CopyClose(symbol,       tme_frm, 0, buff_size, cls_buff);
   CopyTickVolume(symbol,  tme_frm, 0, buff_size, vol_buff);
   
   for(int i=0; i<buff_size; i++) {
      FileWrite(file_handle,
		time_buff[i],
		hi_buff[i],
		low_buff[i],
		cls_buff[i],
		vol_buff[i],
		iMA(symbol, tme_frm, 20, 0, MODE_SMA, PRICE_CLOSE, i));
   }
}

//+------------------------------------------------------------------+
//| Utility functions                                                |
//+------------------------------------------------------------------+
string getFileName(string symbol, int tme_frm)
{
   int min_pos = StringFind(symbol, "-");
      
   if (min_pos != -1) {
      return StringSubstr(symbol, 0, min_pos) + "_" + getTimeframeDesc(tme_frm);
   } else {
      return symbol + "_" + getTimeframeDesc(tme_frm);
   }
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
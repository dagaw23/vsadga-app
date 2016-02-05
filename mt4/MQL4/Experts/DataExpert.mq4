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
input string   SYMBOL_1="EURUSD";
input string   SYMBOL_2="AUDUSD";
input string   SYMBOL_3="GBPUSD";
input string   SYMBOL_4="GOLD";

input string   FILE_DIR_PATH="Actual";
input int      ARRAY_WRITE_SIZE=10;

input bool     IS_FULL_LOAD=false;
input int      FULL_LOAD_SIZE=2000;

//------------------------------------------ Global variables ----------------------------------------
int gLastMinute=100;


//+------------------------------------------------------------------+
//| Expert initialization function                                   |
//+------------------------------------------------------------------+
int OnInit()
  {
   Alert("#OnInit#"); 
   // EventSetTimer(60); // timer every 60 sec.
      
//---
   return(INIT_SUCCEEDED);
  }
//+------------------------------------------------------------------+
//| Expert deinitialization function                                 |
//+------------------------------------------------------------------+
void OnDeinit(const int reason)
  {
  Alert("#OnDeinit#");
//--- destroy timer
  // EventKillTimer();
      
  }
//+------------------------------------------------------------------+
//| Expert tick function                                             |
//+------------------------------------------------------------------+
void OnTick()
{
   if (IS_FULL_LOAD) {
      write_by_symbol(SYMBOL_1, PERIOD_M5, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_2, PERIOD_M5, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_3, PERIOD_M5, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_4, PERIOD_M5, FULL_LOAD_SIZE);
      
      write_by_symbol(SYMBOL_1, PERIOD_M15, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_2, PERIOD_M15, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_3, PERIOD_M15, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_4, PERIOD_M15, FULL_LOAD_SIZE);
      
      write_by_symbol(SYMBOL_1, PERIOD_H1, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_2, PERIOD_H1, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_3, PERIOD_H1, FULL_LOAD_SIZE);
      write_by_symbol(SYMBOL_4, PERIOD_H1, FULL_LOAD_SIZE);
   } else {
      datetime l_time = TimeCurrent();
   
      // czy zmienila sie minuta:
      if (gLastMinute != TimeMinute(l_time)) {
         Sleep(2000); // zatrzymanie jeszcze na 2 sekundy
         Alert("OnTick():", gLastMinute, ",", TimeMinute(l_time), ".");
      
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
   Alert("OnTimer:", TimeToStr(TimeCurrent(), TIME_MINUTES), ".");
   
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
   if (is_market_close(date_time)) {
      Alert("Market is closed.");
      return;
   }
   
   write_by_5_minutes(date_time);
   write_by_15_minutes(date_time);
   write_by_1_hour(date_time);
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

bool is_market_close(datetime date_time)
{
   int week_day_nr = TimeDayOfWeek(date_time);
   int d_hour = TimeHour(date_time);
   
   if (week_day_nr < 5)
      return false;
      
   if (week_day_nr == 5 && d_hour < 23)
      return false;
   
   return true;
}

void write_by_5_minutes(datetime date_time)
{
   int l_min = TimeMinute(date_time);
   
   if (l_min%5 == 0) {
      Alert("Writing by 5MIN [", l_min, "]");
      
      write_by_symbol(SYMBOL_1, PERIOD_M5, ARRAY_WRITE_SIZE);
      write_by_symbol(SYMBOL_2, PERIOD_M5, ARRAY_WRITE_SIZE);
      write_by_symbol(SYMBOL_3, PERIOD_M5, ARRAY_WRITE_SIZE);
      write_by_symbol(SYMBOL_4, PERIOD_M5, ARRAY_WRITE_SIZE);
   }

}

void write_by_15_minutes(datetime date_time)
{
   int l_min = TimeMinute(date_time);
   
   if (l_min%15 == 0) {
      Alert("Writing by 15MIN [", l_min, "]");
      
      write_by_symbol(SYMBOL_1, PERIOD_M15, ARRAY_WRITE_SIZE);
      write_by_symbol(SYMBOL_2, PERIOD_M15, ARRAY_WRITE_SIZE);
      write_by_symbol(SYMBOL_3, PERIOD_M15, ARRAY_WRITE_SIZE);
      write_by_symbol(SYMBOL_4, PERIOD_M15, ARRAY_WRITE_SIZE);
   }
}

void write_by_1_hour(datetime date_time)
{
   int l_min = TimeMinute(date_time);
   int l_hr = TimeHour(date_time);
   
   if (l_min == 0) {
      Alert("Writing by 1 hour [", l_hr, ":", l_min, "]");
      
      write_by_symbol(SYMBOL_1, PERIOD_H1, ARRAY_WRITE_SIZE);
      write_by_symbol(SYMBOL_2, PERIOD_H1, ARRAY_WRITE_SIZE);
      write_by_symbol(SYMBOL_3, PERIOD_H1, ARRAY_WRITE_SIZE);
      write_by_symbol(SYMBOL_4, PERIOD_H1, ARRAY_WRITE_SIZE);
   }
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
      
      write_data_to_file(file_handle, symbol, tme_frm, buff_size);
      PrintFormat("Data is written, %s file is closed.", file_name);          
      
      FileClose(file_handle);
      
   } else
      PrintFormat("Failed to open %s file, Error code = %d.", file_name, GetLastError());
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
		iMA(symbol, tme_frm, 20, 0, MODE_SMA, PRICE_TYPICAL, i));
   }
}

//+------------------------------------------------------------------+
//| Utility functions                                                |
//+------------------------------------------------------------------+
string getFileName(string symbol, int tme_frm)
{
   return symbol + "_" + getTimeframeDesc(tme_frm);
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
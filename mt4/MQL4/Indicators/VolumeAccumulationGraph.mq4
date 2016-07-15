//+------------------------------------------------------------------+
//|                                      VolumeAccumulationGraph.mq4 |
//|                                            Copyright 2016, DaGaw |
//|                                                                  |
//+------------------------------------------------------------------+
#property copyright "Copyright 2016, DaGaw"
#property link      ""
#property version   "1.00"
#property strict
#property indicator_chart_window

//--- input parameters
input string   AccessHttp = "http://generatedata.biz/mt4/smart.php";
input string   AccessKey = "";

// Log:
extern bool EnableLogging = true;
int logHandle = -1;

// Wininet:
int hInternet;
int Internet_Open_Type_Preconfig = 0;
int Internet_Open_Type_Direct = 1;
int Internet_Open_Type_Proxy = 3;
int READURL_BUFFER_SIZE = 1000;

// Debug:
int DebugLevel = 2;
bool bWinInetDebug = true;

// Indicator:
double volumeUpBuffer[];
double volumeDownBuffer[];
double volumeNotEndBuffer[];
double prevBarClose = 0;
string prevBarTrend = "U"; //bez znaczenia od ktorego zaczynamy
int volumeSumUp = 0;
int volumeSumDown = 0;
string zero_date = "1970.01.01%2000:00";
datetime barNotEndDatetime;
int tickCounter = 0;
int textObjCounter = 0;

#import "wininet.dll"
#define INTERNET_FLAG_PRAGMA_NOCACHE    0x00000100 // Forces the request to be resolved by the origin server, even if a cached copy exists on the proxy.
#define INTERNET_FLAG_NO_CACHE_WRITE    0x04000000 // Does not add the returned entity to the cache. 
#define INTERNET_FLAG_RELOAD            0x80000000 // Forces a download of the requested file, object, or directory listing from the origin server, not from the cache.

int InternetOpenW(
	string 	sAgent,
	int		lAccessType,
	string 	sProxyName="",
	string 	sProxyBypass="",
	int 	lFlags=0
);

int InternetOpenUrlW(
	int 	hInternetSession,
	string 	sUrl, 
	string 	sHeaders="",
	int 	lHeadersLength=0,
	int 	lFlags=0,
	int 	lContext=0 
);

int InternetReadFile(
   int 	hFile,
	uchar & arr[],
	int 	lNumBytesToRead,
	int& 	lNumberOfBytesRead[]
);

int InternetCloseHandle(
	int 	hInet
);
#import

//+------------------------------------------------------------------+
//| Custom indicator initialization function                         |
//+------------------------------------------------------------------+
int OnInit()
{
   Alert("OnInit");
   
   IndicatorShortName("Volume Accumulation Graph Indicator");
   OpenLog("VolumeAccumaGraphLog");
   deleteTextObjectsIfExist();

   return(INIT_SUCCEEDED);
}
  
void OnDeinit(const int reason)
{
   Alert("OnDeinit");
   
   CloseLog();
   deleteAllTextObjects();
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
      Alert("rates_total=", rates_total, ", prev_calculated=", prev_calculated, ".");
      
      // clean:
      tickCounter = 0;
      clearInitArrays(rates_total);
      
      // load all data - new time frame:
      if (prev_calculated == 0) {
         // odczyt danych wolumenowych:
         string readData = ReadUrl(getAllBySmartUrl());
         
         // wpisanie danych do wskaznika:
         writeAllVolumeData(readData, rates_total);
         
      } else {
         // odczyt tylko poprzedniego bara:
         string readData = ReadUrl(getLastBySmartUrl());
         //Log("readData=" + readData + ".");
         
         // wpisanie zaktualizowanego bara:
         writeUpdatedVolumeData(readData, rates_total);
      }
      
   }
   
   
      
   
//--- return value of prev_calculated for next call
   return(rates_total);
  }
//+------------------------------------------------------------------+


//+------------------------------------------------------------------+
//| Main indicator functions                                         |
//+------------------------------------------------------------------+
void printTextOrCalculateVolume(int barShift, int barVolume) {
   // czy juz jest poprzedni bar:
   if (prevBarClose == 0) {
      prevBarClose = iClose(NULL, 0, barShift);
      return;
   }
      
   // sprawdzenie zamkniec na barach:
   if (iClose(NULL, 0, barShift) > prevBarClose) {
      // *** UP ***
      
      if (prevBarTrend == "U") {
         volumeSumUp = volumeSumUp + barVolume;
      
      } else if (prevBarTrend == "D") {
         // utworz tekst dla ruchu w dol:
         addTextLabel(barShift+1, volumeSumDown, prevBarTrend);
         // wyzeruj wolumen zbiorczy:
         volumeSumDown = 0;
            
         prevBarTrend = "U";
         volumeSumUp = barVolume;
         volumeUpBuffer[barShift] = volumeSumUp;
            
      }
      //Log("UP, " + prevBarTrend + ", " + volumeSumUp + ", " + volumeSumDown);
         
   } else if (iClose(NULL, 0, barShift) < prevBarClose) {
      // *** DOWN ***
      
      if (prevBarTrend == "U") {
         // utworz tekst dla ruchu w dol:
         addTextLabel(barShift+1, volumeSumUp, prevBarTrend);
         // wyzeruj wolumen zbiorczy:
         volumeSumUp = 0;
         
         prevBarTrend = "D";
         volumeSumDown = barVolume;
         volumeDownBuffer[barShift] = volumeSumDown;
         
      } else if (prevBarTrend == "D") {
         volumeSumDown = volumeSumDown + barVolume;
         volumeDownBuffer[barShift] = volumeSumDown;
            
      }
      //Log("DOWN, " + prevBarTrend + ", " + volumeSumUp + ", " + volumeSumDown);
      
   } else {
      // *** LEVEL ***
      
      if (prevBarTrend == "U") {
         volumeSumUp = volumeSumUp + barVolume;
         volumeUpBuffer[barShift] = volumeSumUp;
         
      } else if (prevBarTrend == "D") {
         volumeSumDown = volumeSumDown + barVolume;
         volumeDownBuffer[barShift] = volumeSumDown;
         
      }
      //Log("LEVEL, " + prevBarTrend + ", " + volumeSumUp + ", " + volumeSumDown);
   }
   
   // aktualizacja zamkniecia bara:
   prevBarClose = iClose(NULL, 0, barShift);
}

void writeAllVolumeData(string data, int ratesTotal) {
   ushort sepaLine = 10; // znak nowej linii
   ushort sepaField = StringGetCharacter(";", 0);
   string linesBuffer[];
   string fieldBuffer[];
   int iFldSplit;
   
   datetime bar_date;
   int bar_shift;
   int bar_volume;
   
   // czyszczenia zmiennych globalnych:
   prevBarClose = 0;
   prevBarTrend = "U"; //bez znaczenia od ktorego zaczynamy
   volumeSumUp = 0;
   volumeSumDown = 0;
   
   // podzielenie calej odpowiedzi z serwera:   
   int iSplit = StringSplit(data, sepaLine, linesBuffer);
   //Log("iSplit=" + iSplit + ", sepaLine=" + sepaLine + ".");
         
   for (int i=iSplit-1; i>0; i--) {
      iFldSplit = StringSplit(linesBuffer[i],sepaField,fieldBuffer);
      //Log("Line " + i + ":" + linesBuffer[i] + ", iFldSplit=" + iFldSplit + ".");
      
      if (iFldSplit != 2) {
         Log("Bledna zawartosc linii nr [" + IntegerToString(i) + "] o zawartsci [" + linesBuffer[i] + "].");
         continue;
      }
      
      bar_date = convertDate(fieldBuffer[0]);
      bar_volume = fieldBuffer[1];
      bar_shift = iBarShift(NULL, 0, bar_date);
      //Log("data:" + TimeToStr(bar_date, TIME_MINUTES)  + ", wolumen:" + bar_volume + ", shift:" + bar_shift + ".");
      
      // pozycja bara - nie moze byc wieksza od wszystkich barow:
      if (bar_shift >= ratesTotal)
         continue;
      
      // i=1: tutaj ostatni bar - bez ostatecznego wolumenu:
      if (i == 1) {
         updateNotEndedBar(bar_date, bar_shift, bar_volume);
         
         // nie zapisujemy sumy wolumenow, ani wskaznika trendu cen
         continue;
      }
      
      // wpisz dane do buforow wskaznika:
      printTextOrCalculateVolume(bar_shift, bar_volume);
   }
}

void writeUpdatedVolumeData(string data, int ratesTotal) {
   ushort sepaLine = 10; // znak nowej linii
   ushort sepaField = StringGetCharacter(";", 0);
   string linesBuffer[];
   string fieldBuffer[];
   int iFldSplit;
   
   datetime bar_date;
   int bar_shift;
   int bar_volume;

   // podzielenie calej odpowiedzi z serwera:   
   int iSplit = StringSplit(data, sepaLine, linesBuffer);
   Log("iSplit=" + IntegerToString(iSplit) + ", sepaLine=" + IntegerToString(sepaLine) + ".");
   
   for (int i=iSplit-1; i>0; i--) {
      iFldSplit = StringSplit(linesBuffer[i],sepaField,fieldBuffer);
      
       if (iFldSplit != 2) {
         Log("Bledna zawartosc linii nr [" + IntegerToString(i) + "] o zawartsci [" + linesBuffer[i] + "].");
         continue;
      }
      
      bar_date = convertDate(fieldBuffer[0]);
      bar_volume = StringToInteger(fieldBuffer[1]);
      bar_shift = iBarShift(NULL, 0, bar_date);
      
      if (bar_date == barNotEndDatetime && bar_shift == 1) {
         // usuniecie poprzedniego:
         volumeNotEndBuffer[bar_shift] = EMPTY_VALUE;
         
         // aktualizacja wolumenu:
         printTextOrCalculateVolume(bar_shift, bar_volume);
         
         // zapisanie daty do nastepnego pobrania wolumenu:
         barNotEndDatetime = iTime(NULL, 0, 0);
      
         Log("Equal:" + IntegerToString(bar_date) + "," + TimeToStr(barNotEndDatetime) + ", bar_shift=" + bar_shift 
            + ", not end:" + IntegerToString(volumeNotEndBuffer[bar_shift]) + ", up:" + DoubleToStr(volumeUpBuffer[bar_shift]) 
            + ", down:" + DoubleToStr(volumeDownBuffer[bar_shift]) + ".");
      } else {
         Log("Info:" + TimeToStr(bar_date) + "," + TimeToStr(barNotEndDatetime) + ", bar_shift=" + IntegerToString(bar_shift));
      }
   }
}

// Konwertuje symbol Forex - na symbol kontraktu terminowego, wg
// ktorego nastepuje pobranie wolumenu rzeczywistego.
string getFuturesSymbol(string fxSymbol)
{
   if (fxSymbol == "AUDUSD")
      return "6a";
   else if (fxSymbol == "GBPUSD")
      return "6b";
   else if (fxSymbol == "USDCAD")
      return "6c";
   else if (fxSymbol == "EURUSD")
      return "6e";
   else if (fxSymbol == "USDJPY")
      return "6j";
   else if (fxSymbol == "NZDUSD")
      return "6n";
   else if (fxSymbol == "USDCHF")
      return "6s";
   else if (fxSymbol == "GOLD")
      return "gc";
   else if (fxSymbol == "Crude Oil")
      return "cl";
   else if (fxSymbol == "Copper")
      return "hg";
   else if (fxSymbol == "Natural Gas")
      return "ng";
   else if (fxSymbol == "Silver")
      return "si";
   else if (fxSymbol == "Wheat")
      return "zw";
   else if (fxSymbol == "E-mini S&P 500")
      return "es";
   else if (fxSymbol == "E-mini NASDAQ-100")
      return "nq";
   else if (fxSymbol == "E-mini Dow")
      return "ym";
   else if (fxSymbol == "U.S. Treasury Bond")
      return "zb";
   else if (fxSymbol == "GER30Cash")
      return "fdax";
   else if (fxSymbol == "Dollar Index")
      return "dx";
    else
      return "??";
}

// Zwraca wielkosc przesuniecia tekstu w gore lub w dol - wg symbolu Forex oraz wg wielkosci pojedynczego ticku.
double shiftBySymbol()
{
   string sym = Symbol();
   int prd = Period();
   
   if (sym == "AUDUSD" || sym == "GBPUSD" || sym == "USDCAD" || sym == "EURUSD" || sym == "USDCHF") {
      if (prd == PERIOD_M1)
         return 0.00007;
      else if (prd == PERIOD_M5)
         return 0.00011;
      else if (prd == PERIOD_M15)
         return 0.0002;
      else if (prd == PERIOD_M30)
         return 0.00025;
      else if (prd == PERIOD_H1)
         return 0.0003;
      else if (prd == PERIOD_H4)
         return 0.0006;
      else
         return 0.0009;
   } else {
      if (prd == PERIOD_M1)
         return 0.0009;
      else if (prd == PERIOD_M5)
         return 0.0015;
      else if (prd == PERIOD_M15)
         return 0.002;
      else if (prd == PERIOD_M30)
         return 0.0025;
      else if (prd == PERIOD_H1)
         return 0.003;
      else if (prd == PERIOD_H4)
         return 0.004;
      else
         return 0.005;
   }
   
   //fxSymbol == "USDJPY"
   //fxSymbol == "NZDUSD"
   //fxSymbol == "GOLD"
   //fxSymbol == "Crude Oil"
   //fxSymbol == "Copper"
   //fxSymbol == "Natural Gas")
   //fxSymbol == "Silver")
   //fxSymbol == "Wheat")
   //fxSymbol == "E-mini S&P 500")
   //fxSymbol == "E-mini NASDAQ-100")
   //fxSymbol == "E-mini Dow")
   //fxSymbol == "U.S. Treasury Bond")
   //fxSymbol == "DAX Index")
   //fxSymbol == "Dollar Index")
}

string getAllBySmartUrl() {
   string result_url = StringConcatenate(AccessHttp,
      "?symbol=", Symbol(),
      "&timeframe=",Period(),
      "&usertime=", getActualTime(),
      "&lastopen=", zero_date,
      "&accesskey=", AccessKey,
      "&extradata=", getFuturesSymbol(Symbol()),
      "&lastloaded=",zero_date);
   
   Log("Http All:" + result_url);
   
   return result_url;
}

string getLastBySmartUrl() {
   string result_url = StringConcatenate(AccessHttp,
      "?symbol=", Symbol(),
      "&timeframe=",Period(),
      "&usertime=", getActualTime(),
      "&lastopen=", convertDate2(barNotEndDatetime),
      "&accesskey=", AccessKey,
      "&extradata=", getFuturesSymbol(Symbol()),
      "&lastloaded=", convertDate2(barNotEndDatetime));
   
   Log("Http Last:" + result_url);
   
   return result_url;
}

void clearInitArrays(int barMaxCount) {
   
   ArrayResize(volumeUpBuffer, barMaxCount);
   ArrayResize(volumeDownBuffer, barMaxCount);
   ArrayResize(volumeNotEndBuffer, 2);
   
   ArrayInitialize(volumeUpBuffer, EMPTY_VALUE);
   ArrayInitialize(volumeDownBuffer, EMPTY_VALUE);
   ArrayInitialize(volumeNotEndBuffer, EMPTY_VALUE);
}

bool deleteAllTextObjects() {
   string obj_name;
   
   // usuniecie obiektow tekstowych - jesli sa jakies utworzone:
   if (textObjCounter > 0) {
      
      for (int i=textObjCounter; i>=0; i--) {
         textObjCounter = textObjCounter - 1;
         obj_name = StringConcatenate("text_obj_", textObjCounter);
         
         if (!ObjectDelete(0, obj_name)) {
            Log("ERROR: can't delete text object [" + obj_name + "], error code ["+GetLastError() + "].");
            return false;
         }
         
         // odswiezenie wykresu:
         ChartRedraw(0);
         Sleep(10);
      }
         
   }   
   
   return true;
}

bool deleteTextObjectsIfExist() {
   string obj_name;
   
   // usuniecie obiektow tekstowych - jesli zostana znalezione:
   for (int i=50000; i>=0; i--) {
      obj_name = StringConcatenate("text_obj_", i);
      
      if (ObjectFind(obj_name) >= 0) {
         if (!ObjectDelete(obj_name)) {
            Log("ERROR: NOT delete text object [" + obj_name + "], error code ["+GetLastError() + "].");
            return false;
         }
         
         // odswiezenie wykresu:
         ChartRedraw(0);
         Sleep(10);
      }
   }
   
   return true;
}

void updateNotEndedBar(datetime barDate, int barShift, int barVolume) {
   // zapisanie daty ostatniego wolumenu:
   barNotEndDatetime = barDate;
   
   // zapisanie wolumenu - bara niekompletnego:
   volumeNotEndBuffer[barShift] = barVolume;
   
   Log("Not end bar datetime:" + convertDate2(barDate) + ".");
}

//+------------------------------------------------------------------+
//| Charting functions                                               |
//+------------------------------------------------------------------+
bool addTextLabel(int barShift, int barVolume, string barTrend) {
   double bar_price = 0;
   long bar_color = 0;
   datetime bar_time = iTime(NULL, 0, barShift);
   
   // nazwa obiektu tekstowego:
   string obj_name = StringConcatenate("text_obj_", textObjCounter);
   
   // pozycja obiektu tekstowego:
   if (barTrend == "U") {
      bar_price = iHigh(NULL, 0, barShift) + (2 * shiftBySymbol());
      bar_color = clrDarkGreen;
   } else if (barTrend == "D") {
      bar_price = iLow(NULL, 0, barShift) - shiftBySymbol();
      bar_color = clrDarkRed;
   }
   
   // utworz obiekt na wykresie:
   if (!ObjectCreate(0, obj_name, OBJ_TEXT, 0, bar_time, bar_price)) {
      Log("ERROR: can't create text object - error code: ["+GetLastError() + "].");
      return false;
   }
   
   // dodatkowa parametryzacja obiektu tekstu:
   ObjectSetString(0, obj_name, OBJPROP_TEXT, IntegerToString(barVolume/100));
   ObjectSetString(0, obj_name, OBJPROP_FONT, "Verdana");
   ObjectSetInteger(0, obj_name, OBJPROP_FONTSIZE, 10);
   ObjectSetInteger(0, obj_name, OBJPROP_COLOR, bar_color);
   
   // odswiezenie wykresu:
   ChartRedraw(0);
   Sleep(10);
   
   // inkrementacja licznika:
   textObjCounter = textObjCounter + 1;
   
   return true;
}

//+------------------------------------------------------------------+
//| Utility functions                                                |
//+------------------------------------------------------------------+
//Konwertuje date z postaci datetime - na lancuch tekstowy w postaci: "yyyy.mm.dd%20hh:mi".
string convertDate2(datetime inputDate) {
   return StringConcatenate(
      TimeToStr(inputDate, TIME_DATE),
      "%20",
      TimeToStr(inputDate, TIME_MINUTES));
}

// Konwertuje date z postaci lancucha tekstowego "dd.mm.yyyy hh:mi" -
// na obiekt daty w postaci datetime.
datetime convertDate(string ddMMyyyyTime) {
   return StringToTime(StringConcatenate(StringSubstr(ddMMyyyyTime, 6, 4), ".", // yyyy
         StringSubstr(ddMMyyyyTime, 3, 3), // MM
         StringSubstr(ddMMyyyyTime, 0, 2), // dd
         StringSubstr(ddMMyyyyTime, 10))); //  HH:mm
}

// Konwertuje date z postaci datetime - na lancuch tekstowy w postaci: "dd.mm.yyyy hh:mi".
string convertDate(datetime inputDate) {
   string date_str;
   
   int day = TimeDay(inputDate);
   if (day < 10)
      date_str = StringConcatenate("0", day);
   else
      date_str = day;
   
   int month = TimeMonth(inputDate);
   if (month < 10)
      date_str = StringConcatenate(date_str, ".", "0", month);
   else
      date_str = StringConcatenate(date_str, ".", month);
   
   return StringConcatenate(date_str, ".", TimeYear(inputDate), " ", TimeToStr(inputDate, TIME_MINUTES));
}

string getActualTime()
{
   datetime act_time = TimeCurrent();
   
   return StringConcatenate(TimeToStr(act_time,TIME_DATE),"%20",
      TimeHour(act_time),":",TimeMinute(act_time));
}
//+------------------------------------------------------------------+
//| Log file operation functions                                     |
//+------------------------------------------------------------------+
void CloseLog()
{
   if (logHandle > 0) {
      FileClose(logHandle);
		Alert("Log Closed.");
	}
}

void OpenLog(string fleName)
{
	if (!EnableLogging) 
		return;
	
	if (logHandle > 0) {
	   FileClose(logHandle);
	   logHandle=-1;//added by euclid
	}
	
	datetime act_time = TimeCurrent();
   string strFilename = StringConcatenate(fleName, "_", TimeYear(act_time), TimeMonth(act_time), TimeDay(act_time), "_log.txt");
	logHandle = FileOpen(strFilename,FILE_CSV|FILE_READ|FILE_WRITE);
	
	if (logHandle > 0)
	{
		FileFlush(logHandle);
		FileSeek(logHandle, 0, SEEK_END);
	}
}
//--------------------------------------------------------------------
void Log(string msg)
{
	if (!EnableLogging) 
		return;
		
	if (logHandle <= 0) 
		return;
		
	msg = TimeToStr(TimeCurrent(),TIME_DATE|TIME_MINUTES|TIME_SECONDS) + " " + msg;
	FileWrite(logHandle,msg);
	FileFlush(logHandle);
}
//+------------------------------------------------------------------+
//| Http connection functions                                        |
//+------------------------------------------------------------------+
int OpenUrl(string urlString)
{
   hInternet = InternetOpenW(
	      "Mozilla/5.0 (Windows NT 6.3; rv:36.0) Gecko/20100101 Firefox/36.0",
	      Internet_Open_Type_Preconfig, NULL, NULL, 0);
	
	if (bWinInetDebug)
		Log("hInternet:" + IntegerToString(hInternet) + ".");
	
	if (hInternet == 0) {
	   InternetCloseHandle(hInternet);
	   
	   if (bWinInetDebug)
		   Log("InternetOpenW() failed, hInternet=" + IntegerToString(hInternet) + ".");
	   
	   return (0);
	}
	
	int hInternetUrl = InternetOpenUrlW(hInternet, urlString, NULL, 0, 0, 0);
	//INTERNET_FLAG_NO_CACHE_WRITE | INTERNET_FLAG_PRAGMA_NOCACHE | INTERNET_FLAG_RELOAD,
	
	if (bWinInetDebug) 
		Log("hInternetUrl:" + IntegerToString(hInternetUrl) + ".");

	if (hInternetUrl == 0) {
	   InternetCloseHandle(hInternetUrl);
	   
	   if (bWinInetDebug)
		   Log("InternetOpenUrlW() failed, hInternetUrl=" + IntegerToString(hInternetUrl) + ".");
	   
	   return (0);
	}
	
	return hInternetUrl;
}
//--------------------------------------------------------------------
// Reads the specified URL and returns the server's response.
// Return value is a blank string if an error occurs.
string ReadUrl(string urlString)
{
   string strData = "";
   bool bSuccess = false;
   bool bKeepReading = true;
      
   int hInternetUrl = OpenUrl(urlString);
   if (hInternetUrl == 0)
      return strData;

   if (bWinInetDebug)
      Log("Okay: url handle:" + IntegerToString(hInternetUrl) + ".");
      
   
   while (bKeepReading) {
      int bytesRead[1];
      uchar arrReceive[];
      ArrayResize(arrReceive, READURL_BUFFER_SIZE + 1);
      int iResult = InternetReadFile(hInternetUrl, arrReceive, READURL_BUFFER_SIZE, bytesRead);
      
      if (iResult == 0) {
         if (bWinInetDebug) Log("InternetReadFile() failed, iResult=" + IntegerToString(iResult) + ".");
         bKeepReading = false;
         
      } else {
         // InternetReadFile() has succeeded, but we may be at the end of the data 
         if (bytesRead[0] == 0) {
            if (bWinInetDebug) Log("Reached end of data.");
            bKeepReading = false;
            bSuccess = true;
            
         } else {
            // Convert the data from Ansi to Unicode using the built-in MT4 function
            string strThisRead = CharArrayToString(arrReceive, 0, bytesRead[0], CP_UTF8);
            strData = StringConcatenate(strData, strThisRead);
         }
      }
   }
   
   InternetCloseHandle(hInternetUrl);
   InternetCloseHandle(hInternet);
	hInternet = -1;
	
   return strData;
}
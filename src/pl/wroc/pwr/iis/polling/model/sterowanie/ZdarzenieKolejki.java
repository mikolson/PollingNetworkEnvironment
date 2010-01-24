/*
 * Copyright 2009 Michal Stanek
 * http://github.com/mikolson/PollingNetworkEnvironment
 * mstanek82@gmail.com
 * http://mstanek.blogspot.com
 */


package pl.wroc.pwr.iis.polling.model.sterowanie;

public enum ZdarzenieKolejki {
	KOLEJKA,		// Zakończono obsługiwać kolejkę 
	ZGLOSZENIE,		// Zakończono obsługiwać zgłoszenie
	CZAS,			// Zakończył się czas obsługi i nie nastąpiło zakończenie przetwarzania zadania ani kolejki
	PUSTA			// Kolejka jest pusta
}

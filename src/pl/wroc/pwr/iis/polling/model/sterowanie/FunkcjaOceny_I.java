package pl.wroc.pwr.iis.polling.model.sterowanie;

import pl.wroc.pwr.iis.polling.model.object.polling.Serwer;


public interface FunkcjaOceny_I {
	
	/**
	 * Dokonuje oceny sytuacji w ramach danego serwera uwzględniajac w trakcie oceny 
	 * parametry zgłoszeń znajdujących się w kolejkach jak również fakt spełnienia 
	 * ograniczeń dotyczących długości przetrzymywania zgłoszeń
	 * 
	 * @param s
	 * @return
	 */
	double ocenaSytuacji(Serwer serwer);
}	

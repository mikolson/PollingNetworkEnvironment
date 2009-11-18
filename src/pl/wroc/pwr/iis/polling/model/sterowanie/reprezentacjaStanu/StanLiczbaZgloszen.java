package pl.wroc.pwr.iis.polling.model.sterowanie.reprezentacjaStanu;

import pl.wroc.pwr.iis.polling.model.object.IStan;
import pl.wroc.pwr.iis.polling.model.object.polling.Serwer;

/**
 * Zwraca stan bedący reprezentacją ilosci zgloszen we wszystkich kolejkach. 
 *  
 *  STAN = 
 *   [ilosc1, ilosc2, ... , iloscN];
 *   
 *   gdzie iloscI - to ilosc zgloszen w kazdej kolejce
 *   
 * @author Misiek
 */
public class StanLiczbaZgloszen implements IStan {
	public int[] getStan(Serwer serwer) {
		int[] result = new int[serwer.getIloscKolejek()];

		for (int i = 0; i < serwer.getIloscKolejek(); i++) {
			result[i] = serwer.getKolejka(i).getIloscZgloszen();
		}
		
		return result;
	}

	public Porownanie compare(Serwer serwer, int[] stan2) {
		int suma1 = 0, suma2 = 0;
		int[] stan1 = serwer.getStan();
		
		for (int i = 0; i < stan2.length; i++) {
			suma1 = stan1[i];
			suma2 = stan2[i];
		}
		
		Porownanie result = Porownanie.NieMoznaPorownac;
		
		if (suma1 < suma2) {
			result = Porownanie.Lepszy;
		} else if (suma1 > suma2) {
			result = Porownanie.Gorszy;
		} else {
			result = Porownanie.Rowny;
		}
		
		return result;
	}

	public int[] getMaxStanow(Serwer serwer) {
		int[] result = new int[serwer.getIloscKolejek()];

		for (int i = 0; i < serwer.getIloscKolejek(); i++) {
			result[i] = serwer.getKolejka(i).getMaxZgloszen();
		}

		return result;
	}

	public float compareWspolczynnik(Serwer serwer, int[] stan) {
		return 0;
	}
}

package pl.wroc.pwr.iis.polling.model.sterowanie.reprezentacjaStanu;

import pl.wroc.pwr.iis.polling.model.object.IStan;
import pl.wroc.pwr.iis.polling.model.object.polling.Serwer;

/**
 * Zwraca stan bedący reprezentacją ilosci zgloszen we wszystkich
 * kolejkach oraz informacji o aktualnie obslugiwanej kolejce 
 *  
 *  STAN = 
 *   [obs_kolejka, ilosc1, ilosc2, ... , iloscN];
 *   
 *   gdzie iloscI - to ilosc zgloszen w kazdej kolejce
 *  
 * @author Misiek
 */
public class StanLiczbaZgloszen_I_Obslugiwana implements IStan {

	public int[] getStan(Serwer serwer) {
		int[] result = new int[serwer.getIloscKolejek()+1];

		result[0] = serwer.getObslugiwanaKolejka(); 

		for (int i = 0; i < serwer.getIloscKolejek(); i++) {
			result[i+1] = serwer.getKolejka(i).getIloscZgloszen();
		}
		
		return result;
	}

	public Porownanie compare(Serwer serwer, int[] stan) {
		return Porownanie.NieMoznaPorownac;
	}

	public int[] getMaxStanow(Serwer serwer) {
		int[] result = new int[serwer.getIloscKolejek()+1];

		result[0] = serwer.getIloscKolejek();
		for (int i = 0; i < serwer.getIloscKolejek(); i++) {
			result[i+1] = serwer.getKolejka(i).getMaxZgloszen();
		}

		return result;
	}

	public float compareWspolczynnik(Serwer serwer, int[] stan) {
		return 0;
	}
}

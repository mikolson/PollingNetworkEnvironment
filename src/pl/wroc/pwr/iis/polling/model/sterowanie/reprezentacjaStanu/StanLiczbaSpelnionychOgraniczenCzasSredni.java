package pl.wroc.pwr.iis.polling.model.sterowanie.reprezentacjaStanu;

import pl.wroc.pwr.iis.polling.model.object.IStan;
import pl.wroc.pwr.iis.polling.model.object.polling.Serwer;

/**
 * Buduje stan o liczbie miejsc odpowiadającej liczbie kolejek
 * Jeżeli kolejka spełnia swoje ograniczenie czasowe to na jej pozycji
 * w stanie występuje 0, jeżeli ograniczenie czasowe (średni czas oczekiwania w kolejce)
 * przekroczył wartość ustaloną dla danej kolejki to w miejscu dla tej
 * kolejki wpisane jest 1.
 * 
 *  stan = [0,0,1,0,1]
 *  
 *  - oznacza ze kolejki 3 oraz 5 nie spełniają ograniczenia czasowego
 *    
 *    
 *    s_i = 0, M_i <= R_i
 *    		1, M_i >  R_i
 *    
 *    
 * @author Misiek		this.shake = false;
 */
public class StanLiczbaSpelnionychOgraniczenCzasSredni implements IStan {
	public static final int STAN_OK = 1;
	public static final int STAN_PRZEKROCZENIE = 0;
	
	public int[] getStan(Serwer serwer) {
		int[] result = new int[serwer.getIloscKolejek()];
		
		for (int i = 0; i < result.length; i++) {
			if (serwer.getKolejka(i).getSredniCzasOczekiwania() <= serwer.getKolejka(i).getMaxCzasOczekiwania()) {
				result[i] = STAN_OK;
			} else {
				result[i] = STAN_PRZEKROCZENIE; 
			}
		}
		
		return result;
	}
	
	/**
	 * @see pl.wroc.pwr.iis.polling.model.object.IStan#compare(pl.wroc.pwr.iis.polling.model.object.polling.Serwer, int[])
	 * 
	 * Sprawdza ile ograniczeń jest spełnionych w bieżącym stanie, a ile
	 * w innym stanie. Jeżeli wiecej kolejek ma spełnione ograniczenie w bieżacym stanie
	 * stan ten jest lepszy. Jeżeli ilosć ta jest równa stany są równe, stan jest gorszy
	 * w przeciwnym przypadku.
	 */
	public Porownanie compare(Serwer serwer, int[] innyStan) {
		Porownanie result = Porownanie.NieMoznaPorownac;

		int[] stan = getStan(serwer); 
		if (stan.length == innyStan.length) {
			int ogr1 = 0;
			int ogr2 = 0;
			for (int i = 0; i < stan.length; i++) {
				if (stan[i] == STAN_PRZEKROCZENIE) { ogr1++; }
				if (innyStan[i] == STAN_PRZEKROCZENIE) { ogr2++; }
			}
			
			// Gorszy jest ten stan ktory ma wiecej 1
			if (ogr1 < ogr2)  { result = Porownanie.Lepszy;}
			else if (ogr1 == ogr2) { result = Porownanie.Rowny;}
			else if (ogr1 > ogr2)  { result = Porownanie.Gorszy;}
			else { result = Porownanie.NieMoznaPorownac; }
		}
		
		return result;
	}

	public int[] getMaxStanow(Serwer serwer) {
		int[] result = new int[serwer.getIloscKolejek()];

		for (int i = 0; i < serwer.getIloscKolejek(); i++) {
			result[i] = 2;
		}

		return result;
	}

	public float compareWspolczynnik(Serwer serwer, int[] innyStan) {
		float result = 0;
		
		int[] stan = getStan(serwer); 
		if (stan.length == innyStan.length) {
			int ogr1 = 0;
			int ogr2 = 0;
			for (int i = 0; i < stan.length; i++) {
				if (stan[i] == STAN_PRZEKROCZENIE) { ogr1++; }
				if (innyStan[i] == STAN_PRZEKROCZENIE) { ogr2++; }
			}
//			ogr1 = stan.length - ogr1;
//			ogr2 = innyStan.length - ogr2;
			
			result = -(ogr1 - ogr2);
		}
		
		return result;
	}
	
}

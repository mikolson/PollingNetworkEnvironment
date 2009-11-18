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
 * @author Misiek
 */
public class StanLiczbaPrzedzialySredniCzasOczekiwania implements IStan {
	public static final int STAN_OK = 1;
	public static final int STAN_PRZEKROCZENIE = 0;
	private final int liczbaPrzedzialow;
	
	public StanLiczbaPrzedzialySredniCzasOczekiwania(int liczbaPrzedzialow) {
		this.liczbaPrzedzialow = liczbaPrzedzialow;
	}
	
	public int[] getStan(Serwer serwer) {
		int[] result = new int[serwer.getIloscKolejek()];
		
		for (int i = 0; i < result.length; i++) {
			int maxCzasOczekiwania = serwer.getKolejka(i).getMaxCzasOczekiwania();
			float sredniCzasOczekiwania = serwer.getKolejka(i).getSredniCzasOczekiwania();
			
			float dlugoscPrzedzialu = (float)maxCzasOczekiwania / (float)liczbaPrzedzialow;
			
			int przedzial = liczbaPrzedzialow;
			
			if (sredniCzasOczekiwania < maxCzasOczekiwania) {
				przedzial = (int) (sredniCzasOczekiwania / dlugoscPrzedzialu);
			}
			
			result[i] = przedzial;
		}
		
		return result;
	}
	
	public static void main(String[] args) {
		int liczbaPrzedzialow = 3;
		int maxCzasOczekiwania = 20;
		
		int dlugoscPrzedzialu = maxCzasOczekiwania / liczbaPrzedzialow;
		float czasOczekiwania = 50;
		
		int przedzial = (int) (czasOczekiwania / dlugoscPrzedzialu);
		
		System.out.println(przedzial);
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
				if (stan[i] >  innyStan[i]) { ogr1++; }
				if (stan[i] <  innyStan[i]) { ogr2++; }
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
			result[i] = liczbaPrzedzialow+2;
		}

		return result;
	}

	public float compareWspolczynnik(Serwer serwer, int[] innyStan) {
		float result = 0;
		
//		int[] stan = getStan(serwer); 
//		if (stan.length == innyStan.length) {
//			int ogr1 = 0;
//			int ogr2 = 0;
//			for (int i = 0; i < stan.length; i++) {
//				if (stan[i] == STAN_PRZEKROCZENIE) { ogr1++; }
//				if (innyStan[i] == STAN_PRZEKROCZENIE) { ogr2++; }
//			}
////			ogr1 = stan.length - ogr1;
////			ogr2 = innyStan.length - ogr2;
//			
//			result = -(ogr1 - ogr2);
//		}
		
		return result;
	}
	
}

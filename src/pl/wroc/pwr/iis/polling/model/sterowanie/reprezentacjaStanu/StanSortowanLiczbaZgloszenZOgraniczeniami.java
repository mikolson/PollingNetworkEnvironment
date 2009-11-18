package pl.wroc.pwr.iis.polling.model.sterowanie.reprezentacjaStanu;

import java.util.ArrayList;
import java.util.Arrays;

import pl.wroc.pwr.iis.polling.model.object.IStan;
import pl.wroc.pwr.iis.polling.model.object.polling.Serwer;

/**
 * Zwraca stan bazujac na zasadzie:
 *   1. Kolejka 1 - 10 zgloszen
 *   2. Kolejka 2 - 20 zgloszen
 *   3. Kolejka 3 - 4 zgloszenia
 *   
 * Sredni czas oczekiwania w zadnej z kolejek nie przekroczyl ograniczenia:
 * 
 * 	Stan = [2,3,1]; // Sortowanie po liczbie zgloszen
 *
 * Jeżeli kolejka 1 przekroczyla ograniczenie czasowe to stan bedzie wygladal 
 * następująco:
 * 
 *  Stan = [0,2,1];
 *  
 * Jeśli natomiast kolejka 3 przekroczyla ograniczenie czasowe to:
 * 
 *  Stan = [0,1,0];
 *  
 *  
 * @author Misiek
 */
public class StanSortowanLiczbaZgloszenZOgraniczeniami implements IStan {
	/**
	 * Wartosc uzywana do obliczania stanu - nie uzywac w zanym innym celu. 
	 */
	private ArrayList<Float> zgloszeniaList = new ArrayList<Float>();

	public int[] getStan(Serwer serwer) {
		int[] result = new int[serwer.getIloscKolejek()];
		
		//Dodanie do tablicy ilosci zgloszen w kolejkach ktore jeszcze nie 
		//przekroczyly ograniczenia czasowego
		zgloszeniaList.clear();
		for (int i = 0; i < result.length; i++) {
			if (serwer.getKolejka(i).getSredniCzasOczekiwania() <= serwer.getKolejka(i).getMaxCzasOczekiwania()) {
				zgloszeniaList.add(getMiara(serwer, i));
			} 
		}

		float[] zgloszenia = new float[zgloszeniaList.size()];
		for (int i = 0; i < zgloszenia.length; i++) {
			zgloszenia[i] = zgloszeniaList.get(i);
		}
		Arrays.sort(zgloszenia);
		
		for (int i = 0; i < result.length; i++) {
			if (serwer.getKolejka(i).getSredniCzasOczekiwania() <= serwer.getKolejka(i).getMaxCzasOczekiwania()) {
				result[i] = Arrays.binarySearch(zgloszenia, getMiara(serwer, i)) + 1;
			} else {
				result[i] = 0; 
			}
		}
		
		return result;
	}
	
	protected Float getMiara(Serwer serwer, int kolejka) {
		return (float)serwer.getKolejka(kolejka).getIloscZgloszen();
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
				if (stan[i] == 0) { ogr1++; }
				if (innyStan[i] == 0) { ogr2++; }
			}
			
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
			result[i] = serwer.getIloscKolejek() + 1; // 1+ wynika z tego ze stan 0 zarezerwowany jest dla przekroczenia ograniczenia czasowego
		}

		return result;
	}

	public float compareWspolczynnik(Serwer serwer, int[] innyStan) {
		float result = 0;
		
		if (compare(serwer, innyStan) != Porownanie.NieMoznaPorownac) {
			int[] stan = getStan(serwer); 
			int ogr1 = 0;
			int ogr2 = 0;
			for (int i = 0; i < stan.length; i++) {
				if (stan[i] == 0) { ogr1++; }
				if (innyStan[i] == 0) { ogr2++; }
			}
			
			result = -(ogr1 - ogr2);
		}
		return result;
	}
	
}

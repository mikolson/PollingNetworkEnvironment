package pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaOceny.routery;

import pl.wroc.pwr.iis.polling.model.object.IStan.Porownanie;
import pl.wroc.pwr.iis.polling.model.object.polling.Serwer;

/**
 * Ocena Rtime to ocena ktora uwzglednia:
 * 
 * @author Misiek
 */
public class OcenaTimeRosnacoTuning_WLASNA extends OcenaTimeRosnacoOrginalna {
	// Zapisany poprzedni stan 
	
	// Parametry oceny

	private int[] poprzedniStan = new int[0];

	public OcenaTimeRosnacoTuning_WLASNA(float C1, float C2, float C3) {
		super(C1, C2, C3);
	}

	/**
	 * @return Zwraca wartość jeżeli nastąpiła poprawa stanu
	 */
	protected float getR_stan(Serwer serwer) {
		float result = 0;
		Porownanie p = serwer.getReprezentacjaStanu().compare(serwer, poprzedniStan);
		
		// pomnozenie wspolczynnika c3 przez roznice
		// pomiędzy stanami
		if(p != Porownanie.NieMoznaPorownac) {
			result = c3 *  serwer.getReprezentacjaStanu().compareWspolczynnik(serwer, poprzedniStan);
		}
		
		return result;
	}
}
 
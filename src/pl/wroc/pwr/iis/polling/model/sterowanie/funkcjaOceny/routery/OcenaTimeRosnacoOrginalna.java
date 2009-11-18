package pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaOceny.routery;

import pl.wroc.pwr.iis.polling.model.object.IStan.Porownanie;
import pl.wroc.pwr.iis.polling.model.object.polling.Kolejka;
import pl.wroc.pwr.iis.polling.model.object.polling.Serwer;
import pl.wroc.pwr.iis.polling.model.sterowanie.FunkcjaOceny_I;
import pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaOceny.OcenaSytuacji_A;

/**
 * Ocena Rtime to ocena ktora uwzglednia:
 * 
 * @author Misiek
 */
public class OcenaTimeRosnacoOrginalna extends OcenaSytuacji_A implements FunkcjaOceny_I {
	// Zapisany poprzedni stan 
	
	// Parametry oceny
	protected final float c1; //
	protected final float c2; //
	protected final float c3; // 

	private int[] poprzedniStan = new int[0];

	public OcenaTimeRosnacoOrginalna(float C1, float C2, float C3) {
		c1 = C1;
		c2 = C2;
		c3 = C3;
	}

	/* 
	 * Średnia ważona ilości zgłoszeń
	 * @see pl.wroc.pwr.iis.polling.model.ocena.ModulOceniajacy#ocenaSytuacji(pl.wroc.pwr.iis.polling.model.object.Serwer)
	 */
	public float ocenaSytuacji(Serwer serwer) {
		float r_time = 0;
		
		for (int i = 0; i < serwer.getIloscKolejek() - 1; i++) {
			Kolejka kolejka = serwer.getKolejka(i);
			
			float r_time_i = 0; 
			int R =  kolejka.getMaxCzasOczekiwania();
			float M = kolejka.getSredniCzasOczekiwania();
//			float M = kolejka.getCzasOczekiwania();
			
			if(M <= R) {
				r_time_i = (c1 * M) /R;
			} else {
				r_time_i = -c2;
			}
			
			float W = kolejka.getWaga();
			r_time += r_time_i * W;
		}
		
		float r_stan = getR_stan(serwer);
		
		//Zapisanie stanu
		this.poprzedniStan = serwer.getReprezentacjaStanu().getStan(serwer);
		return r_time + r_stan;
	}

	/**
	 * @return Zwraca wartość jeżeli nastąpiła poprawa stanu
	 */
	protected float getR_stan(Serwer serwer) {
		float result = 0;
		Porownanie p = serwer.getReprezentacjaStanu().compare(serwer, poprzedniStan);
		 
		if(p == Porownanie.Lepszy) {
			result = c3;
		}
		
		return result;
	}
}
 
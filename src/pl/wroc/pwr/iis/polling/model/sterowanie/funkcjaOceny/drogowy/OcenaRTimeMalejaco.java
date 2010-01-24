package pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaOceny.drogowy;

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
public class OcenaRTimeMalejaco extends OcenaSytuacji_A implements FunkcjaOceny_I {
	// Zapisany poprzedni stan 
	
	// Parametry oceny
	private final float c1; //
	private final float c2; //
	private final float c3; // 

	private int[] poprzedniStan = new int[0];

	public OcenaRTimeMalejaco(float C1, float C2, float C3) {
		c1 = C1;
		c2 = C2;
		c3 = C3;
	}

	/* 
	 * Średnia ważona ilości zgłoszeń
	 * @see pl.wroc.pwr.iis.polling.model.ocena.ModulOceniajacy#ocenaSytuacji(pl.wroc.pwr.iis.polling.model.object.Serwer)
	 */
	public double ocenaSytuacji(Serwer serwer) {
		double r_time = 0;
		
		for (int i = 0; i < serwer.getIloscKolejek(); i++) {
			Kolejka kolejka = serwer.getKolejka(i);
			
			double r_time_i = 0; 
			double R =  kolejka.getMaxCzasOczekiwania();
			double M = kolejka.getSredniCzasOczekiwania();
			
			if(M <= R) {
				r_time_i = c1 * (R-M)/R;
			} else {
				r_time_i = -c2;
			}
			
			float W = kolejka.getWaga();
			r_time += r_time_i * W;
		}
		
		double r_stan = getR_stan(serwer);
		
		//Zapisanie stanu
		this.poprzedniStan = serwer.getReprezentacjaStanu().getStan(serwer);
		
		return r_time + r_stan;
	}

	/**
	 * TODO : dopisac metode
	 * @return
	 */
	private float getR_stan(Serwer serwer) {
		float result = 0;
		Porownanie p = serwer.getReprezentacjaStanu().compare(serwer, poprzedniStan);
		 
		if(p == Porownanie.Lepszy) {
			result = c3;
		}
		
		return result;
	}

	protected int czasAvgOczekiwania(Kolejka k) {
		int result = 0;
		
		for (int i = 0; i < k.getIloscZgloszen(); i++) {
			result += k.getCzasOczekiwania(i);
		}
		
		return result;
	}
}
 
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
public class OcenaTimeRosnacoOrginalna4CzasOczekiwaniaPierwszego extends OcenaSytuacji_A implements FunkcjaOceny_I {
	// Zapisany poprzedni stan 
	
	// Parametry oceny
	protected final float c1; //
	protected final float c2; //
	protected final float c3; // 

	private int[] poprzedniStan = new int[0];

	private double maxR = 10000;
	private final float c4;
	
	public OcenaTimeRosnacoOrginalna4CzasOczekiwaniaPierwszego(float C1, float C2, float C3, float C4) {
		c1 = C1;
		c2 = C2;
		c3 = C3;
		c4 = C4;
	}

	/* 
	 * Średnia ważona ilości zgłoszeń
	 * @see pl.wroc.pwr.iis.polling.model.ocena.ModulOceniajacy#ocenaSytuacji(pl.wroc.pwr.iis.polling.model.object.Serwer)
	 */
	public double ocenaSytuacji(Serwer serwer) {
		float r_time = 0;
		for (int i = 0; i < serwer.getIloscKolejek()-1; i++) {
			Kolejka kolejka = serwer.getKolejka(i);
			
			double r_time_i = 0; 
			double R =  kolejka.getMaxCzasOczekiwania();
//			float M = kolejka.getSredniCzasOczekiwania();
			double M = kolejka.getCzasOczekiwania();
			
			if(M <= R) {
				r_time_i = c1 * M / (-3.0 * R * R) + 4.0/3.0*c1;
			} else {
				r_time_i = c2/R * M * -.5;
//				r_time_i = -0.1*(c2*M/R);
			}
			
			float W = kolejka.getWaga();
			r_time += r_time_i * W;
		}
		Kolejka kolejka = serwer.getKolejka(serwer.getIloscKolejek()-1);
//		float Mr = kolejka.getSredniCzasOczekiwania();
		double Mr = kolejka.getCzasOczekiwania();
		
		maxR = Math.max(Mr, maxR);
//		float M = kolejka.getSredniCzasOczekiwania();
		double M = kolejka.getCzasOczekiwania();
		
//		double d = ((-c4*M/maxR) + c4);
		double d = -M/(float)c4;
		
		
		r_time += d;
		
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
		
//		if(p != Porownanie.NieMoznaPorownac) {
//			result = c3 *  serwer.getReprezentacjaStanu().compareWspolczynnik(serwer, poprzedniStan);
//		}
		
		return result;
	}
}
 
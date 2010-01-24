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
public class OcenaTimeRosnacoOrginalna2 extends OcenaSytuacji_A implements FunkcjaOceny_I {
	// Zapisany poprzedni stan 
	
	// Parametry oceny
	protected final float c1; //
	protected final float c2; //
	protected final float c3; // 

	private int[] poprzedniStan = new int[0];

	private double maxR = 10000;
	private final double c4;
	
	public OcenaTimeRosnacoOrginalna2(float C1, float C2, float C3, float C4) {
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
			double M = kolejka.getSredniCzasOczekiwania();
			
			if(M <= R) {
				double wykladnik = 1;
				r_time_i = c1/Math.pow(R,wykladnik) * Math.pow(M,wykladnik);//(c1 * M) /R;
				r_time_i = r_time_i / c1;
				
				wykladnik = 0.5;
				r_time_i = Math.pow(r_time_i, wykladnik);
				r_time_i = r_time_i * c1;
				if (r_time_i < 0.001) {
					r_time_i = 0;
				}
			} else {
				double wykladnik = 0.5;
				r_time_i = -Math.pow(c2/R * M, wykladnik);
//				r_time_i = -c2;
			}
			
			float W = kolejka.getWaga();
			r_time += r_time_i * W;
		}
		
		Kolejka kolejka = serwer.getKolejka(serwer.getIloscKolejek()-1);
		maxR = Math.max(kolejka.getSredniCzasOczekiwania(), maxR);
		double M = kolejka.getSredniCzasOczekiwania();
		
		double d = ((-c4*M/maxR) + c4) / serwer.getIloscKolejek();
		d = d / (c4/serwer.getIloscKolejek());
		d = Math.pow(d, 2)*c4;
//		System.out.println("OcenaTimeRosnacoOrginalna2.ocenaSytuacji(): " + d + "\t " + maxR);
		
//		if (! zlamaneOgraniczenie)
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
		
		return result;
	}
}
 
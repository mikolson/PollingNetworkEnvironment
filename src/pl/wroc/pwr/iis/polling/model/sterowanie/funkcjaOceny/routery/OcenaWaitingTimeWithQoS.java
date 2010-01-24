package pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaOceny.routery;

import pl.wroc.pwr.iis.polling.model.object.polling.Kolejka;
import pl.wroc.pwr.iis.polling.model.object.polling.Serwer;
import pl.wroc.pwr.iis.polling.model.sterowanie.FunkcjaOceny_I;
import pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaOceny.OcenaSytuacji_A;

/**
 * Funkcja zwracają
 * 
 * @author Misiek
 */
public class OcenaWaitingTimeWithQoS extends OcenaSytuacji_A implements FunkcjaOceny_I {
	static int call = 0;
	public OcenaWaitingTimeWithQoS() {
		
	}

	public double ocenaSytuacji(Serwer serwer) {
		call++;
		float result = 0;
	
//		System.out.println("-----------------------------");
//		System.out.print("iter: " + call + "  ->");
		
		boolean przekroczenie = false;
		
		for (int i = 0; i < serwer.getIloscKolejek()-1 ; i++) {
			Kolejka kolejka = serwer.getKolejka(i);
			
			double W_max =  kolejka.getMaxCzasOczekiwania(); // QoS czasu oczekiwania
			double w = kolejka.getCzasOczekiwania();
//			float w = kolejka.getSredniCzasOczekiwania();
			
			if (w > kolejka.getMaxCzasOczekiwania()) {
				przekroczenie = true;
			}
			
//			System.out.print("  (" + i + ") w:" + w + " max:" + W_max + "   ");
			
			if (w < W_max) {
				w = w/10;
					
			} else {
				w = -(w*w); //W_max-w;
			}
			result += w;
		}
		
		
		Kolejka kolejka = serwer.getKolejka(serwer.getIloscKolejek()-1);
//		float W_max =  kolejka.getMaxCzasOczekiwania(); // QoS czasu oczekiwania
//		float w = kolejka.getCzasOczekiwania();
		double w = kolejka.getCzasOczekiwania();
		
//		if (przekroczenie) {
			result += -w/100;
//		}
		
//		System.out.print("  (" + (serwer.getIloscKolejek()-1) + ") w:" + w + " max:" + kolejka.getMaxCzasOczekiwania() + "   ");
//		System.out.println();
//		System.out.println("  Result: " + result);
//		System.out.println("-----------------------------");
		return  result;   
	}

}
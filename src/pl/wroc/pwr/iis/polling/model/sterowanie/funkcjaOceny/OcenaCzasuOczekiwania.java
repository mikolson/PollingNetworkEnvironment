package pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaOceny;

import pl.wroc.pwr.iis.polling.model.object.polling.Kolejka;
import pl.wroc.pwr.iis.polling.model.object.polling.Serwer;
import pl.wroc.pwr.iis.polling.model.sterowanie.FunkcjaOceny_I;

public class OcenaCzasuOczekiwania extends OcenaSytuacji_A implements FunkcjaOceny_I {

	public OcenaCzasuOczekiwania(float kara) {
		this.kara = kara;
	}

	/* 
	 * Średnia ważona ilości zgłoszeń
	 * @see pl.wroc.pwr.iis.polling.model.ocena.ModulOceniajacy#ocenaSytuacji(pl.wroc.pwr.iis.polling.model.object.Serwer)
	 */
	public double ocenaSytuacji(Serwer serwer) {
		float result = 0;
		float wagi = 0;
		
		for (int i = 0; i < serwer.getIloscKolejek(); i++) {
			Kolejka kolejka = serwer.getKolejka(i);
			result += czasOczekiwania(kolejka) * kolejka.getWaga();
			wagi += kolejka.getWaga();
			
			result += doliczKare(kolejka);
		}
		
		if (wagi == 0) {
			result = 0;
		} else {
			result /= wagi;
		}
		
		return result;
	}

	
	protected int czasOczekiwania(Kolejka k) {
		int result = 0;
		
		for (int i = 0; i < k.getIloscZgloszen(); i++) {
			result += k.getCzasOczekiwania(i);
		}
		
		return result;
	}
}
 
package pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaOceny;

import pl.wroc.pwr.iis.polling.model.object.polling.Kolejka;
import pl.wroc.pwr.iis.polling.model.object.polling.Serwer;
import pl.wroc.pwr.iis.polling.model.sterowanie.FunkcjaOceny_I;

public class OcenaIlosciZgloszen extends OcenaSytuacji_A implements FunkcjaOceny_I {

	public OcenaIlosciZgloszen(float kara) {
		super();
		this.kara = kara;
	}

	/* 
	 * Średnia ważona ilości zgłoszeń
	 * @see pl.wroc.pwr.iis.polling.model.ocena.ModulOceniajacy#ocenaSytuacji(pl.wroc.pwr.iis.polling.model.object.Serwer)
	 */
	public float ocenaSytuacji(Serwer serwer) {
		float result = 0;
		float wagi = 0;
		
		for (int i = 0; i < serwer.getIloscKolejek(); i++) {
			Kolejka kolejka = serwer.getKolejka(i);
			result += kolejka.getIloscZgloszen() * kolejka.getWaga();
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

}

package pl.wroc.pwr.iis.polling.model.sterowanie.deterministyczne;

import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Sterownik;
import pl.wroc.pwr.iis.polling.model.sterowanie.strategie.Strategia_A;

/**
 * TODO nie działa jeszcze
 * @author Misiek
 *
 */
public class SterownikIlosciowy extends Sterownik {
	int ostatniaAkcja = 0;
	
	public SterownikIlosciowy(Strategia_A strategia) {
		super(strategia);
	}
	
	
	@Override
	public int getDecyzjaSterujaca(double ocenaStanu, int[] stan, int iloscAkcji) {
		int akcja = (ostatniaAkcja++) % iloscAkcji;
		return akcja;
	}

}

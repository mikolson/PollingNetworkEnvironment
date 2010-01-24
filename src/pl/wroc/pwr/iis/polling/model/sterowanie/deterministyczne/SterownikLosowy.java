package pl.wroc.pwr.iis.polling.model.sterowanie.deterministyczne;

import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Sterownik;
import pl.wroc.pwr.iis.rozklady.random.RandomGenerator;

/**
 * TODO nie działa jeszcze
 * @author Misiek
 *
 */
public class SterownikLosowy extends Sterownik {
	
	public SterownikLosowy() {
		super(null);
	}
	
	
	@Override
	public int getDecyzjaSterujaca(double ocenaStanu, int[] stan, int iloscAkcji) {
		int akcja = (int)(RandomGenerator.getDefault().nextInt(iloscAkcji));
		return akcja;
	}

}

package pl.wroc.pwr.iis.polling.model.sterowanie.deterministyczne;

import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Sterownik;

public class SterownikNaZmiane extends Sterownik {
	int ostatniaAkcja = 0;
	
	public SterownikNaZmiane() {
		super(null);
	}
	
	
	@Override
	public int getDecyzjaSterujaca(float ocenaStanu, int[] stan, int iloscAkcji) {
		int akcja = (ostatniaAkcja++) % iloscAkcji;
		return akcja;
	}

}

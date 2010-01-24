/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.normal;

import pl.wroc.pwr.iis.polling.model.object.polling.Kolejka;
import pl.wroc.pwr.iis.polling.model.object.polling.Serwer;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Sterownik;


/**
 * Obsługuje kolejkę z nawiększą liczbą zgłoszeń 
 * i obsługuje ją aż do momentu kiedy stanie się pusta
 *  
 * @author Michał Stanek
 */
public class ControlRequestsF extends Sterownik {
	private int akcja = 0;
	private final Serwer serwer;
	
	public ControlRequestsF(Serwer serwer) {
		super(null);
		this.serwer = serwer;
	}

	private int selectLongest() {
		int max = Integer.MIN_VALUE;
		int number = 0;
		for (int i = 0; i < serwer.getIloscKolejek(); i++) {
			Kolejka k = serwer.getKolejka(i);
			int w = k.getIloscZgloszen();
			if (w > max) {
				max = w;
				number = i;
			}
		}
		return number;
	}
	
	@Override
	public int getDecyzjaSterujaca(float ocenaStanu, int[] stan, int iloscAkcji) {
		int zgloszen = serwer.getKolejka(akcja).getIloscZgloszen();
		if (zgloszen == 0) { 
			akcja = selectLongest(); 
		} 
		return akcja;
	}
	
}

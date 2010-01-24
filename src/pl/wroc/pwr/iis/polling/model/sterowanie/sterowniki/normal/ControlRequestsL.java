/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.normal;

import java.util.Random;

import pl.wroc.pwr.iis.polling.model.object.polling.Kolejka;
import pl.wroc.pwr.iis.polling.model.object.polling.Serwer;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Sterownik;


/**
 * Obsługuje kolejkę z nawiększą liczbą zgłoszeń 
 * i obsługuje ją do momentu kiedy nie
 *  
 * @author Misiek
 */
public class ControlRequestsL extends Sterownik {
	private final int LENGTH;
	
	private int action = 0;
	private final Serwer serwer;
	private int l = 0;
	
	public ControlRequestsL(Serwer serwer, int length) {
		super(null);
		this.serwer = serwer;
		this.LENGTH = length;
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
		int result = action;
		
		if (l++ > LENGTH) {
			action = selectLongest();
			l = 0;
		} 
		
		return result;
	}
	
}

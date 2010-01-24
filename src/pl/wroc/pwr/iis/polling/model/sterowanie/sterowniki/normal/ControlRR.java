/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.normal;

import pl.wroc.pwr.iis.polling.model.sterowanie.ZdarzenieKolejki;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Sterownik;

/**
 * Round Robin Control Strategy
 * Zdarzenie: ukonczenie kolejki
 * @author Misiek
 */
public class ControlRR extends Sterownik {
	private int action = 0;
	
	public ControlRR() {
		super(null);
	}

	@Override
	public int getDecyzjaSterujaca(double ocenaStanu, int[] stan, int iloscAkcji) {
		action = (action + 1) % iloscAkcji;
//		System.out.println("Akcja: " + action);
		return action;
	}

	@Override
	public ZdarzenieKolejki getDecyzjaNaZdarzenie() {
		return ZdarzenieKolejki.ZGLOSZENIE;
	}
	
}

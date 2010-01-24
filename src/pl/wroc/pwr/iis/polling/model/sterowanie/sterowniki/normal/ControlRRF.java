/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.normal;

import pl.wroc.pwr.iis.polling.model.object.polling.Serwer;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Sterownik;

/**
 * Round Robin Control Strategy - switch after empty queue
 * @author Misiek
 */
public class ControlRRF extends Sterownik {
	private int akcja = 0;
	private final Serwer serwer;
	
	public ControlRRF(Serwer serwer) {
		super(null);
		this.serwer = serwer;
	}

	@Override
	public int getDecyzjaSterujaca(float ocenaStanu, int[] stan, int iloscAkcji) {
		int iloscZgloszen = serwer.getKolejka(akcja).getIloscZgloszen();
		
		if (iloscZgloszen == 0) {
			akcja = (akcja + 1) % iloscAkcji;
		}
		
		return akcja;
	}
	
}

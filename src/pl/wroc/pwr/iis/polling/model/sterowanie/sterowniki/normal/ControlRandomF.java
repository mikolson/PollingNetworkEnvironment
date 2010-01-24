/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.normal;

import java.util.Random;

import pl.wroc.pwr.iis.polling.model.object.polling.Serwer;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Sterownik;

/**
 * Random Queue Selection and service till queue is empty
 * 
 * @author Michał Stanek <michal.stanek@pwr.wroc.pl>
 */
public class ControlRandomF extends Sterownik {
	private final Random generator = new Random();
	private final Serwer serwer;
	int akcja = 0;
	
	public ControlRandomF(Serwer serwer) {
		super(null);
		this.serwer = serwer;
	}

	@Override
	public int getDecyzjaSterujaca(float ocenaStanu, int[] stan, int iloscAkcji) {
		if (serwer.getKolejka(akcja).getIloscZgloszen() == 0) {
			akcja = generator.nextInt(iloscAkcji);
		}
		return akcja; 
	}
	
}

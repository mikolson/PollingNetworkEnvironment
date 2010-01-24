/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.normal;

import java.util.Random;

import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Sterownik;


/**
 * Losowy wybór kolejki
 * @author Misiek
 */
public class ControlRandom extends Sterownik {
	private static final int BRAK_USTAWIONEJ_WARTOSCI = -1;
	Random generator = new Random();
	
	protected int 	poprzedniStan = BRAK_USTAWIONEJ_WARTOSCI;
	public ControlRandom() {
		super(null);
	}

	@Override
	public int getDecyzjaSterujaca(float ocenaStanu, int[] stan, int iloscAkcji) {
		return generator.nextInt(iloscAkcji);
	}
	
}

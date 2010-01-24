/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.normal;

import pl.wroc.pwr.iis.polling.model.sterowanie.ZdarzenieKolejki;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Sterownik;


/**
 * Obsługa tylko wybranej kolejki ze wszystkich kolejek
 * @author Misiek
 */
public class ControlOneQueue extends Sterownik {
	private static final int BRAK_USTAWIONEJ_WARTOSCI = -1;
	private int action = 0;
	protected int poprzedniStan = BRAK_USTAWIONEJ_WARTOSCI;
	public ControlOneQueue(int action) {
		super(null);
		this.action = action;
	}

	@Override
	public int getDecyzjaSterujaca(double ocenaStanu, int[] stan, int iloscAkcji) {
		return action;
	}

	@Override
	public ZdarzenieKolejki getDecyzjaNaZdarzenie() {
		return ZdarzenieKolejki.ZGLOSZENIE;
	}
}

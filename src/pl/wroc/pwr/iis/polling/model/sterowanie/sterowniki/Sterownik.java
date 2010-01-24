/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki;

import pl.wroc.pwr.iis.polling.model.sterowanie.Sterownik_I;
import pl.wroc.pwr.iis.polling.model.sterowanie.strategie.Strategia_A;

/**
 * @author Michał Stanek
 */
public abstract class Sterownik implements Sterownik_I {
	protected final Strategia_A strategia;

	public Sterownik(Strategia_A strategia) {
		super();
		this.strategia = strategia;
	}

	public boolean bylShake() {
		return false;
	}
	
	public int getDecyzjaSterujaca(float ocenaStanu, int[] stan, int iloscAkcji) {
		return strategia.getAkcja(stan, iloscAkcji);
	}
	
	public Strategia_A getStrategia() {
		return strategia;
	}	
	
	public int koniecSterowania() {
		return 0;
	}

	public int startSterowania() {
		return 0;
	}
	
	@Override
	public String toString() {
		return "";
	}
	
	public String toStringHeader() {
		return "Brak naglowka";
	}
}

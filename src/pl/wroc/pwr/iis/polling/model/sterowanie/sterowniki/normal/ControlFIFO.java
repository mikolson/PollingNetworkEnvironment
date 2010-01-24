/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.normal;

import pl.wroc.pwr.iis.polling.model.object.polling.Kolejka;
import pl.wroc.pwr.iis.polling.model.object.polling.Serwer;
import pl.wroc.pwr.iis.polling.model.sterowanie.ZdarzenieKolejki;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Sterownik;


/**
 * Obsługiwane zgłoszenie z najdłuższym czasem oczekiwania
 * @author Misiek
 */
public class ControlFIFO extends Sterownik {
	private final Serwer serwer;
	
	public ControlFIFO(Serwer serwer) {
		super(null);
		this.serwer = serwer;
	}

	@Override
	public int getDecyzjaSterujaca(double ocenaStanu, int[] stan, int iloscAkcji) {
		double max = Integer.MIN_VALUE;
		int number = 0;
		for (int i = 0; i < serwer.getIloscKolejek(); i++) {
			Kolejka k = serwer.getKolejka(i);
			if (k.getCzasOczekiwania() > max) {
				max = k.getCzasOczekiwania();
				number = i;
			}
		}
//		System.out.println("Decyzja sterująca:" + number);
		return number;
	}

	@Override
	public ZdarzenieKolejki getDecyzjaNaZdarzenie() {
		return ZdarzenieKolejki.ZGLOSZENIE;
	}
}

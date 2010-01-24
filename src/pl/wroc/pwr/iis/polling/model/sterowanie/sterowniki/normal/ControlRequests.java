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
 * @author Misiek
 */
public class ControlRequests extends Sterownik {
	private static final int BRAK_USTAWIONEJ_WARTOSCI = -1;
	private int action = 0;
	protected int 	poprzedniStan = BRAK_USTAWIONEJ_WARTOSCI;
	private final Serwer serwer;
	public ControlRequests(Serwer serwer) {
		super(null);
		this.serwer = serwer;
	}

	@Override
	public int getDecyzjaSterujaca(float ocenaStanu, int[] stan, int iloscAkcji) {
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
//		System.out.println("Decyzja sterująca:" + number);
		return number;
	}
	
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.normal;

import pl.wroc.pwr.iis.polling.model.object.polling.Kolejka;
import pl.wroc.pwr.iis.polling.model.object.polling.Serwer;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Sterownik;


/**
 * W pierwszej kolejności przeszukiwane są kolejki 
 * Obsługiwane zgłoszenie z najdłuższym czasem oczekiwania
 * 
 * @author Misiek
 */
public class ControlFIFO_QoS extends Sterownik {
	private int action = 0;
	private final Serwer serwer;
	public ControlFIFO_QoS(Serwer serwer) {
		super(null);
		this.serwer = serwer;
	}

	@Override
	public int getDecyzjaSterujaca(float ocenaStanu, int[] stan, int iloscAkcji) {
		int max = Integer.MIN_VALUE;
		int number = -1;
		
		// Znajdź kolejkę z największym przekroczeniem czasu
		for (int i = 0; i < serwer.getIloscKolejek(); i++) {
			Kolejka k = serwer.getKolejka(i);
			
			if (k.getCzasOczekiwania() > k.getMaxCzasOczekiwania()) {
				int diff = k.getCzasOczekiwania() - k.getMaxCzasOczekiwania();
				if (max < diff) {
					max = diff;
					number = i;
				}
			}
		}
		
		// Jeżeli nie było kolejki z przekroczeniem wybierz kolejkę z najdłuższym czasem oczekiwania
		if (number < 0) {
			for (int i = 0; i < serwer.getIloscKolejek(); i++) {
				Kolejka k = serwer.getKolejka(i);
				
				if (k.getCzasOczekiwania() > max) {
					max = k.getCzasOczekiwania();
					number = i;
				}
			}
		}
		
		return number;
	}
	
}

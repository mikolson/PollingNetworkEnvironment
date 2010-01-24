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
 * Strategia Smart EDF - earliest deadline first
 * 
 * 
 * @author Misiek
 */
public class ControlSmartEDF extends Sterownik {
	private final Serwer serwer;
	
	public ControlSmartEDF(Serwer serwer) {
		super(null);
		this.serwer = serwer;
	}
	
	@Override
	public int getDecyzjaSterujaca(float ocenaStanu, int[] stan, int iloscAkcji) {
		int min = Integer.MAX_VALUE;
		int number = 0;
		
		
		for (int i = 0; i < serwer.getIloscKolejek(); i++) {
			Kolejka k = serwer.getKolejka(i);
			int edf = k.getMaxCzasOczekiwania() - k.getCzasOczekiwania();
			if (edf < min) {
				min = edf;
				number = i;
			}
		}
		
//		System.out.println("Decyzja sterująca:" + number);
		return number;
	}
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.normal;

import pl.wroc.pwr.iis.polling.model.object.polling.Kolejka;
import pl.wroc.pwr.iis.polling.model.object.polling.Serwer;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Sterownik;


/**
 * Strategia EDF - earliest deadline first
 * 
 * Uwaga - 
 * tutaj jest bardzo ważne w jaki sposób podejdziemy do problemu
 * obliczania EDF dla kolejki Best--Effort. O ile prostym wydaje się policzenie 
 * tego parametry dla każdej z kolejek z podanym ograniczeniem czasowym
 * o tyle policzenie tego parametru dla kolejki BEST-EFFort oparte jest o heurystykę 
 * 
 * @author Misiek
 */
public class ControlEDF extends Sterownik {
	private final Serwer serwer;
	
	public ControlEDF(Serwer serwer) {
		super(null);
		this.serwer = serwer;
	}
	
	@Override
	public int getDecyzjaSterujaca(float ocenaStanu, int[] stan, int iloscAkcji) {
		double min = Integer.MAX_VALUE;
		int number = 0;
		
		for (int i = 0; i < serwer.getIloscKolejek(); i++) {
			Kolejka k = serwer.getKolejka(i);
			double edf = k.getMaxCzasOczekiwania() - k.getCzasOczekiwania();
			if (edf < min) {
				min = edf;
				number = i;
			}
		}
//		System.out.println("Decyzja sterująca:" + number);
		return number;
	}
}

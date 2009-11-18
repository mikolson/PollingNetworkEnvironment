/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.MC.online;

import java.util.ArrayList;

import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Sterownik;
import pl.wroc.pwr.iis.polling.model.sterowanie.strategie.Strategia_A;


/**
 * @author Misiek
 */
public class SterownikFirstVisitMC extends Sterownik {
	private class StanAkcja {
		public final int stan;
		public final int akcja;
		
		public StanAkcja(int stan, int akcja) {
			this.stan = stan;
			this.akcja = akcja;
		}
	}
	
	/** Wartosci nagrod jakie system uzyskal */
	ArrayList<Float> nagrody = new ArrayList<Float>();
	
	/** Numery stanow w jakich znalazl sie system */
	ArrayList<StanAkcja> stany = new ArrayList<StanAkcja>();

	public SterownikFirstVisitMC(Strategia_A strategia) {
		super(strategia);
	}

	@Override
	public int getDecyzjaSterujaca(float ocenaStanu, int[] stan, int iloscAkcji) {
		int akcja = strategia.getAkcja(stan, iloscAkcji);
		int numerStanu = strategia.getNumerStanu(stan);
		
		if (! exist(numerStanu)) {
			stany.add(new StanAkcja(numerStanu, akcja));
		}
		
		nagrody.add(ocenaStanu);
		
		return akcja;
	}

	/**
	 * Sprawdza czy stan o zadanym numerze znajduje sie juz na liscie stanow.
	 * Jezeli tak to zwraca TRUE
	 */
	private boolean exist(int numerStanu) {
		boolean result = false; 
		for (int i = 0; i < stany.size(); i++) {
			if (stany.get(i).stan == numerStanu) {
				result = true;
				break;
			}
		}
		return result;
	}

	@Override
	public int koniecSterowania() {
		//		r0, sa1
		//		r1, sa2
		//		r2, sa3
		//		0
		
		float sumaNagrod = 0;
		for (int i = this.stany.size() - 1; i >= 0; i--) {
			strategia.aktualizuj(sumaNagrod, stany.get(i).stan, stany.get(i).akcja);
			sumaNagrod += nagrody.get(i); // musi to byc za aktualizacja w przciwnym razie brany pod uwage bedzie wynik nie tej akcji
		}
		return 0;
	}

	@Override
	public int startSterowania() {
		nagrody.clear();
		stany.clear();

		return 0;
	}

}

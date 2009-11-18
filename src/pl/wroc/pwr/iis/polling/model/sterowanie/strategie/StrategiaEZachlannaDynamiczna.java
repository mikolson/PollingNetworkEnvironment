package pl.wroc.pwr.iis.polling.model.sterowanie.strategie;

import pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaWartosci.FunkcjaWartosciAkcji;

/**
 * Strategia, która zapewnia, że każda akcja, która nie jest optymalna 
 * może być wybrana z pewnym małym prawdopodobieństwem epsilon/|A|, gdzie
 * epsilon jest współczynnikiem miękkości strategii, a |A| ilością dostępnych
 * akcji 
 * 
 * @author Misiek
 */
public class StrategiaEZachlannaDynamiczna extends StrategiaEZachlanna {
	protected FunkcjaWartosciAkcji Q;
	
	private final float eStart;
	private final float eKoniec;
	private final float iteracji;
	
	private float aktualnaIteracja=1;
	
	
	public StrategiaEZachlannaDynamiczna(float eStart, float eKoniec, float iteracji, int[] maxStanow, int akcji) {
		super(eStart, maxStanow, akcji);
		this.eStart = eStart;
		this.eKoniec = eKoniec;
		this.iteracji = iteracji;
	}

	@Override
	public int getAkcja(int[] stan, int iloscAkcji) {
		if (aktualnaIteracja >= iteracji) {
			zachlannoscE = eKoniec;
		} else {
			zachlannoscE = ((eKoniec - eStart) / iteracji)  * aktualnaIteracja + eStart;
//			ax + b
//			b = eStart
//			a = (eKoniec - eStart) / Iteracji  
			aktualnaIteracja++;
		}
		return ostatniaAkcja = super.getAkcja(stan, iloscAkcji);
	}
}

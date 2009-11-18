package pl.wroc.pwr.iis.polling.model.sterowanie.strategie;

import pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaWartosci.FunkcjaWartosciAkcji;
import pl.wroc.pwr.iis.rozklady.Losuj;

/**
 * Strategia, która zapewnia, że każda akcja, która nie jest optymalna 
 * może być wybrana z pewnym małym prawdopodobieństwem epsilon/|A|, gdzie
 * epsilon jest współczynnikiem miękkości strategii, a |A| ilością dostępnych
 * akcji 
 * 
 * @author Misiek
 */
public class StrategiaEZachlanna extends Strategia_A {
	protected FunkcjaWartosciAkcji Q;
	protected float zachlannoscE = .2f;
	
	
	public StrategiaEZachlanna(int[] maxStanow, int akcji) {
		super(maxStanow, akcji);
		Q = new FunkcjaWartosciAkcji(getIloscStanow(), akcji);
		Q.zainicjujLosowo();
	}
	
	public StrategiaEZachlanna(float zachlannoscE, int[] maxStanow, int akcji) {
		this(maxStanow, akcji);
		setZachlannoscE(zachlannoscE);
	}

	@Override
	public int getAkcja(int[] stan, int iloscAkcji) {
		float prawdAkcji =  zachlannoscE/iloscAkcji;
		float prawdMaxAkcji = 1 - zachlannoscE + prawdAkcji;
		int numerStanu = getNumerStanu(stan);
		FunkcjaWartosciAkcji.Akcja maxAkcja = Q.getMaxAkcja(numerStanu , iloscAkcji);

		double[] losy = new double[iloscAkcji];
		
		for (int i = 0; i < losy.length; i++) {
			losy[i] = prawdAkcji;
		}
		losy[maxAkcja.numer] = prawdMaxAkcji; 
		
		ostatniStan = numerStanu;
		return ostatniaAkcja = Losuj.losujElement(losy);
	}

	public float getZachlannoscE() {
		return zachlannoscE;
	}

	public void setZachlannoscE(float zachlannoscE) {
		this.zachlannoscE = zachlannoscE;
	}

	@Override
	public void aktualizuj(float sumaNagrod, int stan, int akcja) {
		Q.poprawWartosc(stan, akcja, sumaNagrod);
	}

	@Override
	public FunkcjaWartosciAkcji getFunkcjaWartosciAkcji() {
		return Q;
	}
	
	@Override
	public boolean czyStabilna() {
		return true;
	}

	@Override
	public void setFunkcjaWartosciAkcji(FunkcjaWartosciAkcji newQ) {
		this.Q = newQ;
	}

}

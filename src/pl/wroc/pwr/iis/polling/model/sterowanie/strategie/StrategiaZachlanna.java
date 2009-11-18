package pl.wroc.pwr.iis.polling.model.sterowanie.strategie;

import pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaWartosci.FunkcjaWartosciAkcji;

public class StrategiaZachlanna extends Strategia_A {
	protected FunkcjaWartosciAkcji Q;
	
	public StrategiaZachlanna(int[] maxStanow, int akcji) {
		super(maxStanow, akcji);
		Q = new FunkcjaWartosciAkcji(getIloscStanow(), akcji);
		Q.zainicjujLosowo();
	}

	/* (non-Javadoc)
	 * @see pl.wroc.pwr.iis.polling.model.sterowanie.strategie.Strategia#getAkcja(int[], int)
	 */
	@Override
	public int getAkcja(int[] stan, int iloscAkcji) {
		int numerStanu = getNumerStanu(stan);
		return ostatniaAkcja = Q.getMaxAkcja(numerStanu, iloscAkcji).numer;
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

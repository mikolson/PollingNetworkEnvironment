package pl.wroc.pwr.iis.polling.model.sterowanie.strategie;

import pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaWartosci.FunkcjaWartosciAkcji;
import pl.wroc.pwr.iis.rozklady.Losuj;

/**
 * Strategia Bolzmana wyboru akcji
 * 
 * @author Misiek
 */
public class StrategiaSoftMax extends Strategia_A {
	protected FunkcjaWartosciAkcji Q;
	private float temperatura;
	private float start;
	private final float koniec;
	private float iteracji;
	
	private float aktualnaIteracja=1;
	
	/**
	 * @param maxStanow
	 * @param akcji
	 * @param tStart Temperatura początkowa
	 * @param tKoncowe Temperatura Koncowa
	 * @param tIteracji Ilosc Iteracji spadku liniowego miedzy 
	 */
	public StrategiaSoftMax(float tStart, float tKoncowe, float tIteracji, int[] maxStanow, int akcji) {
		super(maxStanow, akcji);
		start = tStart;
		koniec = tKoncowe;
		iteracji = tIteracji;
		Q = new FunkcjaWartosciAkcji(getIloscStanow(), akcji);
		Q.zainicjujLosowo();
	}
	
	/**
	 * @see pl.wroc.pwr.iis.polling.model.sterowanie.strategie.Strategia_A#getAkcja(int[], int)
	 * W przypadku kiedy z rozkładu Bolzmana wychodzi zbyt mała liczba przyjmowana jest wartosc 0,001
	 */
	@Override
	public int getAkcja(int[] stan, int iloscAkcji) {
		double[] losy = new double[iloscAkcji];
		int numerStanu = getNumerStanu(stan);
		
		if (aktualnaIteracja >= iteracji) {
			temperatura = koniec;
		} else {
			temperatura = aktualnaTemeratura(start, koniec, iteracji, aktualnaIteracja);
			aktualnaIteracja++;
		}

		if (! shake) {
			shake = false;
		}	
		
		double suma = 0;
		for (int i = 0; i < losy.length; i++) {
			losy[i] = Math.max(Math.pow(Math.E, (Q.getWartosc(numerStanu, i) / temperatura)), 0.00001);
			suma += losy[i];
		}
		
		if (Double.isInfinite(suma)) {
			System.out.println("StrategiaSoftMax.getAkcja():" + suma);
		}
		for (int i = 0; i < losy.length; i++) {
			losy[i] = (losy[i] / suma);
		}

		ostatniStan = numerStanu;
		return (ostatniaAkcja = Losuj.losujElement(losy));
	}
	
	private float aktualnaTemeratura(float start,float koniec,float  iteracji,float aktualnaIteracja) {
		return ((koniec - start) / iteracji)  * aktualnaIteracja + start; 
	}

	@Override
	public void aktualizuj(float sumaNagrod, int stan, int akcja) {
		Q.poprawWartosc(stan, akcja, sumaNagrod);
	}
	
	@Override
	public void setShake(float parametr) {
		super.setShake(parametr);
		if (aktualnaIteracja >= iteracji) {
			aktualnaIteracja = iteracji - iteracji * parametr;
			temperatura = aktualnaTemeratura(start, koniec, iteracji, aktualnaIteracja);
		}
	}
	
	@Override
	public boolean czyStabilna() {
		return aktualnaIteracja >= iteracji;
	}

	@Override
	public FunkcjaWartosciAkcji getFunkcjaWartosciAkcji() {
		return Q;
	}

	@Override
	public void setFunkcjaWartosciAkcji(FunkcjaWartosciAkcji newQ) {
		this.Q = newQ;
	}
}

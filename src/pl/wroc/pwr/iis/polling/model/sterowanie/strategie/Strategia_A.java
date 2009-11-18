package pl.wroc.pwr.iis.polling.model.sterowanie.strategie;

import pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaWartosci.FunkcjaWartosciAkcji;


/**
 * @author Misiek
 */
public abstract class Strategia_A {
	public static final int BRAK_USTAWIONEJ_WARTOSCI = -1;
	/**
	 * Maxymalna liczba stanow dla kazdego wymiaru
	 */
	protected final int[] wymiary;
	protected boolean shake;

	protected int ostatniaAkcja = BRAK_USTAWIONEJ_WARTOSCI;
	protected int ostatniStan = BRAK_USTAWIONEJ_WARTOSCI;
	private final int iloscAkcji;
	
	public Strategia_A(int[] maxStanow, int akcji) {
		wymiary = new int[maxStanow.length];
		for (int i = 0; i < maxStanow.length; i++) {
			wymiary[i] = maxStanow[i];
		}
		this.iloscAkcji = akcji;
	}
	
	
	/**
	 * Zwraca akcje jaka nalezy wykonac w stanie
	 * @param stan Stan systemu
	 * @return Akcja jaka powinna byc wykonana dla tego stanu
	 */
	public abstract int getAkcja(int[] stan, int iloscAkcji);
	
	
	public void setShake(float parametr) {
	 	shake = true;
	}
	
	public abstract boolean czyStabilna();
	
	/**
	 * @return Zwraca calkowita liczbe stanow calego systemu
	 */
	public int getIloscStanow() {
		int result = 1;
		for (int i = 0; i < wymiary.length; i++) {
			result *= wymiary[i];
		}
		return result;
	}
	
	/**
	 * Oblicza indeks stanu w jednowymiarowej tablicy wartosci
	 * @param stan Zadany stan
	 * @return
	 */
	public int getNumerStanu(int[] stan) {
		int indeks = 0;
		for (int i = 0; i < stan.length; i++) {
			int zgloszen = stan[i];
			
			
			int mnoznik = 1;
			for (int j = i+1; j < stan.length; j++) {
				mnoznik *= wymiary[j];
			}
			
			indeks += zgloszen * mnoznik;
		}
		return indeks;
	}


	public abstract void aktualizuj(float sumaNagrod, int stan, int akcja);
	public abstract FunkcjaWartosciAkcji getFunkcjaWartosciAkcji();
	public abstract void setFunkcjaWartosciAkcji(FunkcjaWartosciAkcji newQ);
	/**
	 * @return Zwraca liczbę akcji możliwych do wykonania w każdym stanie 
	 */
	public int getIloscAkcji(){
		return iloscAkcji; 
	}


	/**
	 * @return Zwraca akcję która była ostatni raz zwrócona przez strategię wyboru akcji
	 */
	public int getOstatniaAkcja() {
		return ostatniaAkcja;
	}

	public int getOstatniStan() {
		return ostatniStan;
	}
}

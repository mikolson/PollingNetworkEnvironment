package pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaWartosci;

import pl.wroc.pwr.iis.rozklady.random.RandomGenerator;


public class FunkcjaWartosciAkcji extends FunkcjaWartosci {
	public class Akcja {
		public int numer;
		public float wartosc;
	}
	
	protected float min_poprawa = 0.001f;
	
	// Tablice potrzebne do przechowywania klasycznej Q-funkcji
	protected float[][] wartosci; 	// Wartosci Q(s,a)
	protected int[][]   poprawki; 	// Wartosc N(s,a) - ilosc modyfikacji Q(s,a)
	protected int akcji; 			// |A| - ilosc mozliwych do wykonania akcji 
	
	// Wartości potrzebne do śledzenia odchylenia standardowego wynikow
	protected float[][] sredniaObserwacji; 	// Srednia wzmocnien w danym stanie
	protected float[][] sredniaKwadratowObserwacji;  // 
	protected int[][]   poprawkiObserwacji; // Wartosc N(s,a) - ilosc modyfikacji Q(s,a)

	
	public FunkcjaWartosciAkcji(int maxStanow, int akcji, int min_poprawa) {
		this(maxStanow, akcji);
		this.min_poprawa = min_poprawa;
	}
	
	public FunkcjaWartosciAkcji(int maxStanow, int akcji) {
		super();
		System.out.println("FunkcjaWartosciAkcji.FunkcjaWartosciAkcji(): " + maxStanow);
		wartosci = new float[maxStanow][akcji];
		poprawki = new int[maxStanow][akcji];
		this.akcji = akcji;
		
		// inicjalizacja wartosci dla odchylen standardowych i sredniej z obserwacji
		sredniaObserwacji 		   = new float[maxStanow][akcji];
		sredniaKwadratowObserwacji = new float[maxStanow][akcji];
		poprawkiObserwacji 		   = new int[maxStanow][akcji];
	}
	
	/* (non-Javadoc)
	 * @see pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaWartosci.FunkcjaWartosci#zainicjujLosowo()
	 */
	public void zainicjujLosowo() {
		for (int i = 0; i < this.wartosci.length; i++) {
			for (int j = 0; j < this.akcji; j++) {
				wartosci[i][j] = (float) (RandomGenerator.getDefault().nextDouble());
            }
		}
	}
	
	/**
	 * @see pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaWartosci.FunkcjaWartosci#wyczysc()
	 */
	public void wyczysc() {
		for (int i = 0; i < this.wartosci.length; i++) {
			for (int j = 0; j < this.akcji; j++) {
				wartosci[i][j] = 0;
            }
		}
	}

	
	/**
	 * Zwraca akcje dajaca minimalne wzocnienie dla danego stanu S
	 * @param stan Zadany stan S
	 * @return numer akcji
	 */
	public Akcja getMinAkcja(int stan, int iloscAkcji) {
		System.out.println("FunkcjaWartosciAkcji.getMinAkcja():" + iloscAkcji);
		Akcja result = new Akcja();
		
		float min = Float.MAX_VALUE;
		for (int i = 0; i < iloscAkcji; i++) {
			System.out.println("FunkcjaWartosciAkcji.getMinAkcja():" + i);
			if (wartosci[stan][i] < min) {
				result.numer = i;
				result.wartosc = wartosci[stan][i];
				min = wartosci[stan][i];
			}
		}
		return result; 
	}
	
	/**
	 * Zwraca akcje dajaca maksymalne wzocnienie dla danego stanu S
	 * @param stan Zadany stan S
	 * @return numer akcji
	 */
	public Akcja getMaxAkcja(int stan, int iloscAkcji) {
		Akcja result = new Akcja();
		
		float max = Float.MIN_VALUE;
		for (int i = 0; i < iloscAkcji; i++) {
			if (wartosci[stan][i] > max) {
				result.numer = i;
				result.wartosc = wartosci[stan][i];
				max = wartosci[stan][i];
			}
		}
		
//		System.out.println("MAX akcja:" + result.wartosc);
		return result; 
	}
	
	/**
	 * Poprawia wartosc Q stosujac zasade sredniej arytmetycznej z wczesniejszych wartosci.
	 * UWAGA: średnia arytmetyczna stosowana jest jako współczynnik proprawy aż do momentu w którym
	 * nie będzie mniejsza od wartosci ustawionej w MIN_POPRAWA
	 * Nowa wartosc jest zapisywana.
	 * 
	 * @param stan Stan dla ktorego dokonywana jest poprawka
	 * @param akcja Akcja dla ktorej dokonywana jest poprawka
	 * @param wartosc Wzmocnienie uzyskane w procesie symulacji
	 * @return Nowa wartosc Q(s,a) = suma(Q(s,a))/ilosc
	 */
	public float poprawWartosc(int stan, int akcja, float wartosc) {
		poprawki[stan][akcja]++;
		return poprawWartosc(stan, akcja, wartosc, Math.max(1/(float)(poprawki[stan][akcja]), min_poprawa));  
	}
	
	/**
	 * @return Zwraca ilość poprawek dla stanu i dla akcji
	 */
	public int getPoprawki(int stan, int akcja) {
		return poprawki[stan][akcja];
	}
	
	public int incPoprawki(int stan, int akcja) {
		return poprawki[stan][akcja]++;
	}
	/**
	 * @param stan stan w jakim był system
	 * @param akcja akcja jaka została wywołana
	 * @param wartosc Współczynnik wartosci akcji uwzględniający stan docelowy (uwzględniający Q funkcję stanu docelowego)
	 * @param obserwacja Wzmocnienie uzyskane w tym stanie
	 * @return 
	 */
	public float poprawObserwacje(int stan, int akcja, float obserwacja) {
		poprawkiObserwacji[stan][akcja]++;
		float wspZmiany = Math.max(1/(float)(poprawkiObserwacji[stan][akcja]), min_poprawa);
		return poprawSrednia(stan, akcja, obserwacja, wspZmiany);
	}
	
	/**
	 * Podaje średnią z obserwacji wzmocnień w danym stanie
	 */
	public float getSredniaObserwacji(int stan, int akcja) {
		return sredniaObserwacji[stan][akcja];
	}
	
	public float getOdchylenieStandardoweObserwacji(int stan, int akcja) {
		return (float) Math.sqrt(sredniaKwadratowObserwacji[stan][akcja]-Math.pow(sredniaObserwacji[stan][akcja], 2));
	}
	
	/**
	 * @return Zwraca liczbę obserwacji jakie zostały 
	 */
	public int getIloscObserwacji(int stan, int akcja) {
		return poprawkiObserwacji[stan][akcja];
	}
	
	/**
	 * @param stan
	 * @param akcja
	 * @return Zwraca liczbę poprawek (ilość liczb w szeregu)
	 */
	public int getIloscPoprawek(int stan, int akcja) {
		return poprawki[stan][akcja];
	}
	
	/**
	 * Poprawia wartosc Q(funkcji wartości akcji) stosujac przy tym wspolczynnik zmiany.
	 * Aby poprawić średnią wartość obserwacji w danym stanie należy wywołać metodę poprawSrednia
	 */
	public float poprawWartosc(int stan, int akcja, float wartosc, float wspolczynnikZmiany) {
		return (wartosci[stan][akcja] = poprawIteracyjnie(wartosci[stan][akcja], wartosc, wspolczynnikZmiany));
	}
	
	/**
	 * Poprawia średnią wartość dla obserwacji w danym stanie.
	 */
	public float poprawSrednia(int stan, int akcja, float wzmocnienie, float wspolczynnikZmiany ) {
		sredniaKwadratowObserwacji[stan][akcja] = poprawIteracyjnie(sredniaKwadratowObserwacji[stan][akcja], (float) Math.pow(wzmocnienie, 2), wspolczynnikZmiany);
		return (sredniaObserwacji[stan][akcja] = poprawIteracyjnie(sredniaObserwacji[stan][akcja], wzmocnienie, wspolczynnikZmiany));
	}
	
	/**
	 * Metoda dokonuje iteracyjnej poprawy wartości średniej na podstawie kolejno podawanych wartość
	 * jako parametr przyjmuje współczynnik poprawy - ktory dla zachowania charakteru zmiennej arytmetycznej
	 * powinien być zmniejszany wraz z każdą poprawą. 
	 * 
	 * @param staraSrednia - Poprzednia Wartość Średniej
	 * @param nowaWartosc - Kolejna wartość pomiaru jaki chcemy dodać do średniej
	 * @param wspolczynnikZmiany - Współczynnik poprawy dla kalkulacji wartości średniej 
	 * @return Nowa wartość średnia
	 */
	protected float poprawIteracyjnie(float staraSrednia, float nowaWartosc, float wspolczynnikZmiany) {
		return staraSrednia + wspolczynnikZmiany *(nowaWartosc - staraSrednia);
	}
	
	/**
	 * @param serwer - wyciagany z niego jest stan systemu - ilosc zgloszen w kolejkach
	 * @param akcja - numer kolejki do ktorej chcemy przelaczyc sterowanie 
	 * @return
	 */
	public float getWartosc(int stan, int akcja) {
		return wartosci[stan][akcja];
	}

	/**
	 * Ustawia konkretną wartość dla stanu i akcji ale jednocześnie ustawia licznik poprawek
	 * na 0. 
	 * @param stan
	 * @param akcja
	 * @param wartosc
	 */
	public void setWartosc(int stan, int akcja, float wartosc) {
		wartosci[stan][akcja] = wartosc;
		poprawki[stan][akcja] = 0;
	}

	public float addWartosc(int stan, int akcja, float wartosc) {
		wartosci[stan][akcja] += wartosc;
		return wartosci[stan][akcja];
	}
	
	public int getIloscAkcji() {
		return akcji;
	}
	
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		
		for (int i = 0; i < wartosci.length; i++) {
			result.append("# ");
			for (int j = 0; j < wartosci[i].length; j++) {
				result.append(j + " : " + wartosci[i][j]+" ; ");
			}
			result.append("\n");
		}
		
		return result.toString();
	}

	/**
	 * @return
	 */
	public float getMin_poprawa() {
		return min_poprawa;
	}

	/**	 
	 * Ustawienie współczynnika minimalnej poprawy. Jeżeli 1/licznik_ilosci popraw 
	 * będzie mniejszy od tej wartosci wartość tej zmiennej będzie używana wymiennie.
	 * @param min_poprawa
	 */
	public void setMin_poprawa(float min_poprawa) {
		this.min_poprawa = min_poprawa;
	}
	
	/**
	 * Ustawia wartosci Q-funkcji dla każdej pary (stan,akcja) w oparciu o przekazaną 
	 * bazową funkcję wartości
	 * 
	 * @param bazowa funkcja wartosci z ktorej mają być skopiowane wartosci (stan, akcja)
	 */
	public void ustaw(FunkcjaWartosciAkcji bazowa) {
		for (int i = 0; i < wartosci.length; i++) {
			for (int j = 0; j < wartosci[i].length; j++) {
				wartosci[i][j] = bazowa.wartosci[i][j];
				poprawki[i][j] = 0;
				sredniaObserwacji[i][j] = poprawkiObserwacji[i][j] = 0; 
			}
		}
	}
	
	/**
	 * Usuwa aktualne obserwacje uzywane do wyciągania informacji statystycznych
	 */
	public void wyczyscObserwacje() {
		for (int i = 0; i < sredniaObserwacji.length; i++) {
			for (int j = 0; j < sredniaObserwacji[i].length; j++) {
				sredniaObserwacji[i][j] = 0;
				sredniaKwadratowObserwacji[i][j] = 0;
			}
		}
	}
	
	/**
	 * Usuwa informacje o obserwacjach dla zadanego stanu i akcji
	 * @param stan
	 * @param akcja
	 */
	public void wyczyscObserwacje(int stan, int akcja) {
		sredniaObserwacji[stan][akcja] = 0;
		sredniaKwadratowObserwacji[stan][akcja] = 0;
		poprawkiObserwacji[stan][akcja] = 0;
	}

}

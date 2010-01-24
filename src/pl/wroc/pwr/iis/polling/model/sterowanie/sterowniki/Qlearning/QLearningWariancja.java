/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Qlearning;

import pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaWartosci.FunkcjaWartosciAkcji;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Sterownik;
import pl.wroc.pwr.iis.polling.model.sterowanie.strategie.Strategia_A;

/**
 * @author Michał Stanek <michal.stanek@pwr.wroc.pl>
 */
public class QLearningWariancja extends Sterownik {
	protected float temperatureShake = 0.5f;
	protected float dyskontShake = 0.2f;
	protected float alfaShake = 0.01f;
	protected int STATYSTYCZNY_PROG_ILOSCI_POMIAROW = 30;
	
	protected float granicaOdchylenia = 3.0f;
	private static final boolean ZAZNACZ_SHAKE = true;
	
    /**
     * Tablica zawierajaca wartosci Q(x,a)
     */
    protected final FunkcjaWartosciAkcji Q;
	protected float alfa; 
	protected float currentAlfa;
	protected float dyskont;
	protected float currentDyskont;

	protected boolean shake;
	protected boolean WLACZONA_ALFA = true;
	protected boolean WLACZONY_DYSKONT = true;
	protected boolean WLACZONY_SHAKE_STRATEGII = true;
	
	public QLearningWariancja(Strategia_A strategia, float alfa, float dyskont, int iloscAkcji) {
		super(strategia);
		this.alfa = alfa;
		this.currentAlfa = alfa;
		this.dyskont = dyskont;
		this.currentDyskont = dyskont;
		
		Q = strategia.getFunkcjaWartosciAkcji();
		Q.setMin_poprawa(alfa);
	}
	
	/**
	 * @return Zwraca prawdę jeżeli podczas pobrania ostatniej decyzji sterującej nastąpił
	 * Shake - czyli zmiana parametrów uczenia w systemie. 
//	 */
	public boolean bylShake() {
		return shake;
	}

	@Override
	public int getDecyzjaSterujaca(double ocenaStanu, int[] stan, int iloscAkcji) {
		final int aktualnyStan = strategia.getNumerStanu(stan);
		final int prevStan 	   = strategia.getOstatniStan();
		final int prevAkcja    = strategia.getOstatniaAkcja();
		final double r 		   = ocenaStanu;
		
		this.shake = false;
		/*
		 * Po przejsciu do kolejnego stanu nalezy poprawić informacje o stanie
		 * w którym byliśmy 
		 */ 
		 if (prevStan != Strategia_A.BRAK_USTAWIONEJ_WARTOSCI) { // poprawa następuje tylko wtedy kiedy był poprzedni stan 
			 // Liczone po otrzy
			 
			 if (czyZrobicShake(Q, prevStan, prevAkcja, r)) {
				zrobShake(alfaShake, dyskontShake, temperatureShake);
				Q.wyczyscObserwacje(prevStan, prevAkcja); //Czyści: Srednia obserwacji, Średnią kwadratów obserwacji, ilosc odwiedzin  
			 } else {
//				poprawParametry(Q); // Ustawia ALPHA + DYSKONT
			 }

			 Q.poprawObserwacje(prevStan,prevAkcja,r);
			 // Max wartosc Q w biezacym stanie
	
//			 float Q_s_a = Q.getWartosc(prevStan, prevAkcja);
			 double maxQ_sn_a = Q.getMaxAkcja(aktualnyStan, iloscAkcji).wartosc;
//			 float alfa = Math.max(1/(Q.incPoprawki(prevStan, prevAkcja)+1), this.alfa);
//			 float nowaWartoscQ = Q_s_a + alfa * (r + currentDyskont * maxQ_sn_a - Q_s_a);
			 double nowaWartoscQ = r + currentDyskont * maxQ_sn_a;	 
			 // Ustawienie nowej wartośći akcji
			 Q.poprawWartosc(prevStan, prevAkcja, nowaWartoscQ);
//			 Q.setWartosc(prevStan, prevAkcja, nowaWartoscQ);
		 } 
		return strategia.getAkcja(stan, iloscAkcji); // Od tego miejsca bedzie to juz aktualna akcja;
	}
	
	protected void zrobShake(float alfa, float dyskont, float temperature) {
		 if (WLACZONA_ALFA) { currentAlfa = alfa;}
		 if (WLACZONY_DYSKONT) { currentDyskont = dyskont;}
		 if (WLACZONY_SHAKE_STRATEGII) {strategia.setShake(temperature);}
		 if (ZAZNACZ_SHAKE)  {shake = true;}
	}

	/**
	 * Poprawia parametry nauki/pracy dla przekazanej Q-funkcji 
	 */
	protected void poprawParametry(FunkcjaWartosciAkcji Q) {
		if (currentAlfa > alfa) {
			currentAlfa = (float) (currentAlfa - 0.001);
			Q.setMin_poprawa(currentAlfa);
		} else if (currentAlfa < alfa) {
			currentAlfa = (float) (currentAlfa + 0.001);
			Q.setMin_poprawa(currentAlfa);
		}

		if (currentDyskont < dyskont) {
			currentDyskont = (float) (currentDyskont + 0.001);
		} else if (currentAlfa > dyskont) {
			currentDyskont = (float) (currentDyskont - 0.001);
		}
	}

	/**
	 * @return Sprawdza czy odleglosc aktualnej obserwacji od odchylenia standardowego jest odpowiednio mała. Dodatkowo 
	 * sprawdza rownież czy liczba obserwacji jest tez dostateczna aby zrobic takie porownanie
	 */
	protected boolean czyZrobicShake(FunkcjaWartosciAkcji Q, int stan, int akcja, double aktualnaObserwacja) {
		boolean ret = false;
		if (odlegloscObserwacjiOdGranicy(Q, stan, akcja, aktualnaObserwacja) > 0) {
			Q.wyczyscObserwacje(stan, akcja);
			ret = true;
		}
		return ret;
	}

	/**
	 * @return Odległość Obserwacji od progu wyznaczonego przez przemnozenie odchylenia standardowego przez czynnik (najczęściej > 3)
	 */
	protected double odlegloscObserwacjiOdGranicy(FunkcjaWartosciAkcji Q, int stan, int akcja, double aktualnaObserwacja) {
		double ret = 0;
		if (Q.getIloscObserwacji(stan, akcja) > STATYSTYCZNY_PROG_ILOSCI_POMIAROW && strategia.czyStabilna()) {
			ret = Math.max(0, odlegloscObserwacji(Q, stan, akcja, aktualnaObserwacja) - granicaOdchylenia * Q.getOdchylenieStandardoweObserwacji(stan, akcja));
		}
		return ret;
	}

	protected double odlegloscObserwacji(FunkcjaWartosciAkcji Q, int stan, int akcja, double aktualnaObserwacja) {
		return Math.abs(Q.getSredniaObserwacji(stan, akcja)-aktualnaObserwacja);
	}
	
	protected float granicaOdchylenieObserwacji(FunkcjaWartosciAkcji Q, int stan, int akcja) {
		return Q.getOdchylenieStandardoweObserwacji(stan, akcja) * granicaOdchylenia;
	}

	@Override
	public int startSterowania() {
		return 0;
	}

	public boolean isWLACZONA_ALFA() {
		return WLACZONA_ALFA;
	}

	public void setWLACZONA_ALFA(boolean wlaczona_alfa) {
		WLACZONA_ALFA = wlaczona_alfa;
	}

	public boolean isWLACZONY_DYSKONT() {
		return WLACZONY_DYSKONT;
	}

	public void setWLACZONY_DYSKONT(boolean wlaczony_dyskont) {
		WLACZONY_DYSKONT = wlaczony_dyskont;
	}

	public boolean isWLACZONY_SHAKE_STRATEGII() {
		return WLACZONY_SHAKE_STRATEGII;
	}

	public void setWLACZONY_SHAKE_STRATEGII(boolean wlaczony_shake_strategii) {
		WLACZONY_SHAKE_STRATEGII = wlaczony_shake_strategii;
	}
	
	public void setWYLACZ_WSZYSTKIE_WSPOMAGACZE(){
		setWLACZONA_ALFA(false);
		setWLACZONY_DYSKONT(false);
		setWLACZONY_SHAKE_STRATEGII(false);
	}

	public float getTemperatureShake() {
		return temperatureShake;
	}

	/**
	 * @param temperatureShake Ustawienie procentowego powrotu do stadium 
	 * 							ustalania parametrów dla metody SOFT_MAX
	 */
	public void setTemperatureShake(float temperatureShake) {
		this.temperatureShake = temperatureShake;
	}

	public float getDyskontShake() {
		return dyskontShake;
	}

	/**
	 * @param dyskontShake Ustawienie wartosci parametru Dyskontowania przy wstrząśnięciu
	 */
	public void setDyskontShake(float dyskontShake) {
		this.dyskontShake = dyskontShake;
	}

	public float getAlfaShake() {
		return alfaShake;
	}

	public void setAlfaShake(float alfaShake) {
		this.alfaShake = alfaShake;
	}
	
	
}

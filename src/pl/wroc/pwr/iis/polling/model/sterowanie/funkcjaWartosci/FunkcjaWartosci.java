package pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaWartosci;


public abstract class FunkcjaWartosci {


	public FunkcjaWartosci() {
		super();
	}

	/**
	 * Inicjuje tablice Q(s,a) lub V(s) losowymi wartosciami
	 */
	public abstract void zainicjujLosowo();
	
	/**
	 * Ustawia wartości tablicy na 0
	 */
	public abstract void wyczysc();
}
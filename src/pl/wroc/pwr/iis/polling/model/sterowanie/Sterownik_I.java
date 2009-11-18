package pl.wroc.pwr.iis.polling.model.sterowanie;


public interface Sterownik_I {
	int startSterowania();
	int koniecSterowania();
	
	int getDecyzjaSterujaca(float ocenaStanu, int[] stan, int iloscAkcji);
	
	
	/**
	 * @return Metoda ma za zadanie zwracać true jeżeli sterownik podjął decyzję o zmianie parametrów pracy.
	 * Parametry pracy mogą zostać zmieniane w wielu okolicznościach, najczęściej jednak zmiana nastepuje kiedy
	 * sterownik "stwierdzi", że warunki pracy zasadniczo odbiegają od normy
	 */
	public boolean bylShake();
}

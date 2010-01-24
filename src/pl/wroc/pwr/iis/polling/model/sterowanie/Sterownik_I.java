package pl.wroc.pwr.iis.polling.model.sterowanie;


public interface Sterownik_I {
	int startSterowania();
	int koniecSterowania();
	
	/**
	 * @return Zwraca w jakich punktach następuje decyzja sterująca 
	 * 			czy po obsłużeniu kolejki, czy po obsłużeniu zadania, czy
	 * 			po wykorzystaniu czasu przetwarzania 
	 */
	public ZdarzenieKolejki getDecyzjaNaZdarzenie();
	int getDecyzjaSterujaca(double ocenaStanu, int[] stan, int iloscAkcji);
	
	/**
	 * @return Metoda ma za zadanie zwracać true jeżeli sterownik podjął decyzję o zmianie parametrów pracy.
	 * Parametry pracy mogą zostać zmieniane w wielu okolicznościach, najczęściej jednak zmiana nastepuje kiedy
	 * sterownik "stwierdzi", że warunki pracy zasadniczo odbiegają od normy
	 */
	public boolean bylShake();
	
	String toStringHeader();
	String toString();
}

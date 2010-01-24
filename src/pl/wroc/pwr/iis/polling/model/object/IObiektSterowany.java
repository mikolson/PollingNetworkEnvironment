package pl.wroc.pwr.iis.polling.model.object;

public interface IObiektSterowany {

	/**
	 * @return Zwraca aktualny stan serwera - stany poszczegolnych kolejek
	 */
	public int[] getStan();
	
	/**
	 * @return Zwraca maxymalna liczbe stanow dla kazdej z kolejek
	 */
	public int[] getMaxStanow();
	
	/**
	 * @return Zwraca liczbę dozwolonych akcji dla obiektu w danym stanie
	 */
	public int getIloscAkcji();
}

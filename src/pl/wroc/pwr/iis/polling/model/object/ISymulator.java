package pl.wroc.pwr.iis.polling.model.object;

/**
 * @author Misiek
 */
public interface ISymulator {
	
	/**
	 * Służy do pobrania aktualnego stanu w jakim znajduje się obiekt sterowany.
	 * 
	 * @return Aktualny stan
	 */
	IStan getStan();
	
	/**
	 * Ustawia określony stan w symulowanym obiekcie
	 * 
	 * @param stan Żądany stan
	 */
	void setStan(IStan stan);
}

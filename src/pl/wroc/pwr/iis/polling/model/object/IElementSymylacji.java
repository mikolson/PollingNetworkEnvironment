/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.wroc.pwr.iis.polling.model.object;

/**
 * @author Michał Stanek
 */
public interface IElementSymylacji {
	// ***********************
	// Dotyczące eksperymentów
	// ***********************
	
	/** Wołane w momencie rozpoczęcia serii experymentów */
	void startEksperymentow();

	/** Wołane w momencie zakończenia serii experymentów */
	void koniecEksperymentow();
	
	
	// ***********************
	// Dotyczące pojedynczej symulacji
	// ***********************
	
	/** Wywołane w momencie startu pojedynczej symulacji*/
	void startSymulacji();

	/** Pojedynczy krok symulacji*/ 
	void wykonajCyklSymulacji(double interwalCzasu);

    /** Zakończenie symulacji */
    void koniecSymulacji();
    
}

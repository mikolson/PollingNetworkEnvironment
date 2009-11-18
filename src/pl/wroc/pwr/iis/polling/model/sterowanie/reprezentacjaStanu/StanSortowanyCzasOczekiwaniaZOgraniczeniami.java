package pl.wroc.pwr.iis.polling.model.sterowanie.reprezentacjaStanu;

import pl.wroc.pwr.iis.polling.model.object.polling.Serwer;


/**
 * Klasa działa analogicznie do Stan_R_Time_IloscZgloszen
 * tylko, że do sortowania uzywana jest nie liczba zgloszen
 * w kazdej z kolejek, a sredni czas oczekiwania w kazdej z nich. 
 * 
 * Zwraca stan bazujac na zasadzie:
 *   1. Kolejka 1 - 10.5 sek (średni czas oczekiwania)
 *   2. Kolejka 2 - 15.34 sek (średni czas oczekiwania)
 *   3. Kolejka 3 - 4 sek (średni czas oczekiwania)
 *   
 * Sredni czas oczekiwania w zadnej z kolejek nie przekroczyl ograniczenia:
 * 
 * 	Stan = [2,3,1]; // Sortowanie po kolejności czasów oczekiwania
 *
 * Jeżeli kolejka 1 przekroczyla ograniczenie czasowe to stan bedzie wygladal 
 * następująco:
 * 
 *  Stan = [0,2,1];
 *  
 * Jeśli natomiast kolejka 3 przekroczyla ograniczenie czasowe to:
 * 
 *  Stan = [0,1,0];
 *  
 *  
 * @author Misiek
 * 
 * @author Misiek
 */
public class StanSortowanyCzasOczekiwaniaZOgraniczeniami extends StanSortowanLiczbaZgloszenZOgraniczeniami {

	protected Float getMiara(Serwer serwer, int kolejka) {
		return (float)serwer.getKolejka(kolejka).getSredniCzasOczekiwania();
	}
}

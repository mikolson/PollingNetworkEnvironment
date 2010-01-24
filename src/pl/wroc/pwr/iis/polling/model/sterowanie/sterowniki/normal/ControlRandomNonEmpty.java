/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.normal;

import java.util.Random;

import pl.wroc.pwr.iis.polling.model.object.polling.Serwer;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Sterownik;

/**
 * Losowy wybór jednej z niepustych kolejek
 * @author Misiek
 */
public class ControlRandomNonEmpty extends Sterownik {
	private static final int BRAK_USTAWIONEJ_WARTOSCI = -1;
	Random generator = new Random();
	
	protected int 	poprzedniStan = BRAK_USTAWIONEJ_WARTOSCI;
	private final Serwer serwer;
	public ControlRandomNonEmpty(Serwer serwer) {
		super(null);
		this.serwer = serwer;
	}

	@Override
	public int getDecyzjaSterujaca(float ocenaStanu, int[] stan, int iloscAkcji) {
		int akcja = 0;
		
		System.out.println("Ilość akcji:" + iloscAkcji);
		
		// Sprawdzenie czy istnieje choć jedna niepusta kolejka
		boolean niepuste = false;
		for (int i = 0; i < serwer.getIloscKolejek(); i++) {
			if (serwer.getKolejka(i).getIloscZgloszen() > 0) {
				niepuste = true;
//				break;
				
			}
			System.out.print(serwer.getKolejka(i).getIloscZgloszen() + " : ");
		}
		System.out.println();
		
		
		// Wylosowanie kolejki, jeżeli choć jedna jest niepusta
		if (niepuste) {
			do {
				akcja = generator.nextInt(iloscAkcji);
				System.out.println("Decyzja sterująca: "+ akcja);
			} while (serwer.getKolejka(akcja).getIloscZgloszen() == 0 );
		}

		return akcja; 
	}
	
}

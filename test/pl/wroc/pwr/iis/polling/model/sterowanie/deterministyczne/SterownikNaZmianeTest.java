package pl.wroc.pwr.iis.polling.model.sterowanie.deterministyczne;

import pl.wroc.pwr.iis.polling.model.object.polling.Serwer;
import pl.wroc.pwr.iis.rozklady.RozkladJednostajny;
import junit.framework.TestCase;

public class SterownikNaZmianeTest extends TestCase {
	Serwer serwer;
	
	protected void setUp() throws Exception {
		super.setUp();
		serwer = new Serwer("test", 2);
		
		serwer.setMaxZgloszen(20);
        serwer.setMaxCzasOczekiwania(10);
        serwer.setWaga(1);
        
        serwer.setRozkladCzasuNastawy(Serwer.BRAK_ROZKLADU);
        serwer.setRozkladIlosciObslug(new RozkladJednostajny(1));
        serwer.setRozkladIlosciPrzybyc(new RozkladJednostajny(1));
        
        serwer.setSterownik(new SterownikNaZmiane());
	}

	public void testOslug() throws Exception {
		
		int iteracji = 20;
		for (int i = 0; i < iteracji; i++) {
			serwer.wykonajCyklSymulacji();
		}
		
		assertEquals(iteracji/serwer.getIloscKolejek(), serwer.getKolejka(0).getIloscZgloszen());
		assertEquals(iteracji/serwer.getIloscKolejek(), serwer.getKolejka(1).getIloscZgloszen());
	}
	

	public void testOslug2() throws Exception {
		serwer.setRozkladIlosciObslug(new RozkladJednostajny(serwer.getIloscKolejek()));
		
		int iteracji = 20;
		for (int i = 0; i < iteracji; i++) {
			serwer.wykonajCyklSymulacji();
		}
		
		// Jeden dlatego ze nie obsluzono ponownie pierwszej kolejki
		assertEquals(1, serwer.getKolejka(0).getIloscZgloszen());
		
		assertEquals(0, serwer.getKolejka(1).getIloscZgloszen());
	}

}

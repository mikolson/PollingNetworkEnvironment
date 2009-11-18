package pl.wroc.pwr.iis.polling.model;

import pl.wroc.pwr.iis.polling.model.object.polling.Serwer;
import pl.wroc.pwr.iis.rozklady.RozkladJednostajny;
import junit.framework.TestCase;


public class SerwerTest extends TestCase {
	Serwer serwer;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		serwer = new Serwer("test", 2);
		
		serwer.setMaxZgloszen(20);
        serwer.setMaxCzasOczekiwania(10);
        serwer.setWaga(1);
        
        serwer.setRozkladCzasuNastawy(Serwer.BRAK_ROZKLADU);
        serwer.setRozkladIlosciObslug(new RozkladJednostajny(1));
        serwer.setRozkladIlosciPrzybyc(new RozkladJednostajny(1));
        serwer.setObslugiwanaKolejka(0);
	}
	
	public void testOslug() throws Exception {
		
		int iteracji = 300;
		for (int i = 0; i < iteracji; i++) {
			serwer.wykonajCyklSymulacji();
		}
		
		assertEquals(0, serwer.getKolejka(0).getIloscZgloszen());
		assertEquals(Math.min(serwer.getMaxZgloszen(), iteracji), serwer.getKolejka(1).getIloscZgloszen());
	}
	
	
	public void testOslug2() throws Exception {
		serwer.setRozkladIlosciPrzybyc(new RozkladJednostajny(2));
		
		int iteracji = 2;
		for (int i = 0; i < iteracji; i++) {
			serwer.wykonajCyklSymulacji();
		}
		
		assertEquals(iteracji, serwer.getKolejka(0).getIloscZgloszen());
	}
	
	public void testCzasNastawy() throws Exception {
		serwer.setRozkladCzasuNastawy(new RozkladJednostajny(1));
		
		int kolejek = 2;
		int iteracji = 10;
		for (int i = 0; i < kolejek ; i++) {
			serwer.setObslugiwanaKolejka(i);
			for (int j = 0; j < iteracji; j++) {
				serwer.wykonajCyklSymulacji();
			}
		}
		
		for (int i = 0; i < kolejek; i++) {
			assertEquals(iteracji+1, serwer.getKolejka(i).getIloscZgloszen());
		}
	}
	
	public void testNaplywuDoKolejek() throws Exception {
		serwer = new Serwer("test", 3);
		
		serwer.setMaxZgloszen(20);
        serwer.setMaxCzasOczekiwania(10);
        serwer.setWaga(1);
        
        serwer.setRozkladCzasuNastawy(Serwer.BRAK_ROZKLADU);
        serwer.setRozkladIlosciObslug(new RozkladJednostajny(1));
        serwer.setRozkladIlosciPrzybyc(new RozkladJednostajny(1));
        serwer.setObslugiwanaKolejka(0);
	}

}

package pl.wroc.pwr.iis.polling.model;

import junit.framework.TestCase;

import org.junit.Test;

import pl.wroc.pwr.iis.polling.model.object.polling.Kolejka;
import pl.wroc.pwr.iis.polling.model.object.polling.Serwer;
import pl.wroc.pwr.iis.polling.model.object.polling.Wlasciwosci;
import pl.wroc.pwr.iis.rozklady.IRozkladPrawdopodobienstwa;
import pl.wroc.pwr.iis.rozklady.RozkladJednostajny;

public class KolejkaTest extends TestCase {

	private static final int MAX_CZAS_OCZEKIWANIA = 10;
	private static final int ILOSC_KOLEJEK = 2;

	Serwer serwer;
	int maxZgloszen = 20;
	float waga = 1f;
	
	@Override
	protected void setUp() throws Exception {
		serwer = new Serwer("Serwer 1", ILOSC_KOLEJEK);
        
		serwer.setMaxZgloszen(maxZgloszen);
        serwer.setMaxCzasOczekiwania(MAX_CZAS_OCZEKIWANIA);
		serwer.setWaga(waga);
        
        serwer.setRozkladCzasuNastawy(new RozkladJednostajny(1));
        serwer.setRozkladIlosciObslug(new RozkladJednostajny(1));
        serwer.setRozkladIlosciPrzybyc(new RozkladJednostajny(1));
        
        serwer.setObslugiwanaKolejka(serwer.BRAK_OBSLUGI); 	
	}
	
	/**
	 * Sprawdza czy poprawnie zwiekszaja sie zgloszenia w kolejkach przy ograniczeniu rozmiaru bufora
	 */
	@Test 
	public void testWykonajCyklSymulacji() {
        // Konfiguracja połączeń wychodzących
          // serwer.setPolaczeniaWychodzace(polaczeniaWychodzace);
        assertTrue(serwer.getIloscKolejek() == ILOSC_KOLEJEK);
        
        int ilosc_iteracji = 200;
		for (int i = 0; i < ilosc_iteracji ; i++) {
			serwer.wykonajCyklSymulacji();
		}
		System.out.println(serwer);
		
		for (int i = 0; i < ILOSC_KOLEJEK ; i++) {
			assertEquals(Math.min(ilosc_iteracji, serwer.getMaxZgloszen()), serwer.getKolejka(i).getIloscZgloszen());
		}
		
	}
	
	/**
	 * Sprawdza czy poprawnie zwiekszaja sie zgloszenia w kolejkach 
	 */
	public void testWykonajCyklSymulacji01() {
		serwer.setMaxZgloszen(100000);
        
        // Konfiguracja połączeń wychodzących
          // serwer.setPolaczeniaWychodzace(polaczeniaWychodzace);
        assertTrue(serwer.getIloscKolejek() == ILOSC_KOLEJEK);
        
        int ilosc_iteracji = 200;
		for (int i = 0; i < ilosc_iteracji ; i++) {
			serwer.wykonajCyklSymulacji();
		}
		System.out.println(serwer);
		
		for (int i = 0; i < ILOSC_KOLEJEK ; i++) {
			assertEquals(Math.min(ilosc_iteracji, serwer.getMaxZgloszen()), serwer.getKolejka(i).getIloscZgloszen());
		}
	}

	
	
	public void testWykonajCyklSymulacji2() {
		serwer.setMaxZgloszen(400);
        serwer.setMaxCzasOczekiwania(10);
        serwer.setWaga(1);
        
        serwer.setRozkladCzasuNastawy(new RozkladJednostajny(2));
        serwer.setRozkladIlosciObslug(new RozkladJednostajny(2));
        serwer.setRozkladIlosciPrzybyc(new RozkladJednostajny(2));
        
        
        IRozkladPrawdopodobienstwa[] rozklady = new IRozkladPrawdopodobienstwa[]{
        	new RozkladJednostajny(0), 
        	new RozkladJednostajny(1),
        };
        
        serwer.setRozkladCzasuPrzybyc(rozklady);
        serwer.setObslugiwanaKolejka(serwer.BRAK_OBSLUGI);
        
        // Konfiguracja połączeń wychodzących
          // serwer.setPolaczeniaWychodzace(polaczeniaWychodzace);
        
        int ilosc_iteracji = 3;
		for (int i = 0; i < ilosc_iteracji ; i++) {
			serwer.wykonajCyklSymulacji();
		}
		System.out.println(serwer);
		
		assertEquals(Math.min(0, serwer.getMaxZgloszen()), serwer.getKolejka(0).getIloscZgloszen());
		assertEquals(Math.min(ilosc_iteracji, serwer.getMaxZgloszen()), serwer.getKolejka(1).getIloscZgloszen());
	}
	
	public void testGetIloscZgloszen() throws Exception {
		for (int i = 0; i < ILOSC_KOLEJEK; i++) {
			assertEquals(maxZgloszen, serwer.getKolejka(i).getMaxZgloszen());
		}
		
		int[] maxZgl = new int[] {4,5};
		
		serwer.setMaxZgloszen(maxZgl);
		for (int i = 0; i < ILOSC_KOLEJEK; i++) {
			assertEquals(maxZgl[i], serwer.getKolejka(i).getMaxZgloszen());
		}
	}
	
	public void testGetWaga() throws Exception {
		for (int i = 0; i < ILOSC_KOLEJEK; i++) {
			assertEquals(waga, serwer.getKolejka(i).getWaga());
		}
		
		float[] wagi = new float[] {4,5};
		
		serwer.setWagi(wagi);
		for (int i = 0; i < ILOSC_KOLEJEK; i++) {
			assertEquals(wagi[i], serwer.getKolejka(i).getWaga());
		}
	}
	
	/**
	 * Test czy dobrze liczony jest średni czas oczekiwania
	 * @throws Exception
	 */
	public void testSredniCzasOczekiwania() throws Exception {
		Kolejka k = new Kolejka(serwer);
		k.setRozkladIlosciPrzybyc(new RozkladJednostajny(1));
		k.setMaxZgloszen(Integer.MAX_VALUE);

		
		int iteracji = 100;
		int suma = 0;
		for (int i = 0; i < iteracji ; i++) {
			suma += i;
			k.wykonajCyklSymulacji();
		}
		
		assertEquals(suma/(float)iteracji, k.getSredniCzasOczekiwania());
	}
	
	public void testMaxCzasOczekiwaniaZserwera() throws Exception {
		assertEquals(MAX_CZAS_OCZEKIWANIA, serwer.getKolejka(0).getMaxCzasOczekiwania());
	}
	
	public void testMaxCzasOczekiwaniaZkolejki() throws Exception {
		int czasOczekiwania = 100;
		serwer.getKolejka(0).setMaxCzasOczekiwania(czasOczekiwania);
		assertEquals(czasOczekiwania, serwer.getKolejka(0).getMaxCzasOczekiwania());
	}
	
	public void testBrakOgraniczeniaCzasowego() throws Exception {
		serwer.setMaxCzasOczekiwania(Wlasciwosci.BRAK_OGRANICZENIA_CZASOWEGO);
		assertTrue(serwer.getKolejka(0).getMaxCzasOczekiwania() >= Integer.MAX_VALUE);
	}
	
	public void testWagi() throws Exception {
		assertEquals(waga, serwer.getKolejka(0).getWaga());
		
		float w = 444;
		serwer.getKolejka(0).setWaga(w);
		
		assertEquals(w, serwer.getKolejka(0).getWaga());
	}
}

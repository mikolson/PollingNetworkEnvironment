package pl.wroc.pwr.iis.polling.model.sterowanie.reprezentacjaStanu;

import java.util.Arrays;

import pl.wroc.pwr.iis.polling.model.object.IStan.Porownanie;
import pl.wroc.pwr.iis.polling.model.object.polling.Serwer;
import pl.wroc.pwr.iis.rozklady.RozkladJednostajny;
import junit.framework.TestCase;

public class StanLiczbaSpelnionychOgraniczenTest extends TestCase {
	
	private static final int STAN_PRZEKROCZENIE = StanLiczbaSpelnionychOgraniczenCzasSredni.STAN_PRZEKROCZENIE;
	private static final int STAN_OK = StanLiczbaSpelnionychOgraniczenCzasSredni.STAN_OK;
	private static final int MAX_CZAS_OCZEKIWANIA = 10;
	private static final int ILOSC_KOLEJEK = 2;

	StanLiczbaSpelnionychOgraniczenCzasSredni stanLiczbaOgranicznen = new StanLiczbaSpelnionychOgraniczenCzasSredni();
	Serwer serwer;
	int maxZgloszen = 20;
	float waga = 1f;

	protected void setUp() throws Exception {
		super.setUp();
		serwer = new Serwer("Serwer 1", ILOSC_KOLEJEK);
        
		serwer.setMaxZgloszen(maxZgloszen);
        serwer.setMaxCzasOczekiwania(MAX_CZAS_OCZEKIWANIA);
		serwer.setWaga(waga);
        
        serwer.setRozkladCzasuNastawy(new RozkladJednostajny(0));
        serwer.setRozkladIlosciObslug(new RozkladJednostajny(0));
        serwer.setRozkladIlosciPrzybyc(new RozkladJednostajny(0));
        
        serwer.setObslugiwanaKolejka(Serwer.BRAK_OBSLUGI); 	
        serwer.setReprezentacjaStanu(stanLiczbaOgranicznen);
        
        for (int i = 0; i < serwer.getIloscKolejek(); i++) {
			serwer.getKolejka(i).dodajDoKolejki(1);
		}
	}

	public void testGetStan() {
		int[] porownanie = new int[serwer.getIloscKolejek()];
		for (int i = 0; i < porownanie.length; i++) {
			porownanie[i] = 1;
		}
		
		for (int i = 0; i < MAX_CZAS_OCZEKIWANIA; i++) {
			serwer.wykonajCyklSymulacji();
			assertTrue(Arrays.equals(serwer.getStan(), porownanie));
		}
		
		// Sprawdzenie czy po przekroczeniu ograniczenia prawidlowo zapisywany jest stan
		serwer.wykonajCyklSymulacji();
		
		
		porownanie = new int[serwer.getIloscKolejek()];
			
		assertTrue(Arrays.equals(serwer.getStan(), porownanie));
		
	}

	public void testCompare() {
		int[] porownanie = new int[]{STAN_OK,STAN_OK};
		int[] porownanie1 = new int[]{STAN_PRZEKROCZENIE,STAN_OK};
		int[] porownanie2 = new int[]{STAN_PRZEKROCZENIE,STAN_PRZEKROCZENIE};
		
		assertEquals(Porownanie.Rowny, stanLiczbaOgranicznen.compare(serwer, porownanie));
		assertEquals(Porownanie.Lepszy, stanLiczbaOgranicznen.compare(serwer, porownanie1));
		
		for (int i = 0; i < MAX_CZAS_OCZEKIWANIA; i++) {
			serwer.wykonajCyklSymulacji();
		}
		
		assertEquals(Porownanie.Rowny, stanLiczbaOgranicznen.compare(serwer, porownanie));
		assertEquals(Porownanie.Lepszy, stanLiczbaOgranicznen.compare(serwer, porownanie1));

		serwer.wykonajCyklSymulacji();
		
		assertEquals(Porownanie.Gorszy, stanLiczbaOgranicznen.compare(serwer, porownanie));
		assertEquals(Porownanie.Gorszy, stanLiczbaOgranicznen.compare(serwer, porownanie1));
		assertEquals(Porownanie.Rowny, stanLiczbaOgranicznen.compare(serwer, porownanie2));
	}

	public void testCompareWspolczynnik() {
		int[] porownanie = new int[]{STAN_OK,STAN_OK};
		int[] porownanie1 = new int[]{STAN_PRZEKROCZENIE,STAN_OK};
		int[] porownanie2 = new int[]{STAN_PRZEKROCZENIE,STAN_PRZEKROCZENIE};
		
		
		//assertEquals(Porownanie.Rowny, stanLiczbaOgranicznen.compare(serwer, porownanie));
		assertEquals(0f, stanLiczbaOgranicznen.compareWspolczynnik(serwer, porownanie));
		// Lepszy
		assertEquals(1f, stanLiczbaOgranicznen.compareWspolczynnik(serwer, porownanie1));
		assertEquals(2f, stanLiczbaOgranicznen.compareWspolczynnik(serwer, porownanie2));
		
		for (int i = 0; i < MAX_CZAS_OCZEKIWANIA+1; i++) {
			serwer.wykonajCyklSymulacji();
		}
		
//		assertEquals(Porownanie.Gorszy, stanLiczbaOgranicznen.compare(serwer, porownanie));
		assertEquals(-2f, stanLiczbaOgranicznen.compareWspolczynnik(serwer, porownanie));
		assertEquals(-1f, stanLiczbaOgranicznen.compareWspolczynnik(serwer, porownanie1));
		
//		assertEquals(Porownanie.Rowny, stanLiczbaOgranicznen.compare(serwer, porownanie2));		
		assertEquals(0f, stanLiczbaOgranicznen.compareWspolczynnik(serwer, porownanie2));		
	}

}

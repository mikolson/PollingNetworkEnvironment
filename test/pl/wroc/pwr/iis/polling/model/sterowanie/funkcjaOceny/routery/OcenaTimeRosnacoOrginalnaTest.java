package pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaOceny.routery;

import pl.wroc.pwr.iis.polling.model.object.polling.Serwer;
import pl.wroc.pwr.iis.polling.model.sterowanie.reprezentacjaStanu.StanLiczbaZgloszen;
import pl.wroc.pwr.iis.rozklady.RozkladJednostajny;
import junit.framework.TestCase;

public class OcenaTimeRosnacoOrginalnaTest extends TestCase {

	private static final int MAX_CZAS_OCZEKIWANIA = 10;
	private static final float C1 = 20;
	private static final float C2 = 30;
	private static final float C3 = 40;
	private Serwer serwer;
	private Serwer serwer3;
	
	private OcenaTimeRosnacoOrginalna funkcjaOceny;
	private StanLiczbaZgloszen reprezentacjaStanu;

	protected void setUp() throws Exception {
		super.setUp();
		
		super.setUp();
		serwer = new Serwer("test", 2);
		
		serwer.setMaxZgloszen(20);
        serwer.setWaga(1);
        
        serwer.setMaxCzasOczekiwania(MAX_CZAS_OCZEKIWANIA);
        
        serwer.setRozkladCzasuNastawy(Serwer.BRAK_ROZKLADU);
        serwer.setObslugiwanaKolejka(Serwer.BRAK_OBSLUGI);
        serwer.setRozkladIlosciObslug(new RozkladJednostajny(0));
        serwer.setRozkladIlosciPrzybyc(new RozkladJednostajny(0));
        
        funkcjaOceny = new OcenaTimeRosnacoOrginalna(C1,C2,C3);
        serwer.setFunkcjaOceny(funkcjaOceny);
        
        serwer3 = new Serwer("test", 3);
		serwer3.setMaxZgloszen(20);
        serwer3.setWaga(1);
        
        serwer3.setMaxCzasOczekiwania(MAX_CZAS_OCZEKIWANIA);
        serwer3.setObslugiwanaKolejka(Serwer.BRAK_OBSLUGI);
        serwer3.setRozkladCzasuNastawy(Serwer.BRAK_ROZKLADU);
        serwer3.setRozkladIlosciObslug(new RozkladJednostajny(0));
        serwer3.setRozkladIlosciPrzybyc(new RozkladJednostajny(0));
        
        funkcjaOceny = new OcenaTimeRosnacoOrginalna(C1,C2,C3);
        serwer3.setFunkcjaOceny(funkcjaOceny);
        
	}

	public void testNormalneWartosciPracyBrakPorownaniaStanu() throws Exception {
        reprezentacjaStanu = new StanLiczbaZgloszen();
        serwer.setReprezentacjaStanu(reprezentacjaStanu);
        serwer3.setReprezentacjaStanu(reprezentacjaStanu);

		serwer.getKolejka(0).dodajDoKolejki(1);
		assertEquals(0f, serwer.getOcenaSytuacji());
		for (int i = 0; i < MAX_CZAS_OCZEKIWANIA; i++) {
			serwer.wykonajCyklSymulacji();
		}
		
		assertEquals(C1, serwer.getOcenaSytuacji());
		
		
		// Test przy 3 kolejkach
		serwer3.getKolejka(0).dodajDoKolejki(1);
		serwer3.getKolejka(1).dodajDoKolejki(1);
		assertEquals(0f, serwer3.getOcenaSytuacji());
		for (int i = 0; i < MAX_CZAS_OCZEKIWANIA; i++) {
			serwer3.wykonajCyklSymulacji();
		}
		
		assertEquals(C1*(serwer3.getIloscKolejek()-1), serwer3.getOcenaSytuacji());
	}
	
	public void testPrzekroczenieZakresuBrakPorownaniaStanu() {
        reprezentacjaStanu = new StanLiczbaZgloszen();
        serwer.setReprezentacjaStanu(reprezentacjaStanu);

		serwer.getKolejka(0).dodajDoKolejki(1);
		for (int i = 0; i < MAX_CZAS_OCZEKIWANIA+1; i++) {
			serwer.wykonajCyklSymulacji();
			System.out.println(serwer.getOcenaSytuacji());
		}
		
		assertEquals(-C2, serwer.getOcenaSytuacji());
		
        serwer3.setReprezentacjaStanu(reprezentacjaStanu);
		serwer3.getKolejka(0).dodajDoKolejki(1);
		serwer3.getKolejka(1).dodajDoKolejki(1);
		
		for (int i = 0; i < MAX_CZAS_OCZEKIWANIA+1; i++) {
			serwer3.wykonajCyklSymulacji();
		}
		
		assertEquals(-C2*2, serwer3.getOcenaSytuacji());
	}
}

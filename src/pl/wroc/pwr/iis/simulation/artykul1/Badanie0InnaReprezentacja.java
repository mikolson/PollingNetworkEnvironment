package pl.wroc.pwr.iis.simulation.artykul1;

import pl.wroc.pwr.iis.polling.model.object.IStan;
import pl.wroc.pwr.iis.polling.model.object.polling.Serwer;
import pl.wroc.pwr.iis.polling.model.sterowanie.FunkcjaOceny_I;
import pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaOceny.routery.OcenaTimeRosnacoOrginalna;
import pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaOceny.routery.OcenaTimeRosnacoOrginalna2;
import pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaOceny.routery.OcenaTimeRosnacoTuning_WLASNA;
import pl.wroc.pwr.iis.polling.model.sterowanie.reprezentacjaStanu.StanBrakReprezentacji;
import pl.wroc.pwr.iis.polling.model.sterowanie.reprezentacjaStanu.StanLiczbaSpelnionychOgraniczenCzasSredni;
import pl.wroc.pwr.iis.polling.model.sterowanie.reprezentacjaStanu.StanSortowanLiczbaZgloszenZOgraniczeniami;
import pl.wroc.pwr.iis.polling.model.sterowanie.reprezentacjaStanu.StanSortowanyCzasOczekiwaniaZOgraniczeniami;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Sterownik;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Qlearning.QLearning;
import pl.wroc.pwr.iis.polling.model.sterowanie.strategie.StrategiaEZachlanna;
import pl.wroc.pwr.iis.polling.model.sterowanie.strategie.StrategiaEZachlannaDynamiczna;
import pl.wroc.pwr.iis.polling.model.sterowanie.strategie.StrategiaSoftMax;
import pl.wroc.pwr.iis.polling.model.sterowanie.strategie.Strategia_A;
import pl.wroc.pwr.iis.rozklady.RozkladBernouliego;
import pl.wroc.pwr.iis.rozklady.RozkladJednostajny;
import pl.wroc.pwr.iis.rozklady.random.RandomGenerator;
import pl.wroc.pwr.iis.simulation.Badanie2Metod;

public class Badanie0InnaReprezentacja extends Badanie2Metod {
   	
	private static final int 	E_ITERACJI = 5000;
	private static final float	E_KONIEC = 0.01f;
	private static final float	E_START = 0.05f;
	
	private static final int T_ITERACJI = 5000;
	private static final float T_KONIEC = 3f;
	private static final float T_START = 200f;
	float c1 = 50;
	float c2 = 100;
	float c3 = 50;
	float c4 = 20;
	float epsilonZachlannosci = 0.05f;
	float alfa = 0.05f; // poprawa Q
	float gamma = 0.5f; // 
	
	int[] intentywnosciNaplywu = new int[] {15,25,25};
	
	public Badanie0InnaReprezentacja() {
		serwerMetoda1 = new Serwer("Serwer 1",3);
		serwerMetoda2 = new Serwer("Serwer 2",3);
//		setParametryEksperymentu(300000, 30, 3000);
//		setParametryEksperymentu(50000, 3, 2000);
//		setParametryEksperymentu(1000000, 3, 2000, 0000,100000);
		setParametryEksperymentu(300000, 3, 2500);
	}
	
	@Override
	protected void konfiguracjaGeneratoraLiczb() {
		RandomGenerator.setDefaultSeed(1021);
	}	
	
	private void ustawienieSerwera(Serwer serwerBadania) {
		serwerBadania.setMaxZgloszen(1000);
//    	serwer1.setMaxCzasOczekiwania(200); 
    	serwerBadania.getKolejka(0).setMaxCzasOczekiwania(100);
    	serwerBadania.getKolejka(1).setMaxCzasOczekiwania(40);
    	serwerBadania.getKolejka(2).setMaxCzasOczekiwania(Integer.MAX_VALUE);
    	
    	// Serwer za kazdym razem obsluguje tylko jedno zgloszenie
    	for (int i = 0; i < serwerBadania.getIloscKolejek(); i++) {
    		serwerBadania.getKolejka(i).setRozkladIlosciObslug(new RozkladJednostajny(1));
		}
//    	serwer1.setRozkladIlosciPrzybyc(new RozkladRownomierny(0,3));
    	for (int i = 0; i < serwerBadania.getIloscKolejek(); i++) {
    		serwerBadania.getKolejka(i).setRozkladIlosciPrzybyc(new RozkladBernouliego(intentywnosciNaplywu[i]));
		}
    	
    	serwerBadania.setRozkladCzasuNastawy(new RozkladJednostajny(0));
    	serwerBadania.setWaga(1);
	}
	
	@Override
	protected void konfiguracjaMetoda1(Serwer serwerBadania) {
		ustawienieSerwera(serwerBadania);
    	
		IStan reprezentacjaStanu;
		reprezentacjaStanu = new StanLiczbaSpelnionychOgraniczenCzasSredni();
//		reprezentacjaStanu = new StanSortowanLiczbaZgloszenZOgraniczeniami();
//		reprezentacjaStanu = new StanSortowanyCzasOczekiwaniaZOgraniczeniami();
		serwerBadania.setReprezentacjaStanu(reprezentacjaStanu);
		
//		FunkcjaOceny_I fOceny = new OcenaTimeRosnacoOrginalna(c1,c2,c3);
		FunkcjaOceny_I fOceny = new OcenaTimeRosnacoOrginalna2(c1,c2,c3,c4);
		serwerBadania.setFunkcjaOceny(fOceny);
//
//		Strategia_A strategiaEzachlanna = new StrategiaEZachlanna(epsilonZachlannosci, serwerBadania.getMaxStanow(), serwerBadania.getIloscAkcji());
//		Strategia_A strategiaEzachlanna = new StrategiaEZachlannaDynamiczna(E_START, E_KONIEC, E_ITERACJI , serwerBadania.getMaxStanow(), serwerBadania.getIloscAkcji());
		Strategia_A strategiaEzachlanna = new StrategiaSoftMax(T_START, T_KONIEC, T_ITERACJI , serwerBadania.getMaxStanow(), serwerBadania.getIloscAkcji());
		
		Sterownik sterownik;
		sterownik = new QLearning(strategiaEzachlanna, alfa, gamma);
//		sterownik = new SarsaOnPolicy(strategiaEzachlanna, alfa, gamma);
//		sterownik = new SterownikEveryVisitMC(strategiaEzachlanna);
//		sterownik = new SterownikNaZmiane();
//		sterownik = new SterownikLosowy();
		serwerBadania.setSterownik(sterownik);
	}

	@Override
	protected void konfiguracjaMetoda2(Serwer serwerBadania) {
		ustawienieSerwera(serwerBadania);
		
		IStan reprezentacjaStanu;
//		reprezentacjaStanu = new StanLiczbaSpelnionychOgraniczen();
//		reprezentacjaStanu = new StanBrakReprezentacji();
//		reprezentacjaStanu = new StanSortowanLiczbaZgloszenZOgraniczeniami();
		reprezentacjaStanu = new StanSortowanyCzasOczekiwaniaZOgraniczeniami();
		serwerBadania.setReprezentacjaStanu(reprezentacjaStanu);
		
//		FunkcjaOceny_I fOceny = new OcenaTimeRosnacoOrginalna(c1,c2,c3);
		FunkcjaOceny_I fOceny = new OcenaTimeRosnacoOrginalna2(c1,c2,c3,c4);
//		FunkcjaOceny_I fOceny = new OcenaTimeRosnacoTuning_WLASNA(c1,c2,c3);
		serwerBadania.setFunkcjaOceny(fOceny);
//
//		Strategia_A strategiaEzachlanna = new StrategiaEZachlanna(epsilonZachlannosci, serwerBadania.getMaxStanow(), serwerBadania.getIloscAkcji());
//		Strategia_A strategiaEzachlanna = new StrategiaEZachlannaDynamiczna(E_START, E_KONIEC, E_ITERACJI , serwerBadania.getMaxStanow(), serwerBadania.getIloscAkcji());
		Strategia_A strategiaEzachlanna = new StrategiaSoftMax(T_START, T_KONIEC, T_ITERACJI , serwerBadania.getMaxStanow(), serwerBadania.getIloscAkcji());
		
		Sterownik sterownik;
		sterownik = new QLearning(strategiaEzachlanna, alfa, gamma);
		
	
//		sterownik = new SarsaOnPolicy(strategiaEzachlanna, alfa, gamma);
//		sterownik = new SterownikEveryVisitMC(strategiaEzachlanna);
//		sterownik = new SterownikNaZmiane();
//		sterownik = new SterownikLosowy();
		serwerBadania.setSterownik(sterownik);
	}
	
	
	public static void main(String[] args) {
		Badanie0InnaReprezentacja badanie = new Badanie0InnaReprezentacja();
		badanie.wykonajBadanie();
	}

}

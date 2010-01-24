package pl.wroc.pwr.iis.simulation.artykul1.wyniki.reprezentacja;

import pl.wroc.pwr.iis.polling.model.object.IStan;
import pl.wroc.pwr.iis.polling.model.object.polling.Serwer;
import pl.wroc.pwr.iis.polling.model.sterowanie.FunkcjaOceny_I;
import pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaOceny.routery.OcenaTimeRosnacoOrginalna3;
import pl.wroc.pwr.iis.polling.model.sterowanie.reprezentacjaStanu.StanLiczbaSpelnionychOgraniczenCzasSredni;
import pl.wroc.pwr.iis.polling.model.sterowanie.reprezentacjaStanu.StanSortowanLiczbaZgloszenZOgraniczeniami;
import pl.wroc.pwr.iis.polling.model.sterowanie.reprezentacjaStanu.StanSortowanyCzasOczekiwaniaZOgraniczeniami;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Sterownik;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Qlearning.QLearning;
import pl.wroc.pwr.iis.polling.model.sterowanie.strategie.StrategiaSoftMax;
import pl.wroc.pwr.iis.polling.model.sterowanie.strategie.Strategia_A;
import pl.wroc.pwr.iis.rozklady.RozkladBernouliego;
import pl.wroc.pwr.iis.rozklady.dyskretne.RozkladJednostajnyDyskretny;
import pl.wroc.pwr.iis.rozklady.random.RandomGenerator;
import pl.wroc.pwr.iis.simulation.Badanie2Metod;

public class BadanieRep_4 extends Badanie2Metod {
   	
	private static final int T_ITERACJI = 5000;
	private static final float T_KONIEC = 2f;
	private static final float T_START = 50f;
	float c1 = 50;
	float c2 = 40;
	float c3 = 50;
	float c4 = 20;
	float epsilonZachlannosci = 0.05f;
	float alfa = 0.05f; // poprawa Q
	float gamma = 0.5f; // 
	
	int[] intentywnosciNaplywu = new int[] {15,25,55};
	int[] maxCzasyOczekiwania = new int[] {100,40, Integer.MAX_VALUE};
	
	
	public BadanieRep_4() {
		serwerMetoda1 = new Serwer("Serwer 1",3);
		serwerMetoda2 = new Serwer("Serwer 2",3);
		setParametryEksperymentu(50000, 5, 50);
	}
	
	@Override
	protected void konfiguracjaGeneratoraLiczb() {
		RandomGenerator.setDefaultSeed(12522);
	}	
	
	private void ustawienieSerwera(Serwer serwerBadania) {
		serwerBadania.setMaxZgloszen(50);
    	
    	// Serwer za kazdym razem obsluguje tylko jedno zgloszenie
    	for (int i = 0; i < serwerBadania.getIloscKolejek(); i++) {
    		serwerBadania.getKolejka(i).setRozkladCzasuObslugi(new RozkladJednostajnyDyskretny(1));
    		serwerBadania.getKolejka(i).setMaxCzasOczekiwania(maxCzasyOczekiwania[i]);
    		serwerBadania.getKolejka(i).setRozkladIlosciPrzybyc(new RozkladBernouliego(intentywnosciNaplywu[i]));
		}
    	
    	serwerBadania.setRozkladCzasuNastawy(new RozkladJednostajnyDyskretny(0));
    	serwerBadania.setWaga(1);
	}
	
	@Override
	protected void konfiguracjaMetoda1(Serwer serwerBadania) {
		ustawienieSerwera(serwerBadania);
    	
		IStan reprezentacjaStanu;
		reprezentacjaStanu = new StanLiczbaSpelnionychOgraniczenCzasSredni();
		serwerBadania.setReprezentacjaStanu(reprezentacjaStanu);
		
		FunkcjaOceny_I fOceny = new OcenaTimeRosnacoOrginalna3(c1,c2,c3,c4);
		serwerBadania.setFunkcjaOceny(fOceny);

		//		Strategia_A strategiaEzachlanna = new StrategiaEZachlanna(epsilonZachlannosci, serwerBadania.getMaxStanow(), serwerBadania.getIloscAkcji());
//		Strategia_A strategiaEzachlanna = new StrategiaEZachlannaDynamiczna(E_START, E_KONIEC, E_ITERACJI , serwerBadania.getMaxStanow(), serwerBadania.getIloscAkcji());
		Strategia_A strategiaEzachlanna = new StrategiaSoftMax(T_START, T_KONIEC, T_ITERACJI , serwerBadania.getMaxStanow(), serwerBadania.getIloscAkcji());
		
		Sterownik sterownik;
		sterownik = new QLearning(strategiaEzachlanna, alfa, gamma);
//		sterownik = new SarsaOnPolicy(strategiaEzachlanna, alfa, gamma);
		serwerBadania.setSterownik(sterownik);
	}

	@Override
	protected void konfiguracjaMetoda2(Serwer serwerBadania) {
		ustawienieSerwera(serwerBadania);
		
		IStan reprezentacjaStanu;
//		reprezentacjaStanu = new StanLiczbaZgloszen();
//		reprezentacjaStanu = new StanBrakReprezentacji();
		reprezentacjaStanu = new StanSortowanyCzasOczekiwaniaZOgraniczeniami();
		
		serwerBadania.setReprezentacjaStanu(reprezentacjaStanu);
		
		FunkcjaOceny_I fOceny = new OcenaTimeRosnacoOrginalna3(c1,c2,c3,c4);
//		FunkcjaOceny_I fOceny = new OcenaTimeRosnacoOrginalna(c1,c2,c3);
		serwerBadania.setFunkcjaOceny(fOceny);
//
//		Strategia_A strategiaEzachlanna = new StrategiaEZachlanna(epsilonZachlannosci, serwerBadania.getMaxStanow(), serwerBadania.getIloscAkcji());
//		Strategia_A strategiaEzachlanna = new StrategiaEZachlannaDynamiczna(E_START, E_KONIEC, E_ITERACJI , serwerBadania.getMaxStanow(), serwerBadania.getIloscAkcji());
		Strategia_A strategiaEzachlanna = new StrategiaSoftMax(T_START, T_KONIEC, T_ITERACJI , serwerBadania.getMaxStanow(), serwerBadania.getIloscAkcji());
		
		Sterownik sterownik;
		sterownik = new QLearning(strategiaEzachlanna, alfa, gamma);
//		sterownik = new SarsaOnPolicy(strategiaEzachlanna, alfa, gamma);
		serwerBadania.setSterownik(sterownik);
	}
	
	public static void main(String[] args) {
		BadanieRep_4 badanie = new BadanieRep_4();
		badanie.wykonajBadanie();
	}

}

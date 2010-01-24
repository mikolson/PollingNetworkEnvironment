package pl.wroc.pwr.iis.simulation.artykul2.fOceny;

import pl.wroc.pwr.iis.polling.model.object.IStan;
import pl.wroc.pwr.iis.polling.model.object.polling.Serwer;
import pl.wroc.pwr.iis.polling.model.sterowanie.FunkcjaOceny_I;
import pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaOceny.routery.OcenaTimeRosnacoOrginalna;
import pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaOceny.routery.OcenaTimeRosnacoOrginalna3;
import pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaOceny.routery.OcenaTimeRosnacoOrginalna4;
import pl.wroc.pwr.iis.polling.model.sterowanie.reprezentacjaStanu.StanLiczbaSpelnionychOgraniczenCzasSredni;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Sterownik;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Qlearning.QLearning;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Qlearning.QLearningWariancja;
import pl.wroc.pwr.iis.polling.model.sterowanie.strategie.StrategiaSoftMax;
import pl.wroc.pwr.iis.polling.model.sterowanie.strategie.Strategia_A;
import pl.wroc.pwr.iis.rozklady.RozkladBernouliego;
import pl.wroc.pwr.iis.rozklady.dyskretne.RozkladJednostajnyDyskretny;
import pl.wroc.pwr.iis.rozklady.random.RandomGenerator;
import pl.wroc.pwr.iis.simulation.Badanie2Metod;

public class Art6_bad_6_wariancja extends Badanie2Metod {
   	
	private static final int T_ITERACJI = 10000;
	private static final float T_KONIEC = 3f;
	private static final float T_START = 100f;
	float c1 = 50;
	float c2 = 70;
	float c3 = 90;
	float c4 = 60;
	float alfa = 0.01f; // poprawa Q
	float gamma = 0.5f; // 
	
	int[] intentywnosciNaplywu = new int[] {20,10,10,25,32};
	int[] maxCzasyOczekiwania = new int[] {70,200,100,150,Integer.MAX_VALUE};
	
	
	public Art6_bad_6_wariancja() {
		serwerMetoda1 = new Serwer("Serwer 1",5);
		serwerMetoda2 = new Serwer("Serwer 2",5);
		setParametryEksperymentu(50000, 10, 100);
	}
	
	@Override
	protected void konfiguracjaGeneratoraLiczb() {
		RandomGenerator.setDefaultSeed(1101);
	}	
	
	
	private void ustawienieSerwera(Serwer serwerBadania) {
    	// Serwer za kazdym razem obsluguje tylko jedno zgloszenie
		serwerBadania.setRozkladIlosciPrzybyc(new RozkladBernouliego(100));
    	for (int i = 0; i < serwerBadania.getIloscKolejek(); i++) {
    		serwerBadania.getKolejka(i).setRozkladCzasuObslugi(new RozkladJednostajnyDyskretny(1));
    		serwerBadania.getKolejka(i).setMaxCzasOczekiwania(maxCzasyOczekiwania[i]);
    		serwerBadania.getKolejka(i).setRozkladIlosciPrzybyc(new RozkladBernouliego(intentywnosciNaplywu[i]));
    		serwerBadania.getKolejka(i).setMaxZgloszen(1000);
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
//		FunkcjaOceny_I fOceny = new OcenaTimeRosnacoOrginalna2(c1,c2,c3,c4);
//		FunkcjaOceny_I fOceny = new OcenaTimeRosnacoOrginalna2(c1,c2,c3);
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
		reprezentacjaStanu = new StanLiczbaSpelnionychOgraniczenCzasSredni();
		serwerBadania.setReprezentacjaStanu(reprezentacjaStanu);
		
		FunkcjaOceny_I fOceny = new OcenaTimeRosnacoOrginalna4(c1,c2,c3,c4);
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
		Art6_bad_6_wariancja badanie = new Art6_bad_6_wariancja();
		badanie.wykonajBadanie();
	}

}

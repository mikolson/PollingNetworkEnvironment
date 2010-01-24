package pl.wroc.pwr.iis.simulation.artykul1.wyniki.badanie4;

import pl.wroc.pwr.iis.polling.model.object.IStan;
import pl.wroc.pwr.iis.polling.model.object.polling.Serwer;
import pl.wroc.pwr.iis.polling.model.sterowanie.FunkcjaOceny_I;
import pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaOceny.routery.OcenaTimeRosnacoOrginalna;
import pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaOceny.routery.OcenaTimeRosnacoOrginalna2;
import pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaOceny.routery.OcenaTimeRosnacoOrginalna3;
import pl.wroc.pwr.iis.polling.model.sterowanie.reprezentacjaStanu.StanLiczbaSpelnionychOgraniczenCzasSredni;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Sterownik;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Qlearning.QLearning;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Qlearning.SarsaOnPolicy;
import pl.wroc.pwr.iis.polling.model.sterowanie.strategie.StrategiaSoftMax;
import pl.wroc.pwr.iis.polling.model.sterowanie.strategie.Strategia_A;
import pl.wroc.pwr.iis.rozklady.RozkladBernouliego;
import pl.wroc.pwr.iis.rozklady.dyskretne.RozkladJednostajnyDyskretny;
import pl.wroc.pwr.iis.rozklady.random.RandomGenerator;
import pl.wroc.pwr.iis.simulation.Badanie2Metod;

public class BadanieArt_4_1 extends Badanie2Metod {
   	
	private static final int 	E_ITERACJI = 5000;
	private static final float	E_KONIEC = 0.01f;
	private static final float	E_START = 0.05f;
	
	private static final int T_ITERACJI = 2000;
	private static final float T_KONIEC = 3f;
	private static final float T_START = 80f;
	float c1 = 50;
	float c2 = 20;
	float c3 = 20;
	float c4 = 20;
	float epsilonZachlannosci = 0.05f;
	float alfa = 0.005f; // poprawa Q
	float gamma = 0.5f; // 
	
	int[] intentywnosciNaplywu = new int[] {50,5,30};
	int[] maxCzasyOczekiwania = new int[] {400,300, Integer.MAX_VALUE};

	int[] intentywnosciNaplywu2 = new int[] {10,60,30};
	int[] maxCzasyOczekiwania2 = new int[] {10,30, Integer.MAX_VALUE};
	int iteracjaZmiany = 50000;
	
	public BadanieArt_4_1() {
		serwerMetoda1 = new Serwer("Serwer 1",3);
		serwerMetoda2 = new Serwer("Serwer 2",3);
		setParametryEksperymentu(100000, 5, 25);
	}
	
	@Override
	protected void konfiguracjaGeneratoraLiczb() {
		RandomGenerator.setDefaultSeed(10222201);
	}	
	
	protected void ustawIteracje(Serwer serwerBadania, int iteracja) {
		if(iteracja == iteracjaZmiany) {
			for (int i = 0; i < serwerBadania.getIloscKolejek(); i++) {
				serwerBadania.getKolejka(i).setRozkladIlosciPrzybyc(new RozkladBernouliego(intentywnosciNaplywu2[i]));
				serwerBadania.getKolejka(i).setMaxCzasOczekiwania(maxCzasyOczekiwania2[i]);
			}
		}
	}
	
	private void ustawienieSerwera(Serwer serwerBadania) {
		serwerBadania.setMaxZgloszen(1000);
    	
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
		
		FunkcjaOceny_I fOceny = new OcenaTimeRosnacoOrginalna(c1,c2,c3);
//		FunkcjaOceny_I fOceny = new OcenaTimeRosnacoOrginalna2(c1,c2,c3);
		serwerBadania.setFunkcjaOceny(fOceny);

//		Strategia_A strategiaEzachlanna = new StrategiaEZachlanna(epsilonZachlannosci, serwerBadania.getMaxStanow(), serwerBadania.getIloscAkcji());
//		Strategia_A strategiaEzachlanna = new StrategiaEZachlannaDynamiczna(E_START, E_KONIEC, E_ITERACJI , serwerBadania.getMaxStanow(), serwerBadania.getIloscAkcji());
		Strategia_A strategiaEzachlanna = new StrategiaSoftMax(T_START, T_KONIEC, T_ITERACJI , serwerBadania.getMaxStanow(), serwerBadania.getIloscAkcji());
		
		Sterownik sterownik;
//		sterownik = new QLearning(strategiaEzachlanna, alfa, gamma);
		sterownik = new SarsaOnPolicy(strategiaEzachlanna, alfa, gamma);
		serwerBadania.setSterownik(sterownik);
	}

	@Override
	protected void konfiguracjaMetoda2(Serwer serwerBadania) {
		ustawienieSerwera(serwerBadania);
		
		IStan reprezentacjaStanu;
		reprezentacjaStanu = new StanLiczbaSpelnionychOgraniczenCzasSredni();
		serwerBadania.setReprezentacjaStanu(reprezentacjaStanu);
		
		FunkcjaOceny_I fOceny = new OcenaTimeRosnacoOrginalna3(c1,c2,c3,c4);
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
		BadanieArt_4_1 badanie = new BadanieArt_4_1();
		badanie.wykonajBadanie();
	}

}

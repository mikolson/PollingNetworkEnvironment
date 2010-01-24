package pl.wroc.pwr.iis.simulation.artykul2.fModel._0_fazaPoczatkowa.parametryUczenia.dlugoscSoftMaxa;

import pl.wroc.pwr.iis.polling.model.object.IStan;
import pl.wroc.pwr.iis.polling.model.object.polling.Serwer;
import pl.wroc.pwr.iis.polling.model.sterowanie.FunkcjaOceny_I;
import pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaOceny.routery.OcenaTimeRosnacoOrginalna4CzasOczekiwaniaPierwszego;
import pl.wroc.pwr.iis.polling.model.sterowanie.reprezentacjaStanu.StanLiczbaSpelnionychOgraniczenMaxCzasOczekiwania;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Sterownik;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Qlearning.QLearning;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Qlearning.QLearningWariancja;
import pl.wroc.pwr.iis.polling.model.sterowanie.strategie.StrategiaSoftMax;
import pl.wroc.pwr.iis.polling.model.sterowanie.strategie.Strategia_A;
import pl.wroc.pwr.iis.rozklady.RozkladBernouliego;
import pl.wroc.pwr.iis.rozklady.dyskretne.RozkladJednostajnyDyskretny;
import pl.wroc.pwr.iis.rozklady.random.RandomGenerator;
import pl.wroc.pwr.iis.simulation.Badanie2Metod;

public class KrotkiSoftMaxa extends Badanie2Metod {
   	
	// Zmniejszono dlugość cyklu ale zwiekszono temperaturę końcową 
	
	private static final int T_ITERACJI = 50;
	private static final float T_KONIEC = 10f;
	private static final float T_START = 100f;
	float epsilonZachlannosci = 0.5f;
	float c1 = 20;
	float c2 = 100;
	float c3 = 300;
	float c4 = 2;
	float alfa = 0.0001f; // poprawa Q
	float gamma = 0.75f; // 
	
	int[] intentywnosciNaplywu = new int[] {50,30,20};
	int[] maxCzasyOczekiwania = new int[] {80,50, Integer.MAX_VALUE};

	int[] intentywnosciNaplywu2 = new int[] {30,45,30};
	int[] maxCzasyOczekiwania2 = new int[] {50,20, Integer.MAX_VALUE};
	
	int[] intentywnosciNaplywu3 = new int[] {30,30,30};
	int[] maxCzasyOczekiwania3 = new int[] {25,10, Integer.MAX_VALUE};

	int iteracjaZmiany[];

	
	public KrotkiSoftMaxa() {
		int iloscCykli = 5000; 
		int zmianaCyklu = 10000;
		serwerMetoda1 = new Serwer("Serwer 1",3);
		serwerMetoda2 = new Serwer("Serwer 2",3);
		setParametryEksperymentu(iloscCykli,100, 10);
		iteracjaZmiany = new int[iloscCykli/zmianaCyklu];
		for (int i = 0; i < iteracjaZmiany.length; i++) {
			iteracjaZmiany[i] = i * zmianaCyklu;
		};
	}
	
	@Override
	protected void konfiguracjaGeneratoraLiczb() {
		RandomGenerator.setDefaultSeed(11232221);
	}	
	
	protected void ustawIteracje(Serwer serwerBadania, int iteracja) {
		for (int c = 0; c < iteracjaZmiany.length; c++) {
			if(iteracjaZmiany[c]  == iteracja) {
				if (c % 3 == 0){
					for (int i = 0; i < serwerBadania.getIloscKolejek(); i++) {
						serwerBadania.getKolejka(i).setRozkladIlosciPrzybyc(new RozkladBernouliego(intentywnosciNaplywu[i]));
						serwerBadania.getKolejka(i).setMaxCzasOczekiwania(maxCzasyOczekiwania[i]);
					}
				} else if (c % 3 == 1){
					for (int i = 0; i < serwerBadania.getIloscKolejek(); i++) {
						serwerBadania.getKolejka(i).setRozkladIlosciPrzybyc(new RozkladBernouliego(intentywnosciNaplywu2[i]));
						serwerBadania.getKolejka(i).setMaxCzasOczekiwania(maxCzasyOczekiwania2[i]);
					}
				} else {
					for (int i = 0; i < serwerBadania.getIloscKolejek(); i++) {
						serwerBadania.getKolejka(i).setRozkladIlosciPrzybyc(new RozkladBernouliego(intentywnosciNaplywu3[i]));
						serwerBadania.getKolejka(i).setMaxCzasOczekiwania(maxCzasyOczekiwania3[i]);
					}
				}
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
		reprezentacjaStanu = new StanLiczbaSpelnionychOgraniczenMaxCzasOczekiwania();
		serwerBadania.setReprezentacjaStanu(reprezentacjaStanu);
		
		FunkcjaOceny_I fOceny = new OcenaTimeRosnacoOrginalna4CzasOczekiwaniaPierwszego(c1,c2,c3,c4);
//		FunkcjaOceny_I fOceny = new OcenaTimeRosnacoOrginalna2(c1,c2,c3);
		serwerBadania.setFunkcjaOceny(fOceny);

//		Strategia_A strategiaEzachlanna = new StrategiaEZachlanna(epsilonZachlannosci, serwerBadania.getMaxStanow(), serwerBadania.getIloscAkcji());
//		Strategia_A strategiaEzachlanna = new StrategiaEZachlannaDynamiczna(E_START, E_KONIEC, E_ITERACJI , serwerBadania.getMaxStanow(), serwerBadania.getIloscAkcji());
		Strategia_A strategiaEzachlanna = new StrategiaSoftMax(T_START, T_KONIEC, T_ITERACJI , serwerBadania.getMaxStanow(), serwerBadania.getIloscAkcji());
		
		Sterownik sterownik;
		sterownik = new QLearning(strategiaEzachlanna, alfa, gamma);
//		sterownik = new QLearningModele(strategiaEzachlanna, alfa, gamma, serwerBadania.getIloscAkcji());
//		sterownik = new SarsaOnPolicy(strategiaEzachlanna, alfa, gamma);
		serwerBadania.setSterownik(sterownik);
	}

	@Override
	protected void konfiguracjaMetoda2(Serwer serwerBadania) {
		ustawienieSerwera(serwerBadania);
		
		IStan reprezentacjaStanu;
		reprezentacjaStanu = new StanLiczbaSpelnionychOgraniczenMaxCzasOczekiwania();
		serwerBadania.setReprezentacjaStanu(reprezentacjaStanu);
		
		FunkcjaOceny_I fOceny = new OcenaTimeRosnacoOrginalna4CzasOczekiwaniaPierwszego(c1,c2,c3,c4);
		serwerBadania.setFunkcjaOceny(fOceny);
//
//		Strategia_A strategiaEzachlanna = new StrategiaEZachlanna(epsilonZachlannosci, serwerBadania.getMaxStanow(), serwerBadania.getIloscAkcji());
//		Strategia_A strategiaEzachlanna = new StrategiaEZachlannaDynamiczna(E_START, E_KONIEC, E_ITERACJI , serwerBadania.getMaxStanow(), serwerBadania.getIloscAkcji());
		Strategia_A strategiaEzachlanna = new StrategiaSoftMax(T_START, T_KONIEC, T_ITERACJI , serwerBadania.getMaxStanow(), serwerBadania.getIloscAkcji());
		
		Sterownik sterownik;
//		sterownik = new QLearning(strategiaEzachlanna, alfa, gamma);
		sterownik = new QLearningWariancja(strategiaEzachlanna, alfa, gamma, serwerBadania.getIloscAkcji());
//		sterownik = new QLearningModele(strategiaEzachlanna, alfa, gamma, serwerBadania.getIloscAkcji());
//		sterownik = new SarsaOnPolicy(strategiaEzachlanna, alfa, gamma);
		serwerBadania.setSterownik(sterownik);
	}
	
	
	public static void main(String[] args) {
		KrotkiSoftMaxa badanie = new KrotkiSoftMaxa();
		badanie.wykonajBadanie();
	}

}

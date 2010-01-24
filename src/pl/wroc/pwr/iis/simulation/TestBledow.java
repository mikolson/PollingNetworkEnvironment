package pl.wroc.pwr.iis.simulation;

import pl.wroc.pwr.iis.polling.model.object.IStan;
import pl.wroc.pwr.iis.polling.model.object.polling.Serwer;
import pl.wroc.pwr.iis.polling.model.sterowanie.FunkcjaOceny_I;
import pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaOceny.routery.OcenaWaitingTimeWithQoS;
import pl.wroc.pwr.iis.polling.model.sterowanie.reprezentacjaStanu.StanLiczbaSpelnionychOgraniczenMaxCzasOczekiwania;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Sterownik;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.normal.ControlFIFO;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.normal.ControlOneQueue;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.normal.ControlPS;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.normal.ControlRR;
import pl.wroc.pwr.iis.polling.model.sterowanie.strategie.StrategiaEZachlanna;
import pl.wroc.pwr.iis.polling.model.sterowanie.strategie.StrategiaSoftMax;
import pl.wroc.pwr.iis.polling.model.sterowanie.strategie.Strategia_A;
import pl.wroc.pwr.iis.rozklady.ciagle.RozkladJednostajnyCiagly;
import pl.wroc.pwr.iis.rozklady.ciagle.RozkladWykladniczy;
import pl.wroc.pwr.iis.rozklady.dyskretne.RozkladPoissona;
import pl.wroc.pwr.iis.rozklady.random.RandomGenerator;

public class TestBledow extends Badanie2Metod {
   	
	private static final int T_ITERACJI =200;
	private static final float T_KONIEC = 5f;
	private static final float T_START = 5f;
	float epsilonZachlannosci = 0.1f;
	float c1 = 20;
	float c2 = 100;
	float c3 = 300;
	float c4 = 2;
	float alfa = 0.2f; // poprawa Q
	float gamma = 0.6f; // 
	
	int eksperymentow = 100;
	double tick = 1;
	int iloscCykli = 2000;

	double[] intentywnosciNaplywu = new double[] {0.15, 0.25, 0.55};
	int[] maxCzasyOczekiwania = new int[] {10,10,10};

//	double[] intentywnosciNaplywu = new double[] {0.15, 0.25, 0.55};
//	int[] maxCzasyOczekiwania = new int[] {10,4, Integer.MAX_VALUE};

	Strategia_A strategia;
	IStan reprezentacjaStanu = new StanLiczbaSpelnionychOgraniczenMaxCzasOczekiwania();
//	FunkcjaOceny_I fOceny = new OcenaTimeRosnacoOrginalna(c1,c2,c3);
	FunkcjaOceny_I fOceny = new OcenaWaitingTimeWithQoS();
	int iteracjaZmiany[ ];

	public TestBledow() {
		runSecondMethod = false;
		plotMethod = false;
		
		int zmianaCyklu = 500000;
		serwerMetoda1 = new Serwer("Serwer 1", intentywnosciNaplywu.length);
		serwerMetoda2 = new Serwer("Serwer 2", intentywnosciNaplywu.length);
		setParametryEksperymentu(iloscCykli,eksperymentow, tick, 1);
		
		iteracjaZmiany = new int[iloscCykli/zmianaCyklu];
		for (int i = 0; i < iteracjaZmiany.length; i++) {
			iteracjaZmiany[i] = i * zmianaCyklu;
		};
	}
	
	@Override
	protected void konfiguracjaGeneratoraLiczb() {
		RandomGenerator.setDefaultSeed(13);
	}	
	
	private void ustawienieSerwera(Serwer serwerBadania) {
		serwerBadania.setMaxZgloszen(10000);
    	
    	// Serwer za kazdym razem obsluguje tylko jedno zgloszenie
    	for (int i = 0; i < serwerBadania.getIloscKolejek(); i++) {
    		serwerBadania.getKolejka(i).setRozkladCzasuObslugi(new RozkladWykladniczy(1));
    		serwerBadania.getKolejka(i).setMaxCzasOczekiwania(maxCzasyOczekiwania[i]);
    		serwerBadania.getKolejka(i).setRozkladIlosciPrzybyc(new RozkladPoissona(intentywnosciNaplywu[i]));
		}
    	
    	serwerBadania.setRozkladCzasuNastawy(new RozkladJednostajnyCiagly(0));
    	serwerBadania.setWaga(1);
	}

	@Override
	protected void konfiguracjaMetoda1(Serwer serwerBadania) {
		ustawienieSerwera(serwerBadania);
		serwerBadania.setReprezentacjaStanu(reprezentacjaStanu);
//		strategia = new StrategiaSoftMax(T_START, T_KONIEC, T_ITERACJI , serwerBadania.getMaxStanow(), serwerBadania.getIloscAkcji());
		strategia = new StrategiaEZachlanna(epsilonZachlannosci, serwerBadania.getMaxStanow(), serwerBadania.getIloscAkcji());

//		FunkcjaOceny_I fOceny = new OcenaTimeRosnacoOrginalna4CzasOczekiwaniaPierwszego(c1,c2,c3,c4);
//		FunkcjaOceny_I fOceny = new OcenaWaitingTimeWithQoS();
		serwerBadania.setFunkcjaOceny(fOceny);
		
		Sterownik sterownik;
//		sterownik = new QLearningWariancja(strategia, alfa, gamma, serwerBadania.getIloscAkcji());
//		sterownik = new QLearning(strategia, alfa, gamma);
//		sterownik = new ControlFIFO_QoSg(serwerBadania);
		sterownik = new ControlOneQueue(0);
//		sterownik = new ControlRR();
		sterownik = new ControlPS();
		sterownik = new ControlFIFO(serwerBadania);
		
//		sterownik = new ControlRandomNonEmpty(serwerBadania);
		serwerBadania.setSterownik(sterownik);
	}
	
	
	@Override
	protected void konfiguracjaMetoda2(Serwer serwerBadania) {
		ustawienieSerwera(serwerBadania);
		serwerBadania.setReprezentacjaStanu(reprezentacjaStanu);
		strategia = new StrategiaSoftMax(T_START, T_KONIEC, T_ITERACJI , serwerBadania.getMaxStanow(), serwerBadania.getIloscAkcji());

//		FunkcjaOceny_I fOceny = new OcenaTimeRosnacoOrginalna4CzasOczekiwaniaPierwszego(c1,c2,c3,c4);
//		FunkcjaOceny_I fOceny = new OcenaTimeRosnacoOrginalna(c1,c2,c3);
		serwerBadania.setFunkcjaOceny(fOceny);

		Sterownik sterownik;
		
//		sterownik = new QLearning(strategia, alfa, gamma);
//		sterownik = new ControlRandom();
//		sterownik = new ControlOneQueue(0);
//		sterownik = new ControlRR();
		sterownik = new ControlPS();
//		sterownik = new ControlFIFO(serwerBadania);
//		sterownik = new ControlRandomNonEmpty(serwerBadania);
//		sterownik = new ControlLongest(serwerBadania);
//		sterownik = new ControlCycleL(10);
//		sterownik = new ControlRequestsF(serwerBadania);
//		sterownik = new ControlEDF(serwerBadania);
//		sterownik = new ControlFIFO_QoS(serwerBadania);
//		sterownik = new ControlRandomNonEmpty(serwerBadania);
		serwerBadania.setSterownik(sterownik);
	}

	public static void main(String[] args) {
		TestBledow badanie = new TestBledow();
		badanie.wykonajBadanie();
	}

//	protected void ustawIteracje(Serwer serwerBadania, int iteracja) {
//		for (int c = 0; c < iteracjaZmiany.length; c++) {
//			if(iteracjaZmiany[c]  == iteracja) {
//				if (c % 3 == 0){
//					for (int i = 0; i < serwerBadania.getIloscKolejek(); i++) {
//						serwerBadania.getKolejka(i).setRozkladIlosciPrzybyc(new RozkladBernouliego(intentywnosciNaplywu[i]));
//						serwerBadania.getKolejka(i).setMaxCzasOczekiwania(maxCzasyOczekiwania[i]);
//					}
//				} else if (c % 3 == 1){
//					for (int i = 0; i < serwerBadania.getIloscKolejek(); i++) {
//						serwerBadania.getKolejka(i).setRozkladIlosciPrzybyc(new RozkladBernouliego(intentywnosciNaplywu2[i]));
//						serwerBadania.getKolejka(i).setMaxCzasOczekiwania(maxCzasyOczekiwania2[i]);
//					}
//				} else {
//					for (int i = 0; i < serwerBadania.getIloscKolejek(); i++) {
//						serwerBadania.getKolejka(i).setRozkladIlosciPrzybyc(new RozkladBernouliego(intentywnosciNaplywu3[i]));
//						serwerBadania.getKolejka(i).setMaxCzasOczekiwania(maxCzasyOczekiwania3[i]);
//					}
//				}
//			}
//		}
//	}
	
}

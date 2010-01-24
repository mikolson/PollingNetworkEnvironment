package pl.wroc.pwr.iis.simulation.artykul2.fModel._4_rozkladPoissona;

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
import pl.wroc.pwr.iis.rozklady.IRozkladDyskretny;
import pl.wroc.pwr.iis.rozklady.RozkladBernouliego;
import pl.wroc.pwr.iis.rozklady.dyskretne.RozkladJednostajnyDyskretny;
import pl.wroc.pwr.iis.rozklady.dyskretne.RozkladPoissona;
import pl.wroc.pwr.iis.rozklady.random.RandomGenerator;
import pl.wroc.pwr.iis.simulation.Badanie2Metod;

public class PoissonSkokowy extends Badanie2Metod {
   	
	private static final int T_ITERACJI =150;
	private static final float T_KONIEC = 10f;
	private static final float T_START = 100f;
	float epsilonZachlannosci = 0.5f;
	float c1 = 20;
	float c2 = 100;
	float c3 = 300;
	float c4 = 2;
	float alfa = 0.0001f; // poprawa Q
	float gamma = 0.75f; //
	
	int[] intentywnosciNaplywu = new int[] {12,3,5}; //20
	int[] maxCzasyOczekiwania = new int[] {100,200,Integer.MAX_VALUE};
	private IRozkladDyskretny rozkladObslugi = new RozkladPoissona(20);
	
	int[] intentywnosciNaplywu2 = new int[] {5,5,5}; // 17
	int[] maxCzasyOczekiwania2 = new int[] {200,40, Integer.MAX_VALUE};
	private IRozkladDyskretny rozkladObslugi2 = new RozkladPoissona(15);

	int[] intentywnosciNaplywu3 = new int[] {2,3,10}; // 16
	int[] maxCzasyOczekiwania3 = new int[] {200,300, Integer.MAX_VALUE};
	private IRozkladDyskretny rozkladObslugi3 = new RozkladPoissona(20);

	int iteracjaZmiany[];

	
	public PoissonSkokowy() {
		MAX_WARTOSC_NA_OSI_Y = 220;
		TYTUL_BADANIA = "The progressive increasing Poisson process as the traffic queue inflow model";

		int dlugoscSymulacji = 6000;
		int zmianaCyklu = 3000;
		serwerMetoda1 = new Serwer("Serwer 1",3);
		serwerMetoda2 = new Serwer("Serwer 2",3);
		setParametryEksperymentu(dlugoscSymulacji,50, 50);
		
		iteracjaZmiany = new int[dlugoscSymulacji/zmianaCyklu];
		for (int i = 0; i < iteracjaZmiany.length; i++) {
			iteracjaZmiany[i] = i * zmianaCyklu;
		}
	}
	
	@Override
	protected void konfiguracjaGeneratoraLiczb() {
		RandomGenerator.setDefaultSeed(113);
	}	
	
	protected void ustawIteracje(Serwer serwerBadania, int iteracja) {
		for (int i = 0; i < iteracjaZmiany.length; i++) {
			if(iteracja == iteracjaZmiany[i]) {
				for (int j = 0; j < serwerBadania.getIloscKolejek(); j++) {
					if (i % 2 == 0) {
						serwerBadania.getKolejka(j).setRozkladCzasuObslugi(rozkladObslugi );
						serwerBadania.getKolejka(j).setMaxCzasOczekiwania(maxCzasyOczekiwania[j]);
						serwerBadania.getKolejka(j).setRozkladIlosciPrzybyc(new RozkladPoissona(intentywnosciNaplywu[j]));
					} else  if (i % 2 == 1){
						serwerBadania.getKolejka(j).setRozkladCzasuObslugi(rozkladObslugi2);
						serwerBadania.getKolejka(j).setMaxCzasOczekiwania(maxCzasyOczekiwania2[j]);
						serwerBadania.getKolejka(j).setRozkladIlosciPrzybyc(new RozkladPoissona(intentywnosciNaplywu2[j]));
					} else {
						serwerBadania.getKolejka(j).setRozkladCzasuObslugi(rozkladObslugi3 );
						serwerBadania.getKolejka(j).setMaxCzasOczekiwania(maxCzasyOczekiwania3[j]);
						serwerBadania.getKolejka(j).setRozkladIlosciPrzybyc(new RozkladPoissona(intentywnosciNaplywu3[j]));
					}
				}
			}
		}
	}
	
	private void ustawienieSerwera(Serwer serwerBadania) {
		serwerBadania.setMaxZgloszen(10000);
    	
    	// Serwer za kazdym razem obsluguje tylko jedno zgloszenie
    	for (int i = 0; i < serwerBadania.getIloscKolejek(); i++) {
//    		serwerBadania.getKolejka(i).setRozkladIlosciObslug(new RozkladJednostajny(1));
    		serwerBadania.getKolejka(i).setRozkladCzasuObslugi(new RozkladPoissona(10));
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
		serwerBadania.setFunkcjaOceny(fOceny);

		Strategia_A strategiaEzachlanna = new StrategiaSoftMax(T_START, T_KONIEC, T_ITERACJI , serwerBadania.getMaxStanow(), serwerBadania.getIloscAkcji());
		
		Sterownik sterownik;
		sterownik = new QLearning(strategiaEzachlanna, alfa, gamma);
		serwerBadania.setSterownik(sterownik);
	}

	static Sterownik sterownik2;
	@Override
	protected void konfiguracjaMetoda2(Serwer serwerBadania) {
		ustawienieSerwera(serwerBadania);

		IStan reprezentacjaStanu;
		reprezentacjaStanu = new StanLiczbaSpelnionychOgraniczenMaxCzasOczekiwania();
		serwerBadania.setReprezentacjaStanu(reprezentacjaStanu);
		
		FunkcjaOceny_I fOceny = new OcenaTimeRosnacoOrginalna4CzasOczekiwaniaPierwszego(c1,c2,c3,c4);
		serwerBadania.setFunkcjaOceny(fOceny);

		Strategia_A strategiaEzachlanna = new StrategiaSoftMax(T_START, T_KONIEC, T_ITERACJI , serwerBadania.getMaxStanow(), serwerBadania.getIloscAkcji());
		sterownik2 = new QLearningWariancja(strategiaEzachlanna, alfa, gamma, serwerBadania.getIloscAkcji());

		serwerBadania.setSterownik(sterownik2);
	}
	
	
	public static void main(String[] args) {
		PoissonSkokowy badanie = new PoissonSkokowy();
		badanie.wykonajBadanie();
	}
}

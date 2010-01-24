package pl.wroc.pwr.iis.simulation.artykul2.fWariancja;

import pl.wroc.pwr.iis.polling.model.object.IStan;
import pl.wroc.pwr.iis.polling.model.object.polling.Serwer;
import pl.wroc.pwr.iis.polling.model.sterowanie.FunkcjaOceny_I;
import pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaOceny.routery.OcenaTimeRosnacoOrginalna4;
import pl.wroc.pwr.iis.polling.model.sterowanie.reprezentacjaStanu.StanLiczbaSpelnionychOgraniczenCzasSredni;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Sterownik;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Qlearning.QLearning;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Qlearning.QLearningWariancja;
import pl.wroc.pwr.iis.polling.model.sterowanie.strategie.StrategiaSoftMax;
import pl.wroc.pwr.iis.polling.model.sterowanie.strategie.Strategia_A;
import pl.wroc.pwr.iis.rozklady.RozkladBernouliego;
import pl.wroc.pwr.iis.rozklady.dyskretne.RozkladJednostajnyDyskretny;
import pl.wroc.pwr.iis.rozklady.dyskretne.RozkladPoissona;
import pl.wroc.pwr.iis.rozklady.random.RandomGenerator;
import pl.wroc.pwr.iis.simulation.Badanie2Metod;

public class BadanieArt_4_1_Wariancja extends Badanie2Metod {
   	
	private static final int T_ITERACJI = 2000;
	private static final float T_KONIEC = 3f;
	private static final float T_START = 80f;
	float c1 = 50;
	float c2 = 20;
	float c3 = 20;
	float c4 = 20;
	float alfa = 0.01f; // poprawa Q
	float gamma = 0.85f; // 
	
	int[] intentywnosciNaplywu = new int[] {50,5,30};
	int[] maxCzasyOczekiwania = new int[] {400,300, Integer.MAX_VALUE};

	int[] intentywnosciNaplywu2 = new int[] {10,60,30};
	int[] maxCzasyOczekiwania2 = new int[] {100,150, Integer.MAX_VALUE};
	
	int[] intentywnosciNaplywu3 = new int[] {4,4,2};
	int[] maxCzasyOczekiwania3 = new int[] {20,30, Integer.MAX_VALUE};

	int[] intentywnosciNaplywu4 = new int[] {5,5,4};
	int[] maxCzasyOczekiwania4 = new int[] {50,100, Integer.MAX_VALUE};

	
	int iteracjaZmiany = 10000;
	int iteracjaZmiany3 = 120000;
	int iteracjaZmiany4 = 170000;
	int iteracjaZmiany5 = 205000;
	
	public BadanieArt_4_1_Wariancja() {
		serwerMetoda1 = new Serwer("Serwer 1",3);
		serwerMetoda2 = new Serwer("Serwer 2",3);
		setParametryEksperymentu(220000, 3, 100);
	}
	
	@Override
	protected void konfiguracjaGeneratoraLiczb() {
		RandomGenerator.setDefaultSeed(22215);
	}	
	
	protected void ustawIteracje(Serwer serwerBadania, int iteracja) {
		if(iteracja == iteracjaZmiany) {
			for (int i = 0; i < serwerBadania.getIloscKolejek(); i++) {
				serwerBadania.getKolejka(i).setRozkladIlosciPrzybyc(new RozkladBernouliego(intentywnosciNaplywu2[i]));
				serwerBadania.getKolejka(i).setMaxCzasOczekiwania(maxCzasyOczekiwania2[i]);
			}
		} else if (iteracja == iteracjaZmiany3) {
			for (int i = 0; i < serwerBadania.getIloscKolejek(); i++) {
				serwerBadania.getKolejka(i).setRozkladIlosciPrzybyc(new RozkladPoissona(intentywnosciNaplywu3[i]));
				serwerBadania.getKolejka(i).setRozkladCzasuObslugi(new RozkladPoissona(10));
				serwerBadania.getKolejka(i).setMaxCzasOczekiwania(maxCzasyOczekiwania3[i]);
			}
		} else if (iteracja == iteracjaZmiany4) {
			for (int i = 0; i < serwerBadania.getIloscKolejek(); i++) {
				serwerBadania.getKolejka(i).setRozkladIlosciPrzybyc(new RozkladPoissona(intentywnosciNaplywu4[i]));
				serwerBadania.getKolejka(i).setRozkladCzasuObslugi(new RozkladPoissona(12));
				serwerBadania.getKolejka(i).setMaxCzasOczekiwania(maxCzasyOczekiwania4[i]);
			}
		} else if (iteracja == iteracjaZmiany5) {
			for (int i = 0; i < serwerBadania.getIloscKolejek(); i++) {
				serwerBadania.getKolejka(i).setRozkladCzasuObslugi(new RozkladPoissona(30));
				serwerBadania.setRozkladCzasuNastawy(new RozkladBernouliego(40));
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
		
		FunkcjaOceny_I fOceny = new OcenaTimeRosnacoOrginalna4(c1,c2,c3,c4);
		serwerBadania.setFunkcjaOceny(fOceny);

		Strategia_A strategiaEzachlanna = new StrategiaSoftMax(T_START, T_KONIEC, T_ITERACJI , serwerBadania.getMaxStanow(), serwerBadania.getIloscAkcji());
		
		Sterownik sterownik;
		sterownik = new QLearning(strategiaEzachlanna, alfa, gamma);
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

		Strategia_A strategiaEzachlanna = new StrategiaSoftMax(T_START, T_KONIEC, T_ITERACJI , serwerBadania.getMaxStanow(), serwerBadania.getIloscAkcji());

		Sterownik sterownik;
		sterownik = new QLearningWariancja(strategiaEzachlanna, alfa, gamma, serwerBadania.getIloscAkcji());
		serwerBadania.setSterownik(sterownik);
	}
	
	public static void main(String[] args) {
		BadanieArt_4_1_Wariancja badanie = new BadanieArt_4_1_Wariancja();
		badanie.wykonajBadanie();
	}

}

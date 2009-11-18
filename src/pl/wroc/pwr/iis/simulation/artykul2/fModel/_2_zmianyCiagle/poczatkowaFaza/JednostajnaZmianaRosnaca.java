package pl.wroc.pwr.iis.simulation.artykul2.fModel._2_zmianyCiagle.poczatkowaFaza;

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
import pl.wroc.pwr.iis.rozklady.RozkladJednostajny;
import pl.wroc.pwr.iis.rozklady.random.RandomGenerator;
import pl.wroc.pwr.iis.simulation.Badanie2Metod;

public class JednostajnaZmianaRosnaca extends Badanie2Metod {
   	
	private static final int T_ITERACJI =150;
	private static final float T_KONIEC = 10f;
	private static final float T_START = 100f;
	float epsilonZachlannosci = 0.5f;
	float c1 = 20;
	float c2 = 100;
	float c3 = 300;
	float c4 = 5;
	float alfa = 0.0001f; // poprawa Q
	float gamma = 0.75f; // 

	int[] intentywnosciNaplywuStart = new int[] {15,15,15};
	int[] maxCzasyOczekiwaniaStart = new int[] {10,20, Integer.MAX_VALUE};
	
	int[] intentywnosciNaplywuKoniec = new int[] {30,33,37};
	int[] maxCzasyOczekiwaniaKoniec = new int[] {100,80, Integer.MAX_VALUE};

	int iteracjaZmianyStart = 3000;
	int iteracjaZmianyKoniec = 6000;
	
	public JednostajnaZmianaRosnaca() {
		MAX_WARTOSC_NA_OSI_Y = 120;
		TYTUL_BADANIA = "Progressive increasing change of traffic intensity and maximum waiting time";
		int iloscCykli = 10000; 
		serwerMetoda1 = new Serwer("Serwer 1",3);
		serwerMetoda2 = new Serwer("Serwer 2",3);
		setParametryEksperymentu(iloscCykli,100, 10);
	}
	
	@Override
	protected void konfiguracjaGeneratoraLiczb() {
		RandomGenerator.setDefaultSeed(11232199);
	}	
	
	protected void ustawIteracje(Serwer serwerBadania, int iteracja) {
		int[] intensywnosci = new int[intentywnosciNaplywuStart.length];
		int[] progi 		= new int[intentywnosciNaplywuStart.length];
		
		if (iteracja > iteracjaZmianyStart && iteracja < iteracjaZmianyKoniec){
			for (int i = 0; i < intensywnosci.length; i++) {
				float aIteracja = iteracja - iteracjaZmianyStart;
				float dlugosc =  iteracjaZmianyKoniec - iteracjaZmianyStart;
				intensywnosci[i] = (int) (intentywnosciNaplywuStart[i] +  (intentywnosciNaplywuKoniec[i] - intentywnosciNaplywuStart[i]) * (aIteracja/dlugosc)) ;
				progi[i] = (int) (maxCzasyOczekiwaniaStart[i] + (maxCzasyOczekiwaniaKoniec[i] - maxCzasyOczekiwaniaStart[i]) * (aIteracja/dlugosc)) ;
			}
		} else if (iteracja < iteracjaZmianyStart) {
			for (int i = 0; i < intensywnosci.length; i++) {
				intensywnosci[i] = intentywnosciNaplywuStart[i];
				progi[i] = maxCzasyOczekiwaniaStart[i];
			}
		} else {
			for (int i = 0; i < intensywnosci.length; i++) {
				intensywnosci[i] = intentywnosciNaplywuKoniec[i];
				progi[i] = maxCzasyOczekiwaniaKoniec[i];
			}
//		} else {
//			System.out.println("Błędna iteracja " + iteracja);
		}
		
		// Ustawienie wartości dla badania
		for (int i = 0; i < serwerBadania.getIloscKolejek(); i++) {
			serwerBadania.getKolejka(i).setRozkladIlosciPrzybyc(new RozkladBernouliego(intensywnosci[i]));
			serwerBadania.getKolejka(i).setMaxCzasOczekiwania(progi[i]);
		}
	}
	
	private void ustawienieSerwera(Serwer serwerBadania) {
		serwerBadania.setMaxZgloszen(1000);
    	
    	// Serwer za kazdym razem obsluguje tylko jedno zgloszenie
    	for (int i = 0; i < serwerBadania.getIloscKolejek(); i++) {
    		serwerBadania.getKolejka(i).setRozkladIlosciObslug(new RozkladJednostajny(1));
    		serwerBadania.getKolejka(i).setMaxCzasOczekiwania(maxCzasyOczekiwaniaStart[i]);
    		serwerBadania.getKolejka(i).setRozkladIlosciPrzybyc(new RozkladBernouliego(intentywnosciNaplywuStart[i]));
		}
    	
    	serwerBadania.setRozkladCzasuNastawy(new RozkladJednostajny(0));
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
		JednostajnaZmianaRosnaca badanie = new JednostajnaZmianaRosnaca();
		badanie.wykonajBadanie();
	}

}

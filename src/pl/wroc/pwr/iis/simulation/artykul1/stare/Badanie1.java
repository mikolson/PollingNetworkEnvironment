/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.wroc.pwr.iis.simulation.artykul1.stare;

import pl.wroc.pwr.iis.polling.model.object.IStan;
import pl.wroc.pwr.iis.polling.model.object.polling.Serwer;
import pl.wroc.pwr.iis.polling.model.sterowanie.FunkcjaOceny_I;
import pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaOceny.routery.OcenaTimeRosnacoOrginalna;
import pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaOceny.routery.OcenaTimeRosnacoOrginalna2;
import pl.wroc.pwr.iis.polling.model.sterowanie.reprezentacjaStanu.StanLiczbaSpelnionychOgraniczenCzasSredni;
import pl.wroc.pwr.iis.polling.model.sterowanie.reprezentacjaStanu.StanSortowanLiczbaZgloszenZOgraniczeniami;
import pl.wroc.pwr.iis.polling.model.sterowanie.reprezentacjaStanu.StanSortowanyCzasOczekiwaniaZOgraniczeniami;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Sterownik;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Qlearning.QLearning;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Qlearning.SarsaOnPolicy;
import pl.wroc.pwr.iis.polling.model.sterowanie.strategie.StrategiaEZachlanna;
import pl.wroc.pwr.iis.polling.model.sterowanie.strategie.StrategiaEZachlannaDynamiczna;
import pl.wroc.pwr.iis.polling.model.sterowanie.strategie.StrategiaSoftMax;
import pl.wroc.pwr.iis.polling.model.sterowanie.strategie.Strategia_A;
import pl.wroc.pwr.iis.rozklady.RozkladBernouliego;
import pl.wroc.pwr.iis.rozklady.dyskretne.RozkladJednostajnyDyskretny;
import pl.wroc.pwr.iis.rozklady.random.RandomGenerator;
import sun.misc.GC;

/**
 *
 * @author Misiek
 */
public class Badanie1 {
	Serwer serwer1 = new Serwer("Serwer 1", 3);
	
	private Sterownik sterownik;
	 
    public void configure() {
    	System.gc();
    	serwer1.setMaxZgloszen(50);
//    	serwer1.setMaxCzasOczekiwania(200); 
    	serwer1.getKolejka(0).setMaxCzasOczekiwania(100);
    	serwer1.getKolejka(1).setMaxCzasOczekiwania(40);
    	serwer1.getKolejka(2).setMaxCzasOczekiwania(Integer.MAX_VALUE);
    	
    	// Serwer za kazdym razem obsluguje tylko jedno zgloszenie
    	serwer1.setRozkladCzasuObslugi(new RozkladJednostajnyDyskretny(1)); 
//    	serwer1.setRozkladIlosciPrzybyc(new RozkladRownomierny(0,3));
    		serwer1.getKolejka(0).setRozkladIlosciPrzybyc(new RozkladBernouliego(30));
    		serwer1.getKolejka(1).setRozkladIlosciPrzybyc(new RozkladBernouliego(25));
    		serwer1.getKolejka(2).setRozkladIlosciPrzybyc(new RozkladBernouliego(60));
    		
    	serwer1.setRozkladCzasuNastawy(new RozkladJednostajnyDyskretny(0));
    	serwer1.setWaga(1);
    	
	   	float c1 = 50;
		float c2 = 20;
		float c3 = 50;
		float epsilonZachlannosci = 0.001f;
		float alfa = 0.05f; // poprawa Q
		float gamma = 0.5f; // 
		
		IStan reprezentacjaStanu;
//		reprezentacjaStanu = new StanLiczbaSpelnionychOgraniczen();
		reprezentacjaStanu = new StanSortowanLiczbaZgloszenZOgraniczeniami();
//		reprezentacjaStanu = new StanSortowanyCzasOczekiwaniaZOgraniczeniami();
		serwer1.setReprezentacjaStanu(reprezentacjaStanu);
		
		FunkcjaOceny_I fOceny = new OcenaTimeRosnacoOrginalna(c1,c2,c3);
//		FunkcjaOceny_I fOceny = new OcenaTimeRosnacoOrginalna2(c1,c2,c3);
		serwer1.setFunkcjaOceny(fOceny);
//
//		Strategia_A strategiaEzachlanna = new StrategiaEZachlanna(epsilonZachlannosci, serwer1.getMaxStanow(), serwer1.getIloscAkcji());
//		Strategia_A strategiaEzachlanna = new StrategiaEZachlannaDynamiczna(0.3f, 0.01f, 2000 , serwer1.getMaxStanow(), serwer1.getIloscAkcji());
		Strategia_A strategiaEzachlanna = new StrategiaSoftMax(200f, 3f, 50000 , serwer1.getMaxStanow(), serwer1.getIloscAkcji());
		
		sterownik = new QLearning(strategiaEzachlanna, alfa, gamma);
//		sterownik = new SarsaOnPolicy(strategiaEzachlanna, alfa, gamma);
//		sterownik = new SterownikEveryVisitMC(strategiaEzachlanna);
//		sterownik = new SterownikNaZmiane();
//		sterownik = new SterownikLosowy();
		serwer1.setSterownik(sterownik);
    }
    
    public void run() {
//    	serwer1.setObslugiwanaKolejka(-1);
    	float[] srednieCzastkowe = new float[serwer1.getIloscKolejek()];
    	float[] srednieCalkowite = new float[serwer1.getIloscKolejek()];
    	double[] srednieKwadratow = new double[serwer1.getIloscKolejek()];
    	
    	long[] ilosci = new long[serwer1.getIloscKolejek()];
    	
    	int eksperymentow = 5;
//    	int iteracji = 400000;
    	int iteracji = 400000;
//    	int badanieCo = 250;
    	int badanieCo = 25000;
    	StringBuffer out = new StringBuffer();
		for (int j = 0; j < eksperymentow; j++) {
			configure();
//			System.out.println(sterownik.getStrategia().getFunkcjaWartosciAkcji());
    		sterownik.startSterowania();
    		serwer1.ustawStanPoczatkowy();
    		 
			for (int i = 0; i < iteracji; i++) {
    			serwer1.wykonajCyklSymulacji();
    			if (i % badanieCo == 0) {
    				out.delete(0, out.length());
    				
    				out.append(i+"\t");
    				for (int k = 0; k < serwer1.getIloscKolejek(); k++) {
    					out.append(srednieCzastkowe[k]/badanieCo);
    					out.append("\t");
    					srednieCzastkowe[k] = 0;
    				}
    				System.out.println(out.toString());
    				
    			} 
    			
    				for (int k = 0; k < serwer1.getIloscKolejek(); k++) {
    					float sredniCzasOczekiwania = serwer1.getKolejka(k).getSredniCzasOczekiwania();
						srednieCzastkowe[k] += sredniCzasOczekiwania; 
						srednieCalkowite[k] = (float) (srednieCalkowite[k] + (1 / (double)(ilosci[k] + 1))*(sredniCzasOczekiwania - srednieCalkowite[k]));
						srednieKwadratow[k] = srednieKwadratow[k] + (1 / (double)(ilosci[k] + 1))*(Math.pow(sredniCzasOczekiwania, 2) - srednieKwadratow[k]);
						
						ilosci[k]++;
					}
    		}
    		sterownik.koniecSterowania();
    		
		}
    	for (int k = 0; k < serwer1.getIloscKolejek(); k++) {
    		System.out.print("# Srednia kolejki: " + srednieCalkowite[k]);
    		System.out.println(" odchylenie: " + Math.pow(srednieKwadratow[k]-Math.pow(srednieCalkowite[k],2), 0.5));
    	}
//    	System.out.println(sterownik.getStrategia().getFunkcjaWartosciAkcji());
	}
    
    public static void main(String[] args) {
    	RandomGenerator.setDefaultSeed(System.currentTimeMillis());
		Badanie1 run = new Badanie1();
		run.run();
	}
    
            
}


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.wroc.pwr.iis.simulation.artykul1.stare;

import pl.wroc.pwr.iis.polling.model.object.IStan;
import pl.wroc.pwr.iis.polling.model.object.polling.Serwer;
import pl.wroc.pwr.iis.polling.model.sterowanie.FunkcjaOceny_I;
import pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaOceny.routery.OcenaTimeRosnacoOrginalna;
import pl.wroc.pwr.iis.polling.model.sterowanie.reprezentacjaStanu.StanLiczbaSpelnionychOgraniczenCzasSredni;
import pl.wroc.pwr.iis.polling.model.sterowanie.reprezentacjaStanu.StanSortowanLiczbaZgloszenZOgraniczeniami;
import pl.wroc.pwr.iis.polling.model.sterowanie.reprezentacjaStanu.StanSortowanyCzasOczekiwaniaZOgraniczeniami;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Sterownik;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Qlearning.QLearning;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Qlearning.SarsaOnPolicy;
import pl.wroc.pwr.iis.polling.model.sterowanie.strategie.StrategiaEZachlanna;
import pl.wroc.pwr.iis.polling.model.sterowanie.strategie.Strategia_A;
import pl.wroc.pwr.iis.rozklady.RozkladBernouliego;
import pl.wroc.pwr.iis.rozklady.RozkladJednostajny;
import sun.misc.GC;

/**
 *
 * @author Misiek
 */
public class OldBadanie2 {
	Serwer serwer1 = new Serwer("Serwer 1", 6);
	
	private Sterownik sterownik;
	 
    public void configure() {
    	System.gc();
    	serwer1.setMaxZgloszen(100);
//    	serwer1.setMaxCzasOczekiwania(200); 
    	serwer1.getKolejka(0).setMaxCzasOczekiwania(70);
    	serwer1.getKolejka(1).setMaxCzasOczekiwania(50);
    	serwer1.getKolejka(2).setMaxCzasOczekiwania(100);
    	serwer1.getKolejka(3).setMaxCzasOczekiwania(120);
    	serwer1.getKolejka(4).setMaxCzasOczekiwania(40);
    	serwer1.getKolejka(5).setMaxCzasOczekiwania(Integer.MAX_VALUE);
    	
    	// Serwer za kazdym razem obsluguje tylko jedno zgloszenie
    	serwer1.setRozkladIlosciObslug(new RozkladJednostajny(1)); 
//    	serwer1.setRozkladIlosciPrzybyc(new RozkladRownomierny(0,3));
    		serwer1.getKolejka(0).setRozkladIlosciPrzybyc(new RozkladBernouliego(15));
    		serwer1.getKolejka(1).setRozkladIlosciPrzybyc(new RozkladBernouliego(15));
    		serwer1.getKolejka(2).setRozkladIlosciPrzybyc(new RozkladBernouliego(10));
    		serwer1.getKolejka(3).setRozkladIlosciPrzybyc(new RozkladBernouliego(20));
    		serwer1.getKolejka(4).setRozkladIlosciPrzybyc(new RozkladBernouliego(10));
    		serwer1.getKolejka(5).setRozkladIlosciPrzybyc(new RozkladBernouliego(25));
    		
    	serwer1.setRozkladCzasuNastawy(new RozkladJednostajny(0));
    	serwer1.setWaga(1);
    	
	   	float c1 = 50;
		float c2 = 20;
		float c3 = 50;
		float epsilonZachlannosci = 0.05f;
		float alfa = 0.05f; // poprawa Q
		float gamma = 0.5f; // 
		
		IStan reprezentacjaStanu;
		reprezentacjaStanu = new StanLiczbaSpelnionychOgraniczenCzasSredni();
//		reprezentacjaStanu = new StanSortowanLiczbaZgloszenZOgraniczeniami();
//		reprezentacjaStanu = new StanSortowanyCzasOczekiwaniaZOgraniczeniami();
		serwer1.setReprezentacjaStanu(reprezentacjaStanu);
		
		FunkcjaOceny_I fOceny = new OcenaTimeRosnacoOrginalna(c1,c2,c3);
		serwer1.setFunkcjaOceny(fOceny);

		Strategia_A strategiaEzachlanna= new StrategiaEZachlanna(epsilonZachlannosci, serwer1.getMaxStanow(), serwer1.getIloscAkcji());
		
//		sterownik = new QLearning(strategiaEzachlanna, alfa, gamma);
		sterownik = new SarsaOnPolicy(strategiaEzachlanna, alfa, gamma);
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
    	
//    	int eksperymentow = 5;
    	int eksperymentow = 5;
    	int iteracji = 700000;
//    	int iteracji = 100000;
//    	int badanieCo = 250;
    	int badanieCo = 2500;
    	StringBuffer out = new StringBuffer();
    	
		for (int j = 0; j < eksperymentow; j++) {
			configure();
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
    		System.out.print(" oczekiwana:" + serwer1.getKolejka(k).getMaxCzasOczekiwania());
    		System.out.println(" odchylenie: " + Math.pow(srednieKwadratow[k]-Math.pow(srednieCalkowite[k],2), 0.5));
    	}
	}
    
    public static void main(String[] args) {
		OldBadanie2 run = new OldBadanie2();
		run.run();
	}
    
            
}

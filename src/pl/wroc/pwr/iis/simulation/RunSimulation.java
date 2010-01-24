/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.wroc.pwr.iis.simulation;

import pl.wroc.pwr.iis.polling.model.object.polling.Serwer;
import pl.wroc.pwr.iis.polling.model.sterowanie.FunkcjaOceny_I;
import pl.wroc.pwr.iis.polling.model.sterowanie.deterministyczne.SterownikNaZmiane;
import pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaOceny.OcenaIlosciZgloszen;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Sterownik;
import pl.wroc.pwr.iis.polling.model.sterowanie.strategie.StrategiaEZachlanna;
import pl.wroc.pwr.iis.polling.model.sterowanie.strategie.Strategia_A;
import pl.wroc.pwr.iis.rozklady.ciagle.RozkladJednostajnyCiagly;
import pl.wroc.pwr.iis.rozklady.ciagle.RozkladWykladniczy;
import pl.wroc.pwr.iis.rozklady.dyskretne.RozkladPoissona;

/**
 *
 * @author Misiek
 */
public class RunSimulation {
	 Serwer serwer1 = new Serwer("Serwer 1", 2);
	private Sterownik sterownik;

	public void configure() {
    	
    	serwer1.setMaxZgloszen(150);
    	serwer1.setMaxCzasOczekiwania(200);
    	serwer1.setRozkladCzasuObslugi(new RozkladWykladniczy(1));
    	
    	serwer1.setRozkladIlosciPrzybyc(new RozkladPoissona(1));
    	serwer1.setRozkladCzasuNastawy(new RozkladJednostajnyCiagly(0));
//    	serwer1.setWagi(new float[]{4,0.1f});
    	
	   	FunkcjaOceny_I fOceny = new OcenaIlosciZgloszen(1000);
	   	 
	   	Strategia_A strategiaEzachlanna= new StrategiaEZachlanna(serwer1.getMaxStanow(), serwer1.getIloscAkcji());
//	   	sterownik = new SterownikEveryVisitMC(strategiaEzachlanna);
	   	sterownik = new SterownikNaZmiane();
		serwer1.setFunkcjaOceny(fOceny);
	    serwer1.setSterownik(sterownik);
    }
    
    public void run() {
//    	serwer1.setObslugiwanaKolejka(-1);
    	for (int j = 0; j < 1000; j++) {
    		sterownik.startSterowania();
    		serwer1.ustawStanPoczatkowy();
    		
    		
    		for (int i = 0; i < 1000; i++) {
    			serwer1.wykonajCyklSymulacji(0.1);
    		}
    		sterownik.koniecSterowania();
		}
    	System.out.println(serwer1);
		
	}
    
    public static void main(String[] args) {
		RunSimulation run = new RunSimulation();
		run.configure();
		run.run();
		System.out.println("test");
	}
    
            
}

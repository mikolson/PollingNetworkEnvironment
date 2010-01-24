package pl.wroc.pwr.iis.simulation.artykul1;

import pl.wroc.pwr.iis.polling.model.object.IStan;
import pl.wroc.pwr.iis.polling.model.object.polling.Serwer;
import pl.wroc.pwr.iis.polling.model.sterowanie.FunkcjaOceny_I;
import pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaOceny.routery.OcenaTimeRosnacoOrginalna;
import pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaOceny.routery.OcenaTimeRosnacoOrginalna2;
import pl.wroc.pwr.iis.polling.model.sterowanie.reprezentacjaStanu.StanLiczbaSpelnionychOgraniczenCzasSredni;
import pl.wroc.pwr.iis.polling.model.sterowanie.reprezentacjaStanu.StanSortowanyCzasOczekiwaniaZOgraniczeniami;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Sterownik;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Qlearning.QLearning;
import pl.wroc.pwr.iis.polling.model.sterowanie.strategie.StrategiaEZachlanna;
import pl.wroc.pwr.iis.polling.model.sterowanie.strategie.StrategiaSoftMax;
import pl.wroc.pwr.iis.polling.model.sterowanie.strategie.Strategia_A;
import pl.wroc.pwr.iis.rozklady.RozkladBernouliego;
import pl.wroc.pwr.iis.rozklady.dyskretne.RozkladJednostajnyDyskretny;
import pl.wroc.pwr.iis.simulation.Badanie2Metod;

public class Badanie2_dlaEZachlannej extends Badanie2Metod {
	private static final int T_ITERACJI = 3000;
	private static final float T_KONIEC = 2f;
	private static final float T_START = 40f;
	float c1 = 50;
	float c2 = 60;
	float c3 = 50;
	float C4 = 20;
	
	float epsilonZachlannosci = 0.05f;
	float alfa = 0.05f; // poprawa Q
	float gamma = 0.5f; // 
	
	public Badanie2_dlaEZachlannej() {
		serwerMetoda1 = new Serwer("Serwer 1",4);
		serwerMetoda2 = new Serwer("Serwer 2",4);
//		setParametryEksperymentu(300000, 30, 3000);
		setParametryEksperymentu(300000, 2, 2000);
//		setParametryEksperymentu(300000, 1, 2500, 20000, 300000);
	}
	
	private void setParametryPodstawowe(Serwer serwerBadania) {
		serwerBadania.setMaxZgloszen(500);
//    	serwer1.setMaxCzasOczekiwania(200); 
    	serwerBadania.getKolejka(0).setMaxCzasOczekiwania(100);
    	serwerBadania.getKolejka(1).setMaxCzasOczekiwania(60);
    	serwerBadania.getKolejka(2).setMaxCzasOczekiwania(40);
    	serwerBadania.getKolejka(3).setMaxCzasOczekiwania(Integer.MAX_VALUE);
    	
    	// Serwer za kazdym razem obsluguje tylko jedno zgloszenie
    	serwerBadania.setRozkladCzasuObslugi(new RozkladJednostajnyDyskretny(1)); 
//    	serwer1.setRozkladIlosciPrzybyc(new RozkladRownomierny(0,3));
		serwerBadania.getKolejka(0).setRozkladIlosciPrzybyc(new RozkladBernouliego(15));
		serwerBadania.getKolejka(1).setRozkladIlosciPrzybyc(new RozkladBernouliego(15));
		serwerBadania.getKolejka(2).setRozkladIlosciPrzybyc(new RozkladBernouliego(20));
		serwerBadania.getKolejka(3).setRozkladIlosciPrzybyc(new RozkladBernouliego(50));
		
    	serwerBadania.setRozkladCzasuNastawy(new RozkladJednostajnyDyskretny(0));
    	serwerBadania.setWaga(1);
	}

	
	@Override
	protected void konfiguracjaMetoda1(Serwer serwerBadania) {
		setParametryPodstawowe(serwerBadania);
    	
		IStan reprezentacjaStanu;
		reprezentacjaStanu = new StanLiczbaSpelnionychOgraniczenCzasSredni();
//		reprezentacjaStanu = new StanSortowanLiczbaZgloszenZOgraniczeniami();
//		reprezentacjaStanu = new StanSortowanyCzasOczekiwaniaZOgraniczeniami();
		serwerBadania.setReprezentacjaStanu(reprezentacjaStanu);
		
		FunkcjaOceny_I fOceny = new OcenaTimeRosnacoOrginalna(c1,c2,c3);
//		FunkcjaOceny_I fOceny = new OcenaTimeRosnacoOrginalna2(c1,c2,c3, C4);
//		FunkcjaOceny_I fOceny = new OcenaTimeRosnacoOrginalna2(c1,c2,c3);
		serwerBadania.setFunkcjaOceny(fOceny);
//
		Strategia_A strategiaEzachlanna = new StrategiaEZachlanna(epsilonZachlannosci, serwerBadania.getMaxStanow(), serwerBadania.getIloscAkcji());
		
		Sterownik sterownik;
		sterownik = new QLearning(strategiaEzachlanna, alfa, gamma);
		serwerBadania.setSterownik(sterownik);
	}

	@Override
	protected void konfiguracjaMetoda2(Serwer serwerBadania) {
		setParametryPodstawowe(serwerBadania);
    	
		IStan reprezentacjaStanu;
		reprezentacjaStanu = new StanLiczbaSpelnionychOgraniczenCzasSredni();
//		reprezentacjaStanu = new StanSortowanLiczbaZgloszenZOgraniczeniami();
//		reprezentacjaStanu = new StanSortowanyCzasOczekiwaniaZOgraniczeniami();
		serwerBadania.setReprezentacjaStanu(reprezentacjaStanu);
		
//		FunkcjaOceny_I fOceny = new OcenaTimeRosnacoOrginalna(c1,c2,c3);
		FunkcjaOceny_I fOceny = new OcenaTimeRosnacoOrginalna2(c1,c2,c3, C4);
		serwerBadania.setFunkcjaOceny(fOceny);
//
		Strategia_A strategiaEzachlanna = new StrategiaEZachlanna(epsilonZachlannosci, serwerBadania.getMaxStanow(), serwerBadania.getIloscAkcji());
		
		Sterownik sterownik;
		sterownik = new QLearning(strategiaEzachlanna, alfa, gamma);
		serwerBadania.setSterownik(sterownik);
	}
	
	
	public static void main(String[] args) {
		Badanie2_dlaEZachlannej badanie = new Badanie2_dlaEZachlannej();
		badanie.wykonajBadanie();
	}

}

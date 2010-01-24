package pl.wroc.pwr.iis.simulation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jfree.ui.RefineryUtilities;

import pl.wroc.pwr.iis.math.OdchylenieStandardowe;
import pl.wroc.pwr.iis.math.Srednia;
import pl.wroc.pwr.iis.polling.model.object.polling.Serwer;
import pl.wroc.pwr.iis.rozklady.random.RandomGenerator;
import pl.wroc.pwr.iis.rozklady.statystyka.MeansEqualityTestModel5;
import pl.wroc.pwr.iis.view.CombinedChart;

public abstract class Badanie2Metod {
	private static final String PLOT_DIRECTORY = "plot-data";

	private static final int ILOSC_BADANYCH_METOD = 2;
	
	protected Serwer serwerMetoda1;
	protected Serwer serwerMetoda2;
	
	private int iloscIteracji = 10000;
	private int iloscEksperymentow = 50;
	private int pomiarCo = 2500;
	private int pomiarOd = 0;
	private int pomiarDo = iloscIteracji;
	
	protected boolean runSecondMethod = true;
	protected boolean plotMethod = true;
	
	protected String NAZWA_PLIKU_WYNIKOWEGO_GNU_PLOTA = "";
	protected double[][][] wyniki; // [numerBadania][kolejka][krokPomiaru]

	protected String TYTUL_BADANIA = "";
	protected int MAX_WARTOSC_NA_OSI_Y = 150;

	private double tick;
	
	protected void setParametryEksperymentu(int iloscIteracji, int iloscEksperymentow, double tick,  int pomiarCo) {
		setIloscIteracji(iloscIteracji);
		this.tick = tick;
		this.iloscEksperymentow = iloscEksperymentow;
		this.pomiarCo = pomiarCo;
	}
	
	protected void setParametryEksperymentu(int iloscIteracji, int iloscEksperymentow, double tick,  int pomiarCo, int pomiarOd, int pomiarDo) {
		setParametryEksperymentu(iloscIteracji, iloscEksperymentow, tick, pomiarCo);
		this.pomiarOd = pomiarOd;
		this.pomiarDo = pomiarDo;
	}
	
	/**
	 * Ustawia ilość wykonywanych iteracji przesuwając jednocześnie granicę badania
	 * @param iloscIteracji
	 */
	protected void setIloscIteracji(int iloscIteracji) {
		this.iloscIteracji = iloscIteracji;
		this.pomiarDo = iloscIteracji;
	}
	
	public void wykonajBadanie() {
		konfiguracjaKolejek();
		konfiguracjaSrodowiska();
		
		konfiguracjaGeneratoraLiczb();
		serwerMetoda1.startEksperymentow();
		for (int numerEksperymentu = 0; numerEksperymentu < iloscEksperymentow; numerEksperymentu++) {
			konfiguracjaMetoda1(serwerMetoda1);
			serwerMetoda1.startSymulacji();
			runSerwer(serwerMetoda1, wyniki[0], numerEksperymentu);
			serwerMetoda1.koniecSymulacji();
		}
		serwerMetoda1.koniecEksperymentow();
		
		if (runSecondMethod) {
			konfiguracjaGeneratoraLiczb();
			for (int numerEksperymentu = 0; numerEksperymentu < iloscEksperymentow; numerEksperymentu++) {
				konfiguracjaMetoda2(serwerMetoda2);
				runSerwer(serwerMetoda2, wyniki[1], numerEksperymentu);
			}
		}
		
		try {
			// Zapisz plik wynikowy - nadaj mu odpowiednia nazwę
	    	String nazwaPliku =  NAZWA_PLIKU_WYNIKOWEGO_GNU_PLOTA;
	    	if ("".equals(nazwaPliku)) {
	    		nazwaPliku = this.getClass().getCanonicalName();
	    	}
	    	nazwaPliku.replaceAll(".", "-");
	    	
			FileWriter fstream = new FileWriter(nazwaPliku);
			BufferedWriter out = new BufferedWriter(fstream);
			
			analizaWynikow(out);
			zapisWynikowGnuPlot(out);
			wyswietlWyniki(out);
			
			out.close();
			
			fstream = new FileWriter(nazwaPliku+".p");
			out = new BufferedWriter(fstream);
			
			String tytulBadania = TYTUL_BADANIA;
			if ("".equals(tytulBadania)) {
				tytulBadania = nazwaPliku;
			}
			
			out.append("load \"gnuplot.config\"\n");
			out.append("set output '"+nazwaPliku+".out'\n");
			out.append("set yr [0:"+ MAX_WARTOSC_NA_OSI_Y +"]\n");
			out.append("set multiplot layout 3, 1 title \"" + tytulBadania + "\"\n");
			out.append("set title \"Standard Q-Learning\"\n");
			
			out.append("plot ");
			for (int i = 0; i < serwerMetoda1.getIloscKolejek()-1; i++) {
				out.append( "\"" + nazwaPliku + "\" using 1:"+ (i + 2) +" title 'Queue "+ (i+1) +"' w l, ");
			}
				out.append(  "\"" + nazwaPliku + "\" using 1:"+ (serwerMetoda1.getIloscKolejek()+1)  +" title 'Best-Effort Queue' w l \n");

			out.append("set title \"AdQ-Learning\"\n");

			out.append("plot ");
			for (int i = 0; i < serwerMetoda2.getIloscKolejek()-1; i++) {
				out.append( "\"" + nazwaPliku + "\" using 1:"+ (i + serwerMetoda1.getIloscKolejek() + 2) +" title 'Queue "+ (i+1) +"' w l, ");
			}
				out.append(  "\"" + nazwaPliku + "\" using 1:"+ (serwerMetoda2.getIloscKolejek() + serwerMetoda1.getIloscKolejek()+1)  +" title 'Best-Effort Queue' w l \n");

			
			out.append("set autoscale\n");
			out.append("set yr [0:1]\n");
			out.append("set format x \"%g\"\n");
			out.append("set format y \"%g\"\n");
			

			out.append("set xlabel \"Iteration Number\" \n");
			out.append("set ylabel \"Anomaly percent\" \n");
			out.append("set title \"Anomaly detection\" \n");
			

			out.append("plot \"" + nazwaPliku +"\" using 1: " + (serwerMetoda2.getIloscKolejek() + serwerMetoda1.getIloscKolejek()+2) + " notitle with lines \n");
				
			out.append("unset multiplot \n");
			out.close();
			
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	private void zapisWynikowGnuPlot(BufferedWriter out) throws IOException {
		Serwer[] serwery = new Serwer[] {serwerMetoda1, serwerMetoda2};
		for (int krokPomiaru = 0; krokPomiaru < wyniki[0][0].length; krokPomiaru++) {
			out.append(krokPomiaru* pomiarCo + "\t");
			for (int numerSerwera = 0; numerSerwera < serwery.length; numerSerwera++) {
				for (int kolejka = 0; kolejka < serwery[numerSerwera].getIloscKolejek(); kolejka++) {
					out.append(wyniki[numerSerwera][kolejka][krokPomiaru]+ "\t");
				}
			}
			out.append(wyniki[serwery.length-1][serwery[serwery.length-1].getIloscKolejek()][krokPomiaru]+ "\t");
			out.append("\n");
		}
	}

	protected void wyswietlWyniki(BufferedWriter out) throws IOException {
		StringBuffer result = new StringBuffer();
		Serwer[] serwery = new Serwer[] {serwerMetoda1, serwerMetoda2};
		int startElement = pomiarOd / pomiarCo;
		int koniecElement = pomiarDo / pomiarCo;
		result.append("# Ilość pomiarów: " + wyniki[0][0].length + "\n");
		result.append("# Pierwszy element: " + startElement);
		result.append("# Końcowy element: " + koniecElement);
		for (int i = 0; i < serwery.length; i++) {
			result.append("# Serwer " + i + "\n");
			for (int j = 0; j < serwery[i].getIloscKolejek(); j++) {
				result.append("# \t k: " + Srednia.sredniaArytmetyczna(wyniki[i][j],startElement, koniecElement));
				result.append("# (odch. st:" + OdchylenieStandardowe.oblicz(wyniki[i][j], startElement, koniecElement) + ")");
				result.append("#  - ograniczenie:" + serwery[i].getKolejka(j).getMaxCzasOczekiwania());
				result.append("# \n");
			}
			result.append("\n");
		}
		out.append(result.toString());
		
		if (plotMethod) {
			CombinedChart combinedChart = new CombinedChart("Wykres 1","K", pomiarCo, wyniki[0], wyniki[1]);
			combinedChart.pack();
			RefineryUtilities.centerFrameOnScreen(combinedChart);
			combinedChart.setVisible(true);
		}
	}

	/**
	 * Ustawia parametry kolejek
	 */
	private void konfiguracjaKolejek() {
		System.gc();
		konfiguracjaMetoda1(serwerMetoda1);
		konfiguracjaMetoda2(serwerMetoda2);
	}

	/**
	 * Ustawia generator liczb pseudolosowych. Oraz wywołuje
	 * konfigurację 2 metod
	 */
	private void konfiguracjaSrodowiska() {
		int iloscPunktowPomiarowych = iloscIteracji/pomiarCo;
		wyniki = new double[ILOSC_BADANYCH_METOD][][];
		
		Serwer[] serwery = new Serwer[] {serwerMetoda1, serwerMetoda2};
		
		for (int i = 0; i < ILOSC_BADANYCH_METOD; i++) {
			// Ilość Kolejek + 1 ze względu na to że trzeba przechowywać również ilość wywołań metody SHAKE w każdym ze strowników
			wyniki[i] = new double[serwery[i].getIloscKolejek()+1][iloscPunktowPomiarowych];
		}
	}
	
	protected void konfiguracjaGeneratoraLiczb() {
		RandomGenerator.setDefaultSeed(System.currentTimeMillis());
	}

	protected abstract void konfiguracjaMetoda1(Serwer serwerBadania);
	protected abstract void konfiguracjaMetoda2(Serwer serwerBadania);

	/**
	 * Analiza wyników testem statystycznym. Porównanie czy metoda 2 jest lepsza od metody 1
	 * @param out 
	 * @throws IOException 
	 */
	protected void analizaWynikow(BufferedWriter out) throws IOException {
		if (serwerMetoda1.getIloscKolejek() != serwerMetoda2.getIloscKolejek()) {
			System.err.println("Nie można przeprowadzić badania statystycznego - ilość kolejek w serwerach różni się");
		} else {
			
			for (int i = 0; i < serwerMetoda1.getIloscKolejek(); i++) {
				MeansEqualityTestModel5 testStatystyczny = new MeansEqualityTestModel5(wyniki[0][i], wyniki[1][i]);
				testStatystyczny.apply(out);
			}
		}
	}
	
//	protected void run(int numerEkspermentu) {
//		runSerwer(serwerMetoda1, wyniki[0], numerEkspermentu);
//		runSerwer(serwerMetoda2, wyniki[1], numerEkspermentu);
//	}

	/**
	 * @param wybranySerwer
	 * @param wyniki [numerKolejki][WartoscPomiaru]
	 * @param oControl 
	 * @param oServer 
	 * @param oQueue 
	 */
	protected void runSerwer(Serwer wybranySerwer, double[][] wyniki, int numerEksperymentu) {
    	double[] srednieCzastkowe = new double[wybranySerwer.getIloscKolejek()+1];
    	
    	StringBuffer out = new StringBuffer();
    	
		wybranySerwer.getSterownik().startSterowania();
		wybranySerwer.ustawStanPoczatkowy();
    		 
		System.out.println("Eksperyment: " + numerEksperymentu + "(serwer: " + wybranySerwer.getNazwa() + ")");
		int ilosci = 0;
		int numerPomiaru = 0;
		for (int iteracja = 0; iteracja < iloscIteracji; iteracja++) {
			ustawIteracje(wybranySerwer, iteracja);
			wybranySerwer.wykonajCyklSymulacji(tick);
			
			// TEN FRAGMENT KODU NALEŻAŁOBY USUNĄĆ
			if (iteracja % pomiarCo == 0) {
				out.delete(0, out.length());
				out.append("(" + wybranySerwer.getNazwa()+") ");
				out.append(" ex: " + numerEksperymentu + "\t");
				out.append(" it: " + iteracja + "\t");
				for (int kolejka = 0; kolejka < srednieCzastkowe.length; kolejka++) {
					wyniki[kolejka][numerPomiaru] = Srednia.sredniaArytmetyczna(wyniki[kolejka][numerPomiaru], srednieCzastkowe[kolejka], numerEksperymentu);
					out.append(srednieCzastkowe[kolejka]);
					out.append("\t");
					srednieCzastkowe[kolejka] = 0;
				}
//				System.out.println(out.toString());
				ilosci = 0;
				numerPomiaru++;
			} 
			
			if (iteracja > pomiarOd && iteracja < pomiarDo){
				for (int k = 0; k < wybranySerwer.getIloscKolejek(); k++) {
//					double sredniCzasOczekiwania = wybranySerwer.getKolejka(k).getSredniCzasOczekiwania();
					double sredniCzasOczekiwania = wybranySerwer.getKolejka(k).getCzasOczekiwania();
					srednieCzastkowe[k] = Srednia.sredniaArytmetyczna(srednieCzastkowe[k], sredniCzasOczekiwania, ilosci); 
				}
				
				// Ostatnia kolejka przechowuje informacje na temat tego ile razy wywołany był shake
				if (wybranySerwer.getSterownik().bylShake()) {
					srednieCzastkowe[srednieCzastkowe.length-1] += 100;	
				}
				 
				ilosci++;
			}
		}
		wybranySerwer.getSterownik().koniecSterowania();
//    	System.out.println(sterownik.getStrategia().getFunkcjaWartosciAkcji());
//    	for (int k = 0; k < wybranySerwer.getIloscKolejek(); k++) {
//    		System.out.print("# Srednia kolejki: " + srednieCalkowite[k]);
//    		System.out.println(" odchylenie: " + Math.pow(srednieKwadratow[k]-Math.pow(srednieCalkowite[k],2), 0.5));
//    	}
	}
	
	
	protected void ustawIteracje(Serwer serwerBadania, int iteracja) {
		
	}
}

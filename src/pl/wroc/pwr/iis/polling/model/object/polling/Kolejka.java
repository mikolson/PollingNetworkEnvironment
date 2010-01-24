/*
 * Kolejka.java
 * 
 * Created on 2007-10-19, 01:17:47
 */
package pl.wroc.pwr.iis.polling.model.object.polling;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pl.wroc.pwr.iis.polling.model.sterowanie.ZdarzenieKolejki;
import pl.wroc.pwr.iis.rozklady.IRozkladCiagly;
import pl.wroc.pwr.iis.rozklady.IRozkladDyskretny;

/**
 * @author Michał Stanek
 */
public class Kolejka extends Wlasciwosci {//implements IElementSymylacji {
	private static final float WARTOSC_NIEAKTUALNA = -1;
	private static final int PIERWSZE_ZGLOSZENIE = 0;
	
	protected double sredniCzasOczekiwania = WARTOSC_NIEAKTUALNA;
	protected double aktualnyCzas = 0;

	protected ArrayList<Zgloszenie> zgloszenia = new ArrayList<Zgloszenie>();
	protected ArrayList<Zgloszenie> zgloszeniaObsluzone = new ArrayList<Zgloszenie>();

	protected Serwer serwer;
	protected int straconych;
	
    private Random random = new Random();
	private int przybyc;
	private int obslug;
	private BufferedWriter fileWaitingTime;
	private BufferedWriter fileServiceTime;
	private BufferedWriter fileLength;
	
	public Kolejka(Serwer serwer) {
		this.aktualnyCzas = 0;
		this.serwer = serwer;
		setWaga(BRAK_WAGI);
	}

	/**
	 * Odpowiada za wygenerowanie nowych przybyć do systemu
	 * 
	 * @see pl.wroc.pwr.iis.polling.model.object.IElementSymylacji#wykonajCyklSymulacji(double)
	 */
	public synchronized void wykonajCyklSymulacji(double aktualnyCzas, double interwal) {
		this.aktualnyCzas = aktualnyCzas; // Zwiększ aktualny czas
		
		// Zapisanie długości kolejki - WAŻNE - przed wygenerowaniem nowych złgoszeń
		
		zgloszeniaObsluzone.clear();
		
		sredniCzasOczekiwania = WARTOSC_NIEAKTUALNA;
		przybyc = 0;
		obslug = 0;
		
		// Uwzglednienie tylko zgloszen wewnetrznych
		przybyc = wygenerujIlosciPrzybyc(interwal);
		
		dodajDoKolejki(przybyc, aktualnyCzas);
		writeLength(aktualnyCzas, getIloscZgloszen());
	}
	
	/**
	 * Przetwarza zadania w kolejce. Jeżeli zaczęto przetwarzać pustą kolejkę cykl zostanie zmarnowany
	 * 
	 * @return Zwraca ilosc pretworzonych zadan
	 */
	public synchronized void przetwarzaj(double aktualnyCzas, double mozliwyCzas) {
		sredniCzasOczekiwania = WARTOSC_NIEAKTUALNA;
//		System.out.println("---------");
//		System.out.println("Przetwarzam o:" + czas);
		
		double pozostalyCzas = 0;
		ZdarzenieKolejki zdarzenie = ZdarzenieKolejki.PUSTA;
		
		if (getIloscZgloszen() > 0) {
			Zgloszenie z = zgloszenia.get(0);
			double ukonczenie = z.doUkonczenia;
			
			if (ukonczenie > mozliwyCzas) {
				z.doUkonczenia = z.doUkonczenia - mozliwyCzas;
				pozostalyCzas = 0;
				zdarzenie = ZdarzenieKolejki.CZAS;
			} else {
				pozostalyCzas = mozliwyCzas - z.doUkonczenia;
				z.czasUkonczenia = aktualnyCzas + z.doUkonczenia; 
				z.doUkonczenia = 0; 
				
				// Zapisanie informacji o przetworzonym zadaniu
				double czasZakonczenia = aktualnyCzas + mozliwyCzas - pozostalyCzas;
				writeServiceTime(czasZakonczenia, z.czasObslugi);
				writeWaitingTime(czasZakonczenia, z.getCzasOczekiwania(czasZakonczenia));

				
				zgloszeniaObsluzone.add(z);
				wstawDoKolejnejKolejki(1); // Usunięcie starych zgloszeń
				
				if (getIloscZgloszen() > 0) {
					zdarzenie = ZdarzenieKolejki.ZGLOSZENIE;
				} else {
					zdarzenie = ZdarzenieKolejki.KOLEJKA;
				}
			}
		} else {
			pozostalyCzas = 0; // Cykl został zmarnowany
			zdarzenie = ZdarzenieKolejki.PUSTA;
		}
		double czasZakonczenia = aktualnyCzas + mozliwyCzas - pozostalyCzas;
		serwer.zakonczonoPrzetwarzac(czasZakonczenia, pozostalyCzas, zdarzenie);
	}

	/**
	 * Dodaje do kolejki nowe zgloszenie - ustawiając jego czas oczekiwania na 0
	 * @param iloscZgloszen
	 */
	public synchronized void dodajDoKolejki(int iloscZgloszen, double czas) {
		for (int i = 0; i < iloscZgloszen; i++) {
			dodajDoKolejki(null);
		}
	}

	/**
	 * Dodanie do kolejki zgłoszenia które pochodzi z innego stanowiska
	 * @param zgloszenie
	 */
	public synchronized void dodajDoKolejki(Zgloszenie zgloszenie) {
		if (getIloscZgloszen() < getOgraniczenieLiczbyZgloszen()) {
			if (zgloszenie == null) {
				double czasObslugi = getCzasObslugi();
				zgloszenie = new Zgloszenie(aktualnyCzas, czasObslugi);
			} else {
				zgloszenie.ustawCzasPrzybycia(aktualnyCzas);
			}
			this.zgloszenia.add(zgloszenie);
		} else {
			this.straconych++;
		}
	}
	
	/**
	 * Pobiera maksymalną liczbę zgłoszeń jaka może znaleźć się w kolejce
	 * jeżeli nie jest ona ustawiona bezpośrednio w kolejce wartość ta
	 * pobierana jest z serwera. Jeżeli tam ona również nie jest ustawiona
	 * to zwracaną wartością jest Integer.MAX_VALUE 
	 */
	public int getOgraniczenieLiczbyZgloszen() {
		int result = getMaxZgloszen();

		if (result < 0) {
			result = getSerwer().getMaxZgloszen();
			
			if (result < 0) {
				result = Integer.MAX_VALUE;
			}
		}
		
		return result;
	}

	public synchronized Zgloszenie pobierzZgloszenie() {
		if (zgloszenia.size() > 0) {
			return zgloszenia.remove(PIERWSZE_ZGLOSZENIE);
		} else {
			System.out.println("Kolejka.pobierzZgloszenie(): probowano pobrac zgloszenia z pustej kolejki");
			return null;
		}
	}

	/**
	 * @return Pobiera liczbę zgłoszeń w kolejce
	 */
	public int getIloscZgloszen() {
		return zgloszenia.size();
	}
	
	/**
	 * @return Pobiera liczbę zgłoszeń w kolejce
	 */
	public int size() {
		return getIloscZgloszen();
	}
	
	
	/**
	 * @return Zwraca liczbę przybyć do kolejki w ostatnim takcie
	 */
	public int getIloscPrzybyc() {
		return przybyc;
	}
	
	/**
	 * @return Zwraca liczbę elementów obsłużonych w kolejce
	 */
	public int getIloscObsluzonych() {
		return obslug; 
	}

	/**
	 * Ze względów optymalizacyjnych zapisywany jest ostatnio obliczony średni czas oczekiwania
	 * W przypadku wywołania metody wykonajCyklSymulacji lub przetwarzaj Zadania wartość ta jest 
	 * invalidowana. 
	 * 
	 * @return Zwraca średni czas oczekiwania zgłoszeń w kolejce
	 */
	public double getSredniCzasOczekiwania() {
		double result = sredniCzasOczekiwania;
		
		if (result == WARTOSC_NIEAKTUALNA) {
			result = 0;
			if (getIloscZgloszen() > 0) {
				for (int i = 0; i < getIloscZgloszen(); i++) {
					result += getCzasOczekiwania(i);
				} 
				result = result/(double)getIloscZgloszen();
			} 
			sredniCzasOczekiwania = result;
		}
		
		return result;
	}
	
	/**
	 * @return Czas jaki jest potrzebny do obsłużenia wszystkich zgłoszeń w kolejce
	 */
	public double getLacznyCzasObslugi(){
		double r = 0;
		
		for (int i = 0; i < getIloscZgloszen(); i++) {
			r += zgloszenia.get(i).doUkonczenia;
		}
		
		return r;
	}
	
	/**
	 * Zwraca średni czas oczekiwania na obsługę dla zgłoszeńia które aktualnie oczekuje w kolejce oraz dla zgłoszeń, które w danym cyklu
	 * zostały już obsłużone
	 * 
	 * @return Zwraca czas oczekiwania pierwszego zgłoszenia w kolejce
	 */
	public double getCzasOczekiwania() {
//		double srednia = 0; 
			
//		if (getIloscZgloszen() > 0) {
//			srednia = zgloszenia.get(PIERWSZE_ZGLOSZENIE).getCzasOczekiwania(aktualnyCzas); 
//		}
//		
//		for (int i = 0; i < zgloszeniaObsluzone.size(); i++) {
//			srednia += zgloszeniaObsluzone.get(i).getCzasOczekiwania();
//		}
		
//		return srednia / (zgloszeniaObsluzone.size()+1);
		return getCzasOczekiwania(0);
	}

	/**
	 * Zwraca czas oczekiwania pierwszego pakietu w kolejce
	 * @param i Numer kolejki
	 * @return
	 */
	public double getCzasOczekiwania(int i) {
		double result = 0;
		if (zgloszenia.size() > i) {
			result = zgloszenia.get(i).getCzasOczekiwania(this.aktualnyCzas);
		}
		return result;
	}
	
	/**
	 * @return generuje ile zgłoszeń pojawi się w kolejce w tym cyklu czasu
	 */
	private int wygenerujIlosciPrzybyc(double kwantCzasu) {
		IRozkladDyskretny rKolejki = getRozkladCzasuPrzybyc();
		IRozkladDyskretny rSerwera = this.serwer.getRozkladCzasuPrzybyc();

 		return losuj(rKolejki, rSerwera, kwantCzasu);
	}

	private double getCzasObslugi() {
		IRozkladCiagly rKolejki = getRozkladCzasuObslugi();
		IRozkladCiagly rSerwera = this.serwer.getRozkladCzasuObslugi();
		
		return losuj(rKolejki, rSerwera);
	}

	public Serwer getSerwer() {
		return serwer;
	}

	/**
	 * @param ilosc 
	 */
	protected void wstawDoKolejnejKolejki(int ilosc) {
		List<Polaczenie> polaczenia = getPolaczeniaWychodzace();

		if (polaczenia == null) {
			polaczenia = getSerwer().getPolaczeniaWychodzace();
		}
		
		int obslug = Math.min(getIloscZgloszen(), ilosc);
		
		if (polaczenia == null) {
			for (int i = 0; i < obslug; i++) {
				pobierzZgloszenie();
			}
		} else {
			for (int i = 0; i < obslug; i++) {
				Polaczenie polaczenie = losujPolaczenie(polaczenia);
				Zgloszenie zgloszenie = pobierzZgloszenie();
				polaczenie.getKolejkaDocelowa().dodajDoKolejki(zgloszenie);
			}
		}
	}

	/**
	 * Losuje polaczenie wychodzące jakim zostanie wysłane zadanie
	 * 
	 * @param polaczenia Lista polaczeń wychodzących
	 * @return Wybrane polaczenie
	 */
	protected Polaczenie losujPolaczenie(List<Polaczenie> polaczenia) {
		Polaczenie result = polaczenia.get(0);
		byte liczba = (byte) random.nextInt(100);
		
		byte suma = 0;
		for (int i = 0; i < polaczenia.size(); i++) {
			suma += polaczenia.get(i).getPrawdopodobienstwo();
			if (suma > liczba) {
				result = polaczenia.get(i);
			}
		}
		
		return result;
	}
	
	public int getStraconych() {
		return straconych;
	}
	
	public void clearStraconych() {
		this.straconych = 0;
	}

	/* (non-Javadoc)
	 * @see pl.wroc.pwr.iis.polling.model.object.polling.Wlasciwosci#getMaxCzasOczekiwania()
	 */
	public double getMaxCzasOczekiwania() {
		double czasOczekiwaniaKolejki = super.getMaxCzasOczekiwania();
		if (czasOczekiwaniaKolejki == BRAK_OGRANICZENIA_CZASOWEGO) {
			czasOczekiwaniaKolejki = serwer.getMaxCzasOczekiwania();
			
			if (czasOczekiwaniaKolejki == BRAK_OGRANICZENIA_CZASOWEGO){
				czasOczekiwaniaKolejki = Integer.MAX_VALUE;
			}
		}
		return czasOczekiwaniaKolejki;
	}
	
	@Override
	public int getMaxZgloszen() {
		int result = super.getMaxZgloszen();
		
		if (result == Wlasciwosci.BRAK_OGRANICZENIA_ZGLOSZEN) {
			result = getSerwer().getMaxZgloszen();
		}
		
		return result;
	}

	@Override
	public float getWaga() {
		float result = super.getWaga();
		
		if (result == Wlasciwosci.BRAK_WAGI) {
			result = getSerwer().getWaga();
			
			if (result == Wlasciwosci.BRAK_WAGI) {
				result = 1;
			}
		} 
		
		return result;
	}

	public void ustawStanPoczatkowy() {
		straconych = 0;
		zgloszenia.clear();
	}

	public String toStringHeader() {
		return  "Sredni Czas Oczekiwania;Czas Oczekiwania pierwszego;Ilosc zgloszen;Ilosc przybyc;Ilosc obsluzonych";
	}
	
	@Override
	public String toString() {
		StringBuffer out = new StringBuffer();
		
		double[] res = new double[]{
			getSredniCzasOczekiwania(), getCzasOczekiwania(), getIloscZgloszen(), getIloscPrzybyc(), getIloscObsluzonych()  
		};
		
		for (int i = 0; i < res.length-1; i++) {
			out.append(res[i]);
			out.append(";");
		}
		out.append(res[res.length-1]);
		return out.toString();
	}

	public void createFiles(String folder, int numerKolejki) {
		try {
			String waiting = folder + File.separator + numerKolejki + "-queue.waiting.data";
			String service = folder + File.separator + numerKolejki + "-queue.service.data";
			String length = folder + File.separator + numerKolejki + "-queue.length.data";

			String configuration = folder + File.separator + numerKolejki + "-queue.config";
			
			fileWaitingTime = new BufferedWriter(new FileWriter(waiting));
			fileServiceTime = new BufferedWriter(new FileWriter(service));
			fileLength = new BufferedWriter(new FileWriter(length));
			
			BufferedWriter[] files = new BufferedWriter[] {fileLength, fileServiceTime, fileWaitingTime};
			for (BufferedWriter file : files) {
				writeLine(file, "0.0\t0");
			}
			
//			writeConfiguration(configuration);
		} catch (Exception e) { 
			System.err.println("Problem z plikami: " + e.getMessage());
		}
	}
	
	/**
	 * Zapisuje do pliku linijkę wyników
	 * @param plik
	 * @param zawartosc
	 */
	private void writeLine(BufferedWriter plik, String zawartosc) {
		try {
			plik.append(zawartosc + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private void writeConfiguration(String fileName) throws IOException {
		BufferedWriter file = new BufferedWriter(new FileWriter(fileName));
		file.append("Ograniczenie czasu oczekiwania: " + getMaxCzasOczekiwania() + "\n");
		file.append("Ograniczenie liczby zgłoszeń: " + getMaxZgloszen() + "\n");

		file.append("Waga kolejki: " + getWaga() + "\n");
		
		file.append("Czas obsługi: " + getRozkladCzasuObslugi().toString() + "\n");
		file.append("Czas nastawy: " + getRozkladCzasuNastawy().toString() + "\n");
		file.append("Czas przybyć: " + getRozkladCzasuPrzybyc().toString() + "\n");
		file.close();
	}

	/**
	 * Zamyka pliki wyników
	 */
	public void closeFiles() {
		try {
			fileWaitingTime.close();
			fileLength.close();
			fileServiceTime.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Dodaje do plików linie oddzielające eksperymenty
	 */
	public void nextExperiment() {
		BufferedWriter[] files = new BufferedWriter[] {fileLength, fileServiceTime, fileWaitingTime};
		for (BufferedWriter file : files) {
			writeLine(file, "\n\n# Next experiment");
		}
	}
	
	protected void writeWaitingTime(double time, double value){
		writeLine(fileWaitingTime, time + "\t"+value);
	}
	
	protected void writeServiceTime(double time, double value){
		writeLine(fileServiceTime, time + "\t"+value);
	}
	
	protected void writeLength(double time, int value){
		writeLine(fileLength, time + "\t"+value);
	}

}

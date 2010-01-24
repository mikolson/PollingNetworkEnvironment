/*
 * Serwer.java
 * 
 * Created on 2007-10-19, 01:17:34
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.wroc.pwr.iis.polling.model.object.polling;

import java.io.File;
import java.util.ArrayList;

import pl.wroc.pwr.iis.polling.model.object.IElementSymylacji;
import pl.wroc.pwr.iis.polling.model.object.IObiektSterowany;
import pl.wroc.pwr.iis.polling.model.object.IStan;
import pl.wroc.pwr.iis.polling.model.sterowanie.FunkcjaOceny_I;
import pl.wroc.pwr.iis.polling.model.sterowanie.Sterownik_I;
import pl.wroc.pwr.iis.polling.model.sterowanie.ZdarzenieKolejki;
import pl.wroc.pwr.iis.rozklady.IRozkladCiagly;
import pl.wroc.pwr.iis.rozklady.IRozkladDyskretny;
import pl.wroc.pwr.iis.rozklady.ciagle.RozkladJednostajnyCiagly;

/**
 * @author Misiek
 */
public class Serwer extends Wlasciwosci implements IElementSymylacji, IObiektSterowany {
	private static final String PLOT_DIRECTORY = "plot-data";
	
	public static final IRozkladCiagly BRAK_ROZKLADU = new RozkladJednostajnyCiagly(0);
	public static final int BRAK_OBSLUGI = -1; // nie ma juz czegos takiego jak brak obslugi

	protected ArrayList<Kolejka> kolejki = new ArrayList<Kolejka>(5);
	protected ArrayList<Polaczenie> polaczeniaWychodzace = new ArrayList<Polaczenie>(5);

	private int obslugiwanaKolejka = BRAK_OBSLUGI;
	protected double cyklNastawy = 0;

	protected Sterownik_I sterownik;
	
	/** Funkcja oceny stanu - funkcja oceniajaca aktualny stan. 
	 *  Domyslnie tworzona jest funkcja oceniająca liczbę zgłoszeń w kolejkach */
	protected FunkcjaOceny_I funkcjaOceny;

	/** Reprezentacja stanu serwera - domyślnie tworzony jest stan reprezentujacy liczbę zgłoszen */
	protected IStan reprezentacjaStanu;
	private double aktualnyCzas;

	/* ----------------------------------------------------------------
	 * Kontruktory
	 * ---------------------------------------------------------------- */
	public Serwer(String text, int iloscKolejek) {
		super(text);
		setRozkladCzasuNastawy(BRAK_ROZKLADU);
		setWaga(1);
		addKolejki(iloscKolejek);
		
		this.aktualnyCzas = 0;
	}

	/* ----------------------------------------------------------------
	 * Metody
	 * ---------------------------------------------------------------- */
	
	protected Kolejka addKolejka() {
		return addKolejka("Kolejka " + kolejki.size());
	}

	protected Kolejka addKolejka(String nazwa) {
		Kolejka k = new Kolejka(this);
		k.setNazwa(nazwa);
		kolejki.add(k);

		return k;
	}

	protected void addKolejki(int i) {
		for (int j = 0; j < i; j++) {
			addKolejka();
		}
	}

	public void addPolaczenieWychodzace(Kolejka kolejka, int prawdopodobienstwo) {
		Polaczenie polaczenie = new Polaczenie(kolejka, prawdopodobienstwo);
		this.polaczeniaWychodzace.add(polaczenie);
	}

	public void setMaxCzasOczekiwania(int[] maxCzasyOczekiwania) {
		if (!(maxCzasyOczekiwania.length >= 0 && maxCzasyOczekiwania.length < kolejki.size())) {
			throw new RuntimeException("Błędne parametry");
		}

		for (int i = 0; i < maxCzasyOczekiwania.length; i++) {
			kolejki.get(i).setMaxCzasOczekiwania(maxCzasyOczekiwania[i]);
		}
	}

	public void setRozkladCzasuObslugi(IRozkladCiagly[] czasyObslugi) {
		if (!(czasyObslugi.length >= 0 && czasyObslugi.length < kolejki.size())) {
			throw new RuntimeException("Błędne parametry");
		}
		for (int i = 0; i < czasyObslugi.length; i++) {
			kolejki.get(i).setRozkladCzasuObslugi(czasyObslugi[i]);
		}
	}

	public void setRozkladCzasuPrzybyc(IRozkladDyskretny[] czasyPrzybyc) {
		if (czasyPrzybyc.length != kolejki.size()) {
			throw new RuntimeException("Błędne parametry");
		}
		for (int i = 0; i < czasyPrzybyc.length; i++) {
			kolejki.get(i).setRozkladIlosciPrzybyc(czasyPrzybyc[i]);
		}
	}

	public void setRozkladyCzasuNastawy(
			IRozkladDyskretny[] czasyNastawy) {
		if (!(czasyNastawy.length >= 0 && czasyNastawy.length < kolejki.size())) {
			throw new RuntimeException("Błędne parametry");
		}

		for (int i = 0; i < czasyNastawy.length; i++) {
			kolejki.get(i).setRozkladIlosciPrzybyc(czasyNastawy[i]);
		}
	}

	public Kolejka getKolejka(int numer) {
		if (!(numer >= 0 && numer < kolejki.size())) {
			throw new RuntimeException("Błędne parametry");
		}
		return this.kolejki.get(numer);
	}
	
	public int getObslugiwanaKolejka() {
		return obslugiwanaKolejka;
	}

	public void setObslugiwanaKolejka(int nowaKolejka) {
		if ((nowaKolejka < 0 && nowaKolejka >= kolejki.size() && nowaKolejka != BRAK_OBSLUGI)) {
			throw new RuntimeException("Błędne parametry");
		}
		
		// Ustawienie ilosci cykli biernych przy przelaczeniu obslugi kolejek
		// Przełączenie obsługi na tą samą kolejkę nie może generować czasu nastawy
		if (nowaKolejka != BRAK_OBSLUGI && nowaKolejka != this.obslugiwanaKolejka) {
			double czas = losuj(getKolejka(nowaKolejka).getRozkladCzasuNastawy(), getRozkladCzasuNastawy());
			cyklNastawy = czas;
		}

		this.obslugiwanaKolejka = nowaKolejka;
	}

	public void setMaxZgloszen(int[] ograniczeniaLiczbyZgloszen) {
		if (ograniczeniaLiczbyZgloszen.length != kolejki.size()) {
			throw new RuntimeException("Błędne parametry");
		}

		for (int i = 0; i < kolejki.size(); i++) {
			kolejki.get(i).setMaxZgloszen(ograniczeniaLiczbyZgloszen[i]);
		}
	}

	public void setWagi(float[] wagi) {
		if (wagi.length != kolejki.size()) {
			throw new RuntimeException("Błędne parametry");
		}

		for (int i = 0; i < kolejki.size(); i++) {
			kolejki.get(i).setWaga(wagi[i]);
		}
	}

	public int getIloscKolejek() {
		return this.kolejki.size();
	}

	public Sterownik_I getSterownik() {
		return sterownik;
	}

	public void setSterownik(Sterownik_I sterownik) {
		this.sterownik = sterownik;
	}

	public FunkcjaOceny_I getFunkcjaOceny() {
		return funkcjaOceny;
	}

	public void setFunkcjaOceny(FunkcjaOceny_I funkcjaOceny) {
		this.funkcjaOceny = funkcjaOceny;
	}
	
	public IStan getReprezentacjaStanu() {
		return reprezentacjaStanu;
	}

	public void setReprezentacjaStanu(IStan reprezentacjaStanu) {
		this.reprezentacjaStanu = reprezentacjaStanu;
	}

	/**
	 * @return Zwraca aktualny stan serwera - stany poszczegolnych kolejek
	 */
	public int[] getStan() {
		int[] result;
		if (getReprezentacjaStanu() != null) {
			result = getReprezentacjaStanu().getStan(this);
		} else {
			System.out.println("Serwer.getStan(): Nie podano funkcji reprezentacji stanu");
			result = new int[0];
		}
		return result;
	}

	/**
	 * @return Zwraca maxymalna liczbe stanow dla kazdej z kolejek
	 */
	public int[] getMaxStanow() {
		return getReprezentacjaStanu().getMaxStanow(this);
	}

	/**
	 * @return Zwraca liczbę dozwolonych akcji dla
	 */
	public int getIloscAkcji() {
		return getIloscKolejek();
	}

	/**
	 * @return Zwraca ocenę sytuacji po zastosowaniu funckji Oceny
	 */
	public double getOcenaSytuacji() {
		double result = 0;
		if (funkcjaOceny != null) { result = funkcjaOceny.ocenaSytuacji(this); } 
		else { System.out.println("Serwer.getOcenaSytuacji(): Nie przypisano funkcji oceny systuacji"); }
		return result;
	}
	
	/**
	 * Metode należy wykonać w przypadku kiedy nastąpiło w systemie wywołanie zdarzenia. 
	 * Sprawdza ona czy sterownik reaguje na dane zdarzenie i pobiera z niego nową decyzję sterującą. 
	 * 
	 * @param zdarzenieObslugiwanePrzezSterownik
	 * @param zdarzenieWygenerowane
	 * 
	 * @return
	 */
	private int podjacDecyzjeSterujaca(ZdarzenieKolejki zdarzenieObslugiwanePrzezSterownik, ZdarzenieKolejki zdarzenieWygenerowane) {
		int resultAction = getObslugiwanaKolejka();
		// Jeżeli zgłoszone zdarzenie jest identyczne ze zdarzeniem obsługiwanym 
		// lub jeżeli zdarzenie wygenerowane to koniec kolejki, a sterownik obsługuje koniec zgloszenia, to są to zdarzenia tożsame
		
//		if (zdarzenieWygenerowane == ZdarzenieKolejki.ZGLOSZENIE || zdarzenieWygenerowane == ZdarzenieKolejki.KOLEJKA ){
//			System.out.println(zdarzenieWygenerowane);
//		}
		
		// Każdy sterownik reaguje na zdarzenie obsługiwane przez siebie
		// każdy sterownik reaguje również na zdarzenie typu kolejka pusta 
		// Sterownik obsługujący zdarzenie typu obsługa zdarzenia reaguje również na zakończenie obsługi kolejki
		if ( (zdarzenieWygenerowane == zdarzenieObslugiwanePrzezSterownik) || (zdarzenieWygenerowane == ZdarzenieKolejki.PUSTA) || 
				zdarzenieWygenerowane == ZdarzenieKolejki.KOLEJKA && zdarzenieObslugiwanePrzezSterownik == ZdarzenieKolejki.ZGLOSZENIE) {
			resultAction = this.sterownik.getDecyzjaSterujaca(getOcenaSytuacji(), getStan(), getIloscAkcji());
//			System.out.println("wołam");
		}
		return resultAction;
	}
	
	/**
	 * Jeżeli kolejka była pusta czas został zmarnowany, ale zostanie podjęta nowa decyzja sterująca
	 * @param aktualnyCzas
	 * @param mozliwyCzas
	 * @param zdarzenieWygenerowane
	 */
	public void zakonczonoPrzetwarzac(double aktualnyCzas, double mozliwyCzas, ZdarzenieKolejki zdarzenieWygenerowane){
		int nowaAkcja = podjacDecyzjeSterujaca(sterownik.getDecyzjaNaZdarzenie(), zdarzenieWygenerowane);
		setObslugiwanaKolejka(nowaAkcja);

		// Jeżeli nastąpiła zmiana kolejki należy dokonać nastawy
		if (mozliwyCzas > 0) {
			double pozostalyCzas = dokonajNastawy(mozliwyCzas);
			aktualnyCzas = aktualnyCzas + mozliwyCzas - pozostalyCzas;
			getKolejka(getObslugiwanaKolejka()).przetwarzaj(aktualnyCzas, pozostalyCzas);
		}
	}
	
	protected double dokonajNastawy(double tick){
		// Obsługa czasu przełączenia
		if (cyklNastawy > 0) {
			if (tick <= cyklNastawy) {
				cyklNastawy -= tick;
				tick = 0;
			} else {
				tick = cyklNastawy - tick;
			}
		}
		
		return tick;
	}
	
	public void wykonajCyklSymulacji(double tick) {
//		System.out.println("Cykl symulacji:" + tick);
		// Kolejki zawsze muszą wygenerować tyle zgłoszeń ile czasu upłynęło 
		for (int i = 0; i < kolejki.size(); i++) {
			getKolejka(i).wykonajCyklSymulacji(aktualnyCzas, tick);
//			System.out.println(i+": " + getKolejka(i).getIloscZgloszen() + " czas: " + getKolejka(i).getLacznyCzasObslugi() );
//			System.out.println(i+": " + getKolejka(i).getIloscZgloszen() + " czas: " + getKolejka(i).getSredniCzasOczekiwania() );
//			System.out.println(i+": " + getKolejka(i).getIloscZgloszen() + " czas: " + getKolejka(i).getCzasOczekiwania() );
		}
		
		// To nie może być wyżej bo popsuje czas generowania do komórek
		tick = dokonajNastawy(tick);
//		System.out.println("Obsługiwana kolejka:" + obslugiwanaKolejka);
		if (tick > 0 && obslugiwanaKolejka >= 0 && obslugiwanaKolejka < getIloscKolejek()) {
			getKolejka(obslugiwanaKolejka).przetwarzaj(aktualnyCzas, tick);
		}
		this.aktualnyCzas += tick;
	}
	
	public void ustawStanPoczatkowy() {
		cyklNastawy = 0;
		obslugiwanaKolejka = 0;
		for (int i = 0; i < getIloscKolejek(); i++) {
			kolejki.get(i).ustawStanPoczatkowy();
		}
	}

	public String toStringHeader() {
		return  "Obslugiwana kolejka,Ocena stanu";
	}
	
	public String toKolejkiStringHeader() {
		String result  = "";
		for (int i = 0; i < getIloscKolejek(); i++) {
			result += "Kolejka " + (i+1) +";"+getKolejka(i).toStringHeader();
		}
		return result;
	}

	@Override
	public String toString() {
		StringBuffer out = new StringBuffer();
		
		double[] res = new double[]{
			getObslugiwanaKolejka(), getOcenaSytuacji(),  
		};
		
		for (int i = 0; i < res.length-1; i++) {
			out.append(res[i]);
			out.append(";");
		}
		out.append(res[res.length-1]);

		return out.toString();
	}
	
	
	public String[] getKolejkiString() {
		String[] result = new String[getIloscKolejek()];
		
		for (int numerKolejki = 0; numerKolejki < getIloscKolejek(); numerKolejki++) {
			result[numerKolejki] = numerKolejki + ";" + getKolejka(numerKolejki).toString();
		}
		
		return result;
	}

	@Override
	public void koniecEksperymentow() {
		for (int i = 0; i < getIloscKolejek(); i++) {
			getKolejka(i).closeFiles();
		}
	}

	@Override
	public void koniecSymulacji() {
		for (int i = 0; i < getIloscKolejek(); i++) {
			getKolejka(i).nextExperiment();
		}
	}

	@Override
	public void startEksperymentow() {
		for (int i = 0; i < getIloscKolejek(); i++) {
			getKolejka(i).createFiles(PLOT_DIRECTORY,i+1);
		}
	}

	@Override
	public void startSymulacji() {
		aktualnyCzas = 0;
	}
	
}

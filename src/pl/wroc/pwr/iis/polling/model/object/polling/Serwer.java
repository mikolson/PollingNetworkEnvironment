/*
 * Serwer.java
 * 
 * Created on 2007-10-19, 01:17:34
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.wroc.pwr.iis.polling.model.object.polling;

import java.util.ArrayList;

import pl.wroc.pwr.iis.polling.model.object.IElementSymylacji;
import pl.wroc.pwr.iis.polling.model.object.IObiektSterowany;
import pl.wroc.pwr.iis.polling.model.object.IStan;
import pl.wroc.pwr.iis.polling.model.sterowanie.FunkcjaOceny_I;
import pl.wroc.pwr.iis.polling.model.sterowanie.Sterownik_I;
import pl.wroc.pwr.iis.rozklady.IRozkladPrawdopodobienstwa;
import pl.wroc.pwr.iis.rozklady.RozkladJednostajny;

/**
 * @author Misiek
 */
public class Serwer extends Wlasciwosci implements IElementSymylacji, IObiektSterowany {
	public static final IRozkladPrawdopodobienstwa BRAK_ROZKLADU = new RozkladJednostajny(0);

	public static final int BRAK_OBSLUGI = -1; // nie ma juz czegos takiego jak brak obslugi

	protected ArrayList<Kolejka> kolejki = new ArrayList<Kolejka>(5);
	protected ArrayList<Polaczenie> polaczeniaWychodzace = new ArrayList<Polaczenie>(5);

	protected int obslugiwanaKolejka = BRAK_OBSLUGI;
	protected int cyklNastawy = 0;

	protected Sterownik_I sterownik;
	
	/** Funkcja oceny stanu - funkcja oceniajaca aktualny stan. 
	 *  Domyslnie tworzona jest funkcja oceniająca liczbę zgłoszeń w kolejkach */
	protected FunkcjaOceny_I funkcjaOceny;

	/** Reprezentacja stanu serwera - domyślnie tworzony jest stan reprezentujacy liczbę zgłoszen */
	protected IStan reprezentacjaStanu;

	/* ----------------------------------------------------------------
	 * Kontruktory
	 * ---------------------------------------------------------------- */
	public Serwer(String text, int iloscKolejek) {
		super(text);
		setRozkladCzasuNastawy(BRAK_ROZKLADU);
		setWaga(1);
		addKolejki(iloscKolejek);
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

	public void setRozkladCzasuObslugi(IRozkladPrawdopodobienstwa[] czasyObslugi) {
		if (!(czasyObslugi.length >= 0 && czasyObslugi.length < kolejki.size())) {
			throw new RuntimeException("Błędne parametry");
		}
		for (int i = 0; i < czasyObslugi.length; i++) {
			kolejki.get(i).setRozkladIlosciObslug(czasyObslugi[i]);
		}
	}

	public void setRozkladCzasuPrzybyc(IRozkladPrawdopodobienstwa[] czasyPrzybyc) {
		if (czasyPrzybyc.length != kolejki.size()) {
			throw new RuntimeException("Błędne parametry");
		}
		for (int i = 0; i < czasyPrzybyc.length; i++) {
			kolejki.get(i).setRozkladIlosciPrzybyc(czasyPrzybyc[i]);
		}
	}

	public void setRozkladyCzasuNastawy(
			IRozkladPrawdopodobienstwa[] czasyNastawy) {
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

	public void setObslugiwanaKolejka(int numer) {
		if ((numer < 0 && numer >= kolejki.size() && numer != BRAK_OBSLUGI)) {
			throw new RuntimeException("Błędne parametry");
		}

		// Ustawienie ilosci cykli biernych przy przelaczeniu obslugi kolejek
		if (numer != BRAK_OBSLUGI) {
			int czas = getIloscWylosowanych(getKolejka(numer)
					.getRozkladCzasuNastawy(), getRozkladCzasuNastawy());
			cyklNastawy = czas;
		}

		this.obslugiwanaKolejka = numer;
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

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();

		for (int i = 0; i < this.kolejki.size(); i++) {
			result.append(this.kolejki.get(i).getNazwa());
			result.append(" :: ");
			result.append(this.kolejki.get(i).getIloscZgloszen());
			result.append("\n");
		}

		return result.toString();
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

	public float getOcenaSytuacji() {
		float result = 0;
		if (funkcjaOceny != null) {
			result = funkcjaOceny.ocenaSytuacji(this);
		} else {
			System.out.println("Serwer.getOcenaSytuacji(): Nie przypisano funkcji oceny systuacji");
		}
		return result;
	}
	
	public void wykonajCyklSymulacji() {
		
		if(sterownik != null){
			int akcja = this.sterownik.getDecyzjaSterujaca(getOcenaSytuacji(), getStan(), getIloscAkcji());
			setObslugiwanaKolejka(akcja);
		}
		
		for (int i = 0; i < kolejki.size(); i++) {
			getKolejka(i).wykonajCyklSymulacji();
			
			//TODO tutaj kod dynamicznej zmiany wagi - przeniesc go!
			if (getObslugiwanaKolejka() == i) {
				getKolejka(i).setWaga(0.3f);
			} else {
				getKolejka(i).setWaga(1f);
			}
		}

		if (obslugiwanaKolejka != BRAK_OBSLUGI) {
			if (cyklNastawy <= 0) {
				getKolejka(obslugiwanaKolejka).przetwarzajZadania();
			} else {
				cyklNastawy--;
			}
		}
	}
	
	public void ustawStanPoczatkowy() {
		cyklNastawy = 0;
		obslugiwanaKolejka = 0;
		for (int i = 0; i < getIloscKolejek(); i++) {
			kolejki.get(i).ustawStanPoczatkowy();
		}
	}
}

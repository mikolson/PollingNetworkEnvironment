/*
 * Kolejka.java
 * 
 * Created on 2007-10-19, 01:17:47
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.wroc.pwr.iis.polling.model.object.polling;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pl.wroc.pwr.iis.polling.model.object.IElementSymylacji;
import pl.wroc.pwr.iis.rozklady.IRozkladPrawdopodobienstwa;

/**
 * 
 * @author Misiek
 */
public class Kolejka extends Wlasciwosci implements IElementSymylacji {

	protected ArrayList<Zgloszenie> zgloszenia = new ArrayList<Zgloszenie>();
	
	protected Serwer serwer;
	protected int straconych;
	

    private Random random = new Random();
	
	public Kolejka(Serwer serwer) {
		this.serwer = serwer;
		setWaga(BRAK_WAGI);
	}

	/**
	 * Dodaje do kolejki nowe zgloszenie - ustawiaj�c jego czas oczekiwania na 0
	 * 
	 * @param iloscZgloszen
	 */
	public synchronized void dodajDoKolejki(int iloscZgloszen) {
		for (int i = 0; i < iloscZgloszen; i++) {
			//dodajDoKolejki(new Zgloszenie());
			dodajDoKolejki(null);
		}
	}
	
	/**
	 * Dodanie do kolejki zg�oszenia kt�re pochodzi z innego stanowiska
	 * @param zgloszenie
	 */
	public synchronized void dodajDoKolejki(Zgloszenie zgloszenie) {
		//Dodanie zgloszenia moze nastapic tylko 
		//jeżeli w kolejce znajduje się jeszcze miejsce
		if (getIloscZgloszen() < getOgraniczenieLiczbyZgloszen()) {
			if (zgloszenie == null) {
				zgloszenie = new Zgloszenie();
			} else {
				zgloszenie.usunCzasOczekiwania();
			}
			//this.zgloszenia.addLast(zgloszenie);
			this.zgloszenia.add(0, zgloszenie);
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
//			return zgloszenia.removeFirst();
			return zgloszenia.remove(zgloszenia.size()-1);
		} else {
			System.out.println("Kolejka.pobierzZgloszenie(): probowano pobrac zgloszenia z pustej kolejki");
			return null;
		}
	}

	public int getIloscZgloszen() {
		return zgloszenia.size();
	}

	public synchronized void wykonajCyklSymulacji() {
		// Uwzglednienie tylko zgloszen wewnetrznych
		int przybyc = getIloscPrzybyc();
		
		// Zwiekszenie czasu oczekiwania zgloszen
		zwiekszCzasOczekiwania();

		// Dodanie nowych zgloszen do kolejki
		dodajDoKolejki(przybyc);
	}
	
	public synchronized void przetwarzajZadania() {
		int obslug = getIloscObsluzonych();

		// Usunięcie starych zgloszeń
		wstawDoKolejnejKolejki(obslug);
	}

	/**
	 * Zwiększa czas oczekiwania wszystkich zgloszen jakie znajduja sie na
	 * serwerze
	 */
	private void zwiekszCzasOczekiwania() {
		for (int i = 0; i < this.zgloszenia.size(); i++) {
			zgloszenia.get(i).zwiekszCzasOczekiwania();
		}
	}

	/**
	 * @return Zwraca czas oczekiwania pierwszego zgłoszenia w kolejce
	 */
	public int getCzasOczekiwania() {
		//return zgloszenia.getFirst().getCzasOczekiwania();
		return getCzasOczekiwania(0);
	}

	/**
	 * @return Zwraca średni czas oczekiwania zgłoszeń w kolejce
	 */
	public float getSredniCzasOczekiwania() {
		float result = 0;
		
		if (getIloscZgloszen() > 0) {
			for (int i = 0; i < getIloscZgloszen(); i++) {
				result += getCzasOczekiwania(i);
			} 
			result = result/(float)getIloscZgloszen();
		}
		
		return result;
	}
	
	/**
	 * Zwraca czas oczekiwania ostatniego pakietu w kolejce
	 * @param i Numer kolejki
	 * @return
	 */
	public int getCzasOczekiwania(int i) {
		int result = 0;
		if (zgloszenia.size() > i) {
			result = zgloszenia.get(i).getCzasOczekiwania();
		}
		return result;
	}
	
	protected int getIloscPrzybyc() {
		IRozkladPrawdopodobienstwa rKolejki = getRozkladCzasuPrzybyc();
		IRozkladPrawdopodobienstwa rSerwera = this.serwer.getRozkladCzasuPrzybyc();

		return getIloscWylosowanych(rKolejki, rSerwera);
	}

	protected int getIloscObsluzonych() {
		IRozkladPrawdopodobienstwa rKolejki = getRozkladCzasuObslugi();
		IRozkladPrawdopodobienstwa rSerwera = this.serwer.getRozkladCzasuObslugi();

		return getIloscWylosowanych(rKolejki, rSerwera);
	}

	public Serwer getSerwer() {
		return serwer;
	}

	protected void wstawDoKolejnejKolejki(int ilosc) {
		List<Polaczenie> polaczenia = getPolaczeniaWychodzace();
		if (polaczenia == null) {
			polaczenia = getSerwer().getPolaczeniaWychodzace();
		}
		
		// Je�eli brak jest ustawionych po��cze� wychodz�cych to znaczy, �e 
		// nie interesuje nas gdzie znajd� si� docelowo ��dania i po prostu
		// Je usuwamy
		
		int obslug = Math.min(getIloscZgloszen(), ilosc);
		
		if (polaczenia == null) {
			for (int i = 0; i < obslug; i++) {
				pobierzZgloszenie();
			}
		} else {
			for (int i = 0; i < ilosc; i++) {
				Polaczenie polaczenie = losujPolaczenie(polaczenia);
				Zgloszenie zgloszenie = pobierzZgloszenie();
				polaczenie.getKolejkaDocelowa().dodajDoKolejki(zgloszenie);
			}
		}
	}

	/**
	 * Losuje polaczenie wychodz�ce jakim zostanie wys�ane zadanie
	 * 
	 * @param polaczenia Lista polacze� wychodz�cych
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
	public int getMaxCzasOczekiwania() {
		int czasOczekiwaniaKolejki = super.getMaxCzasOczekiwania();
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
	
	

}

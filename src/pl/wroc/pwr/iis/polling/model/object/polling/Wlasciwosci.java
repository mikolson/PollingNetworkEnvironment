/*
 * Wlasciwosci.java
 * 
 * Created on 2007-10-25, 15:43:49
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.wroc.pwr.iis.polling.model.object.polling;

import java.util.List;

import pl.wroc.pwr.iis.rozklady.IRozkladCiagly;
import pl.wroc.pwr.iis.rozklady.IRozkladDyskretny;

/**
 * @author Michał Stanek <michal.stanek@pwr.wroc.pl>
 */
public class Wlasciwosci {
	public static final int BRAK_OGRANICZENIA_ZGLOSZEN = -1;
	public static final int BRAK_OGRANICZENIA_CZASOWEGO = -1;
	public static final int BRAK_WAGI = -1;
	
    protected IRozkladDyskretny rozkladCzasuPrzybyc;
    protected IRozkladCiagly rozkladCzasuObslugi;
    protected IRozkladCiagly rozkladCzasuNastawy;
    
    protected int maxZgloszen = BRAK_OGRANICZENIA_ZGLOSZEN;
    protected float waga = 1f;
    protected String nazwa;
    
    protected List<Polaczenie> polaczeniaWychodzace = null;
    protected double maxCzasOczekiwania = BRAK_OGRANICZENIA_CZASOWEGO; 

    public Wlasciwosci() {
    }

    public Wlasciwosci(String nazwa) {
        this.nazwa = nazwa;
    }

    public String getNazwa() {
		return nazwa;
	}

	public void setNazwa(String nazwa) {
		this.nazwa = nazwa;
	}

	public IRozkladCiagly getRozkladCzasuObslugi() {
        return rozkladCzasuObslugi;
    }

    public void setRozkladCzasuObslugi(IRozkladCiagly rozkladCzasuObslugi) {
        this.rozkladCzasuObslugi = rozkladCzasuObslugi;
    }

    public IRozkladDyskretny getRozkladCzasuPrzybyc() {
        return rozkladCzasuPrzybyc;
    }

    public void setRozkladIlosciPrzybyc(IRozkladDyskretny rozkladCzasuPrzybyc) {
        this.rozkladCzasuPrzybyc = rozkladCzasuPrzybyc;
    }
    
    public IRozkladCiagly getRozkladCzasuNastawy() {
		return rozkladCzasuNastawy;
	}

	public void setRozkladCzasuNastawy(IRozkladCiagly rozkladCzasuNastawy) {
		this.rozkladCzasuNastawy = rozkladCzasuNastawy;
	}

	public int getMaxZgloszen() {
        return maxZgloszen;
    }

    public void setMaxZgloszen(int maxZgloszen) {
        this.maxZgloszen = maxZgloszen;
    }

    /**
     * Zwraca maksymalny czas oczekiwania w kolejce. Czas ten ustawiony 
     * jest jako parametr konfiguracyjny. Jeżeli kolejka nie ma ustawionego
     * max. czasu oczekiwania pobierany jest domyślny czas ustawiony dla
     * całego serwera. Możliwe jest również ustawienie BRAKU_OGRANICZENIA_CZASOWEGO
     *  
     * @return Czas oczekiwania w kolejce
     */
    public double getMaxCzasOczekiwania() {
        return maxCzasOczekiwania;
    }

    public void setMaxCzasOczekiwania(int maxCzasOczekiwania) {
        this.maxCzasOczekiwania = maxCzasOczekiwania;
    }

    public List<Polaczenie> getPolaczeniaWychodzace() {
        return polaczeniaWychodzace;
    }

    public void setPolaczeniaWychodzace(List<Polaczenie> polaczeniaWychodzace) {
        this.polaczeniaWychodzace = polaczeniaWychodzace;
    }

	public float getWaga() {
		return waga;
	}

	public void setWaga(float waga) {
		this.waga = waga;
	}
	
	/**
	 * Losuje wg. aktualnie ustawionego w kolejce rozkładu prawdopodobieństwa 
	 * liczbę przybyć. Jeżeli kolejka nie ma ustawionego rozkładu czasu 
	 * przybyć brany jest rozkład używany dla całego serwera. 
	 * @param kolejki Rozkład czasu przybyć kolejki
	 * @param serwera Rozkład czasu przybyć serwera
	 * @return Ilość nowych zgłoszeń
	 */
	protected int losuj(IRozkladDyskretny kolejki, IRozkladDyskretny serwera, double kwantCzasu) {
		IRozkladDyskretny rKolejki = kolejki;
		IRozkladDyskretny rSerwera = serwera;

		int result;

		if (rKolejki == null && rSerwera != null) {
			rKolejki = rSerwera;
		}
		
		if (rKolejki == null){
			assert true;
		}

		
		result = rKolejki.losuj(kwantCzasu);
		return result;
	}
	
	
	protected double losuj(IRozkladCiagly kolejki, IRozkladCiagly serwera) {
		IRozkladCiagly rKolejki = kolejki;
		IRozkladCiagly rSerwera = serwera;
		double result;

		if (rKolejki == null && rSerwera != null) {
			rKolejki = rSerwera;
		}
		
		if (rKolejki == null){
			assert true;
		}
		
		
		result = rKolejki.losuj();
		return result;
	}
}


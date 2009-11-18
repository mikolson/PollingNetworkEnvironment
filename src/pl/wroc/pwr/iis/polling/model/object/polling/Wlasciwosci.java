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

import pl.wroc.pwr.iis.rozklady.IRozkladPrawdopodobienstwa;


/**
 *
 * @author Misiek
 */
public class Wlasciwosci {
	public static final int BRAK_OGRANICZENIA_ZGLOSZEN = -1;
	public static final int BRAK_OGRANICZENIA_CZASOWEGO = -1;
	
	public static final int BRAK_WAGI = -1;
	
    protected IRozkladPrawdopodobienstwa rozkladCzasuPrzybyc;
    protected IRozkladPrawdopodobienstwa rozkladCzasuObslugi;
    protected IRozkladPrawdopodobienstwa rozkladCzasuNastawy;
    
    protected int maxZgloszen = BRAK_OGRANICZENIA_ZGLOSZEN;
    
    protected float waga = 1f;
    protected String nazwa;
    
    protected List<Polaczenie> polaczeniaWychodzace = null;

    // Ograniczenia
    protected int maxCzasOczekiwania = BRAK_OGRANICZENIA_CZASOWEGO; //

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

	public IRozkladPrawdopodobienstwa getRozkladCzasuObslugi() {
        return rozkladCzasuObslugi;
    }

    public void setRozkladIlosciObslug(IRozkladPrawdopodobienstwa rozkladCzasuObslugi) {
        this.rozkladCzasuObslugi = rozkladCzasuObslugi;
    }

    public IRozkladPrawdopodobienstwa getRozkladCzasuPrzybyc() {
        return rozkladCzasuPrzybyc;
    }

    public void setRozkladIlosciPrzybyc(IRozkladPrawdopodobienstwa rozkladCzasuPrzybyc) {
        this.rozkladCzasuPrzybyc = rozkladCzasuPrzybyc;
    }
    
    public IRozkladPrawdopodobienstwa getRozkladCzasuNastawy() {
		return rozkladCzasuNastawy;
	}

	public void setRozkladCzasuNastawy(IRozkladPrawdopodobienstwa rozkladCzasuNastawy) {
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
    public int getMaxCzasOczekiwania() {
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
	protected int getIloscWylosowanych(IRozkladPrawdopodobienstwa kolejki, IRozkladPrawdopodobienstwa serwera) {
		IRozkladPrawdopodobienstwa rKolejki = kolejki;
		IRozkladPrawdopodobienstwa rSerwera = serwera;

		int result;

		if (rKolejki == null && rSerwera != null) {
			rKolejki = rSerwera;
		}
		
		if (rKolejki == null){
			assert true;
		}

		result = (int) rKolejki.losuj();

		return result;
	}
}


package pl.wroc.pwr.iis.polling.model.object.polling;

/**
 * @author Michał Stanek <michal.stanek@pwr.wroc.pl>
 */
public class Zgloszenie {
    protected double czasPrzybycia;
    protected double czasObslugi;
    protected double doUkonczenia;
    protected double czasUkonczenia = -1;

    public Zgloszenie(double czasPrzybycia, double czasObslugi) {
    	this.czasPrzybycia = czasPrzybycia; 
    	this.czasObslugi = czasObslugi; 
    	this.doUkonczenia = czasObslugi;
//    	System.out.println("Tworze zgłoszenie: " + doUkonczenia + " czas: " + czasPrzybycia);
    }
    
    /**
     * Ustawia nowy czasy przybycia zadania do kolejki
     * @param aktualnyCzas
     */
    public void ustawCzasPrzybycia(double aktualnyCzas){ 
    	czasPrzybycia = aktualnyCzas;
    }

    /**
     * Ustawia nowy czas obsługi zadania
     * @param czasObslugi
     */
    public void setCzasObslugi(double czasObslugi) {
    	this.czasObslugi = czasObslugi;
    }
    
    public double getCzasObslugi() {
    	return this.czasObslugi;
    }

    /**
     * @return Czas potrzebny do zakończenia zadania
     */
    public double getCzasDoUkonczenia(){
    	return this.doUkonczenia;
    }
    
    /**
     * Ustawia nowy czas do ukończenia zadania
     * @param nowyCzas
     */
    public void setCzasDoUkonczenia(double nowyCzas) {
    	this.doUkonczenia = nowyCzas;
    }
    
    /**
     * @param aktualnyCzas 
     * @return Zwraca czas oczekiwania od momentu przybycia do aktualnego momentu
     */
    public double getCzasOczekiwania(double aktualnyCzas) {
        return aktualnyCzas - this.czasPrzybycia;
    }

	public double getCzasOczekiwania() {
		double result = 0;
		
		if (czasUkonczenia>0) {
			result = czasUkonczenia - czasPrzybycia;
		}
		
		return result;
	}
}

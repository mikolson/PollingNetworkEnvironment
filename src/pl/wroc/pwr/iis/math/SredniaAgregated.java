package pl.wroc.pwr.iis.math;

/**
 * Klasa która może być agregowana w innej klasie i obliczac srednia arytmetyczna 
 * z otrzymywanych wynikow
 */
public class SredniaAgregated {
	private int iteracja = 0;
	private double srednia = 0;
	
	public void dodajElement(double nowaWartosc) {
		srednia = Srednia.sredniaArytmetyczna(srednia, nowaWartosc,iteracja);
		iteracja++;
	}

	public int getIteracja() {
		return iteracja;
	}

	public double getSrednia() {
		return srednia;
	}
}

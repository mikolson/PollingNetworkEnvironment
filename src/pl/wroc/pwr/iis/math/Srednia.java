package pl.wroc.pwr.iis.math;

public class Srednia {
	public static double sredniaArytmetyczna(double wartoscSredniej, double nowyElement, int numerElementu) {
		return wartoscSredniej + (1/(double)(numerElementu + 1))*(nowyElement - wartoscSredniej);
	}
	
	public static double sredniaArytmetyczna(double[] wartosci) {
		return sredniaArytmetyczna(wartosci,0, wartosci.length);
	}
	
	public static double sredniaArytmetyczna(double[] wartosci, int elementStart, int elementKoniec) {
		double suma = 0;
		int koniec = Math.min(wartosci.length, elementKoniec);
		for (int i = Math.min(0, elementStart); i < koniec; i++) {
			suma += wartosci[i];
		}
		return suma/(elementKoniec-elementStart);
	}
}

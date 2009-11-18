package pl.wroc.pwr.iis.math;

public class OdchylenieStandardowe {
	public static double oblicz(double[] wartosci) {
		return oblicz(wartosci, 0, wartosci.length);
	}
	public static double oblicz(double[] wartosci, int start, int koniec) {
		double srednia = Srednia.sredniaArytmetyczna(wartosci, start, koniec);
		
		double sumaRoznic = 0;
		int koniecI = Math.min(koniec, wartosci.length);
		for (int i = Math.min(0, koniec); i < koniecI; i++) {
			sumaRoznic = Math.pow(wartosci[i] - srednia, 2); 
		}
		return sumaRoznic/(koniec-start);
	}
}

package pl.wroc.pwr.iis.polling.model.sterowanie.strategie;

import pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaWartosci.FunkcjaWartosciAkcji;
import junit.framework.TestCase;

public class StrategiaSoftMaxTest extends TestCase {
	private static final float EPSILON = 0.1f;

	private static final int ILOSC_AKCJI = 2;
	
	StrategiaSoftMax strategia;
	FunkcjaWartosciAkcji funkcjaWartosciAkcji;
	
	protected void setUp() throws Exception {
		super.setUp();
		int[] maxStanow = new int[]{2, 2};
		strategia = new StrategiaSoftMax(1000, 1, 1000, maxStanow, ILOSC_AKCJI);
		funkcjaWartosciAkcji = strategia.getFunkcjaWartosciAkcji();
		funkcjaWartosciAkcji.wyczysc();
	}

	public void testGetAkcja() {
		int[] stan = new int[]{0,0};
		int akcja = 0;
		int numerStanu = strategia.getNumerStanu(stan);

		float wartosc = 20;
		
		funkcjaWartosciAkcji.setWartosc(numerStanu, akcja, wartosc );
		
		int[] wybory = new int[ILOSC_AKCJI];
		int iteracji = 1000000;
		for (int i = 0; i < iteracji ; i++) {
			wybory[strategia.getAkcja(stan, ILOSC_AKCJI)]++;
		}
		
		int tolerancja = 1000;
		System.out.println(wybory[0]);
		System.out.println(wybory[1]);
		
		System.out.println(0.95*iteracji - tolerancja);
		
		assertTrue(wybory[0] > 0.95*iteracji - tolerancja);
		assertTrue(wybory[0] < 0.95*iteracji + tolerancja);
		
		assertTrue(wybory[1] > 0.05*iteracji - tolerancja);
		assertTrue(wybory[1] < 0.05*iteracji + tolerancja);
		
//		int a = strategia.getAkcja(stan, ILOSC_AKCJI);
	}

	public void testAktualizuj() {
	}

	public void testGetFunkcjaWartosciAkcji() {
	}

	public void testStrategiaEZachlannaIntArrayInt() {
	}

	public void testStrategiaEZachlannaFloatIntArrayInt() {
	}
}

package pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaWartosci;

import junit.framework.TestCase;

public class FunkcjaWartosciAkcjiTest extends TestCase {
	private FunkcjaWartosciAkcji f1;

	public void setUp() throws Exception {
		f1 = new FunkcjaWartosciAkcji(2, 2);
	}

	public void testPoprawWartoscIntIntFloat() {
		float srednia = f1.poprawWartosc(0, 0, 1);
		assertEquals(1, srednia);
	}

	
//	public void testZainicjujLosowo() {
//	}
//
//	public void testWyczysc() {
//		
//	}
//
//	public void testFunkcjaWartosciAkcji() {
//	}
//
//	public void testGetMinAkcja() {
//	}
//
//	public void testGetMaxAkcja() {
//	}
//
//
//	public void testPoprawWartoscIntIntFloatFloat() {
//		
//	}
//
//	public void testGetWartosc() {
//	}
//
//	public void testSetWartosc() {
//	}
//
//	public void testAddWartosc() {
//	}
//
//	public void testGetIloscAkcji() {
//	}
//
//	public void testToString() {
//	}
}

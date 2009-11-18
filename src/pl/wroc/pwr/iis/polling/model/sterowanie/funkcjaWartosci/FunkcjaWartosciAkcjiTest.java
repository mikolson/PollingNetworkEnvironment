package pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaWartosci;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class FunkcjaWartosciAkcjiTest {
	
	FunkcjaWartosciAkcji fW;
	
	@Before
	public void setUp() throws Exception {
		fW = new FunkcjaWartosciAkcji(2,2);
	}

	@Test
	public void testPoprawWartoscIntIntFloat() {
		float srednia = fW.poprawWartosc(0, 0, 1);
		assertEquals(srednia, 1);
		srednia = fW.poprawWartosc(0, 0, 3);
		assertEquals(srednia, 2);
		srednia = fW.poprawWartosc(0, 0, 5);
		assertEquals(srednia, 3);
	}
	
	public void testAddWartosc() throws Exception {
		fW.addWartosc(0, 0, 1);
		fW.addWartosc(0, 0, 1);
		float s = fW.addWartosc(0, 0, 1);
		assertEquals(s, 3);
		s = fW.addWartosc(0, 0, 30);
		assertEquals(s, 33);
	}
}

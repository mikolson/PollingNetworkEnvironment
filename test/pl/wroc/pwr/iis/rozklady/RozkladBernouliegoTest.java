package pl.wroc.pwr.iis.rozklady;

import junit.framework.TestCase;

public class RozkladBernouliegoTest extends TestCase {

	private static final int LOSOWAN = 10000000;

	public void testLosuj() {
		RozkladBernouliego rozkladBernouliego = new RozkladBernouliego(100);
		int suma = 0;
		for (int i = 0; i < LOSOWAN; i++) {
			suma += rozkladBernouliego.losuj();
		}
		
		assertEquals(suma, LOSOWAN);
		
		
		rozkladBernouliego = new RozkladBernouliego(0);
		suma = 0;
		for (int i = 0; i < LOSOWAN; i++) {
			suma += rozkladBernouliego.losuj();
		}
		assertEquals(suma, 0);
		
		
		rozkladBernouliego = new RozkladBernouliego(50);
		suma = 0;
		for (int i = 0; i < LOSOWAN; i++) {
			suma += rozkladBernouliego.losuj();
		}
		
		int min = (int) (LOSOWAN*.5 - LOSOWAN*0.01);
		int max = (int) (LOSOWAN*.5 + LOSOWAN*0.01);
		
		assertTrue(suma > min);
		assertTrue(suma < max);
		
		
		rozkladBernouliego = new RozkladBernouliego(30);
		suma = 0;
		for (int i = 0; i < LOSOWAN; i++) {
			suma += rozkladBernouliego.losuj();
		}
		
		min = (int) (LOSOWAN*.3 - LOSOWAN*0.01);
		max = (int) (LOSOWAN*.3 + LOSOWAN*0.01);
	
		assertTrue(suma > min);
		assertTrue(suma < max);
	}
	
	public void testStuProcentowePradopodobienstwo() throws Exception {
		RozkladBernouliego rozkladBernouliego = new RozkladBernouliego(100);
		int suma = 0;
		for (int i = 0; i < LOSOWAN; i++) {
			suma += rozkladBernouliego.losuj();
		}
		assertEquals(LOSOWAN, suma);
	}

}

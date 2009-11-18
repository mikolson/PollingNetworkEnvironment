package pl.wroc.pwr.iis.rozklady;

import junit.framework.TestCase;

public class LosujTest extends TestCase {
	double[] elementy;
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testLosujElement() {
		int wybrany;
		
		elementy = new double[]{0,0,1};
		wybrany = Losuj.losujElement(elementy);
		assertEquals(2, wybrany);
		
		elementy = new double[]{0,1,0};
		wybrany = Losuj.losujElement(elementy);
		assertEquals(1, wybrany);
		
		elementy = new double[]{1,0,0};
		wybrany = Losuj.losujElement(elementy);
		assertEquals(0, wybrany);
		
		elementy = new double[]{0.9f,.1f,0};
		
		int iteracji = 1000000;
		int toleracja =  700; 
		int akcja0 = 0;
		int akcja1 = 0;
		
		for (int i = 0; i < iteracji ; i++) {
			wybrany = Losuj.losujElement(elementy);
			if (wybrany == 0) {
				akcja0++;
			} else if (wybrany == 1){
				akcja1++;
			} else {
				fail("nie ma takiej możliwości żeby wybrany był aktualny obiekt");
			}
		}
		System.out.println("LosujTest.testLosujElement(): " + akcja0 + " : oczekiwano: " + iteracji * elementy[0]);
		System.out.println("LosujTest.testLosujElement(): " + akcja1 + " : oczekiwano: " + iteracji * elementy[1]);
		
		
		assertTrue( akcja0 > (iteracji * elementy[0]) - toleracja);
		assertTrue( akcja0 < iteracji * elementy[0] + toleracja);
		
		assertTrue( akcja1 > iteracji * elementy[1] - toleracja);
		assertTrue( akcja1 < iteracji * elementy[1] + toleracja);
		
		
		
		elementy = new double[]{0.4f,.3f,0.3f};
		iteracji *= 10; 
		toleracja = 5000;
		akcja0 = 0;
		akcja1 = 0;
		int akcja2 = 0;
		
		for (int i = 0; i < iteracji ; i++) {
			wybrany = Losuj.losujElement(elementy);
			if (wybrany == 0) {
				akcja0++;
			} else if (wybrany == 1){
				akcja1++;
			} else if (wybrany == 2){
				akcja2++;
			} else {
				fail("nie ma takiej możliwości żeby wybrany był aktualny obiekt");
			}
		}
		System.out.println("LosujTest.testLosujElement(): " + akcja0 +" : oczekiwano: " + (iteracji * elementy[0]));
		System.out.println("LosujTest.testLosujElement(): " + akcja1 +" : oczekiwano: " + (iteracji * elementy[1]));
		System.out.println("LosujTest.testLosujElement(): " + akcja2 +" : oczekiwano: " + (iteracji * elementy[2]));
		
		assertTrue( akcja0 > iteracji * elementy[0] - toleracja);
		assertTrue( akcja0 < iteracji * elementy[0] + toleracja);
		
		assertTrue( akcja1 > iteracji * elementy[1] - toleracja);
		assertTrue( akcja1 < iteracji * elementy[1] + toleracja);
		
		assertTrue( akcja2 > iteracji * elementy[2] - toleracja);
		assertTrue( akcja2 < iteracji * elementy[2] + toleracja);
	}

}

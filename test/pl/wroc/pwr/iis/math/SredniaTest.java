package pl.wroc.pwr.iis.math;

import junit.framework.TestCase;

public class SredniaTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testWyliczSrednia() {
		double[] wartosci = new double[] {1,2,3,4,5,6,7,8,9,10};
		double srednia = 0;
		for (int i = 0; i < wartosci.length; i++) {
			srednia = Srednia.sredniaArytmetyczna(srednia, wartosci[i], i);
		}
		assertEquals(Srednia.sredniaArytmetyczna(wartosci), srednia);
	}
	

}

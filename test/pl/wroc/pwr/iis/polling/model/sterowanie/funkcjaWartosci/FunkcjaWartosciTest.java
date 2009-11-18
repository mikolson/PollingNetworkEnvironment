package pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaWartosci;

import junit.framework.TestCase;
import pl.wroc.pwr.iis.polling.model.object.polling.Serwer;

public class FunkcjaWartosciTest extends TestCase {
	Serwer serwer;
	FunkcjaWartosciAkcji funkcjaWartosci;
	
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		funkcjaWartosci = new FunkcjaWartosciAkcji(2,2);
	}
		
	public void testname() throws Exception {
		float s = funkcjaWartosci.poprawWartosc(0,0,1);
		System.out.println(s);
	}
}

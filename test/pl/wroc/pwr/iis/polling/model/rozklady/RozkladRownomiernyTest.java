package pl.wroc.pwr.iis.polling.model.rozklady;

import pl.wroc.pwr.iis.rozklady.RozkladJednostajny;
import junit.framework.TestCase;

public class RozkladRownomiernyTest extends TestCase {
	public void testLosuj() throws Exception {
		RozkladJednostajny r;
		int iteracji = 10000;
		int testow = 150;
		
		for (int i = 0; i < testow; i++) {
			int srednia = i;
			r= new RozkladJednostajny(srednia);
			long suma = 0;
			for (int j = 0; j < iteracji; j++) {
				suma += r.losuj(); 
			}
			double wynik = suma / (double)iteracji;
			
			System.out.println(wynik);
			if (!(wynik == srednia)) {
				fail();
			}
		}
		
		
	}
}

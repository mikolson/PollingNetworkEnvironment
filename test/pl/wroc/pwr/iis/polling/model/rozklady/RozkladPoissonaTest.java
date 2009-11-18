package pl.wroc.pwr.iis.polling.model.rozklady;

import pl.wroc.pwr.iis.rozklady.RozkladPoissona;
import junit.framework.TestCase;

public class RozkladPoissonaTest extends TestCase {
	public void testLosuj() throws Exception {
		RozkladPoissona r;
		int iteracji = 1000;
		
		
		for (int i = 0; i < 150; i++) {
			int srednia = i;
			r= new RozkladPoissona(srednia);
			long suma = 0;
			for (int j = 0; j < iteracji; j++) {
				suma += r.losuj(); 
			}
			double wynik = suma / (double)iteracji;
			
			System.out.println(wynik);
			double tolerancja = 1;
			if (!(wynik > (srednia - tolerancja)) && (wynik < (srednia + tolerancja))) {
				fail();
			}
		}
		
		
	}
}

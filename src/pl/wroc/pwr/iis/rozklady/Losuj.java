package pl.wroc.pwr.iis.rozklady;

import pl.wroc.pwr.iis.rozklady.random.RandomGenerator;

public class Losuj {
	public static final float EPSILON = 0.00001f;
	
	/**
	 * Losuje elementy, uwzgledniajac prawdopodobienstwo kazdego z elementów
	 * które podane jest w tablicy
	 * @param elementy
	 * @return
	 */
	public static int losujElement(double[] elementy){
		int result = 0;
		float suma = 0;
		
		// TODO tutaj mozna te cześć przeżucić niżej, zwiększając przez to 
		// ogólną efektyność procesu
		for (int i = 0; i < elementy.length; i++) {
			suma += elementy[i];
		}
		
		if (1-EPSILON < suma && suma < 1+EPSILON) {
			float los = (float) RandomGenerator.getDefault().nextDouble();
			float s = 0;
			for (int i = 0; i < elementy.length; i++) {
				s += elementy[i];
				
				if (s > los) {
					result = Math.max(0,i);
					break;
				}
			}
		} else {
			System.err.println("Losowanie nie moze sie odbyc powniewaz suma prawdopodobieństwa nie jest rowna 1: " + suma);
			for (double d : elementy) {
				System.out.println(d + ", ");
			}
		}
		
		return result;
	}
}

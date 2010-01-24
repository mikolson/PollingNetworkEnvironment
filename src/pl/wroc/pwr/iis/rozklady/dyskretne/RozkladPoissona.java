/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.wroc.pwr.iis.rozklady.dyskretne;

import java.util.Random;

import pl.wroc.pwr.iis.rozklady.IRozkladDyskretny;
import pl.wroc.pwr.iis.rozklady.random.RandomGenerator;

/**
 * @author Misiek
 */
public class RozkladPoissona extends Random implements IRozkladDyskretny {
	private static final long serialVersionUID = 6161475517205700044L;
	
	private double lambda;

    public RozkladPoissona(double parametr) {
        setLambda(parametr);
    }
    
    public void setLambda(double lambda) {
        this.lambda = lambda;
    }

    public int losuj() {
    	return losuj(1);
    }

	@Override
	public int losuj(double kwantCzasu) {
	    double elambda = Math.exp(-1 * lambda * kwantCzasu);
        double product = 1;
        int count = 0;
        int result = 0;
        while (product >= elambda) {
            product *= RandomGenerator.getDefault().nextDouble();
            result = count;
            count++; // keep result one behind
        }
        return result;
	}
	
	public static void main(String[] args) {
		RozkladPoissona p = new RozkladPoissona(0.15);
		int ilosc = 1000000;
		int suma = 0;
		double dzielnik = 0.1;
		for (int i = 0; i < ilosc; i++) {
		   for (int j = 0; j < 1/dzielnik; j++) {
			   suma += p.losuj(dzielnik);
		   }
		}
		System.out.println((double)suma/ilosc);
	}
}

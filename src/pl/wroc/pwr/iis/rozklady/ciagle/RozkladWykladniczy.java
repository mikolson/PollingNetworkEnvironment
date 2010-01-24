/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.wroc.pwr.iis.rozklady.ciagle;

import java.util.Random;

import pl.wroc.pwr.iis.rozklady.IRozkladCiagly;
import pl.wroc.pwr.iis.rozklady.random.RandomGenerator;
import umontreal.iro.lecuyer.randvar.ExponentialGen;
import umontreal.iro.lecuyer.randvar.RandomVariateGen;
import umontreal.iro.lecuyer.rng.MRG31k3p;

/**
 * Implementacja rozkładu wykładniczego
 * @author Michał Stanek
 */
public class RozkladWykladniczy extends Random implements IRozkladCiagly {
	private static final long serialVersionUID = 6161475517205700044L;
	
//	static RandomStream stream = new MRG31k3p();
	static MRG31k3p stream = new MRG31k3p();
	static RandomVariateGen gen1;

	public RozkladWykladniczy(double srednia) {
        setLambda(1/srednia);
        
        int[] seed = new int[6];
        for (int i = 0; i < seed.length; i++) {
			seed[i] = RandomGenerator.getDefault().nextInt(Integer.MAX_VALUE);
		}
        stream.setSeed(seed);
    }
    
    public void setLambda(double lambda) {
		gen1 = new ExponentialGen(stream, lambda);
    }
    
//    public double genExponential(double l) {
//    	return -lambda*Math.log(Math.random());
//        }

    public double losuj() {
    	return gen1.nextDouble();
    }
    
    public static void main(String[] args) {
		RozkladWykladniczy p = new RozkladWykladniczy(1);
		int ilosc = 10000000;
		double suma = 0;
		for (int i = 0; i < ilosc; i++) {
			   suma += p.losuj();
		}
		System.out.println("Średnia:");
		System.out.println((double)suma/ilosc);
	}
    
    @Override
    public String toString() {
    	return "Rozkład wykładniczy: " + gen1.getDistribution().getMean();
    }
}

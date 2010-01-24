/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.wroc.pwr.iis.rozklady.dyskretne;

import pl.wroc.pwr.iis.rozklady.IRozkladDyskretny;
import pl.wroc.pwr.iis.rozklady.random.RandomGenerator;


/**
 * @author Misiek
 */
public class RozkladRownomiernyDyskretny implements IRozkladDyskretny {

	private int min;
    private int max;

    public RozkladRownomiernyDyskretny() {
    	this(0,100);
    }

    public RozkladRownomiernyDyskretny(int max) {
    	this(0,max);
    }
    
    public RozkladRownomiernyDyskretny(int min, int max) {
    	this.min = min;
    	this.max = max;
    }

    public int losuj() {
        return (int) (min + RandomGenerator.getDefault().nextInt(max - min));
    }
    
    public static void main(String[] args) {
		RozkladRownomiernyDyskretny r = new RozkladRownomiernyDyskretny(0,12);
		for (int i = 0; i < 100; i++) {
				System.out.println("RozkladRownomierny.main()" + r.losuj());
		}
	}

	@Override
	public int losuj(double kwantCzasu) {
		return (int) (min + RandomGenerator.getDefault().nextInt(max - min));
	}
}

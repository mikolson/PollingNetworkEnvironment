/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.wroc.pwr.iis.rozklady;

import pl.wroc.pwr.iis.rozklady.random.RandomGenerator;


/**
 * @author Misiek
 */
public class RozkladRownomierny implements IRozkladPrawdopodobienstwa {

	private int min;
    private int max;

    public RozkladRownomierny() {
    	this(0,100);
    }

    public RozkladRownomierny(int max) {
    	this(0,max);
    }
    
    public RozkladRownomierny(int min, int max) {
    	this.min = min;
    	this.max = max;
    }

    public int losuj() {
        return (int) (min + RandomGenerator.getDefault().nextInt(max - min));
    }
    
    public static void main(String[] args) {
		RozkladRownomierny r = new RozkladRownomierny(0,12);
		for (int i = 0; i < 100; i++) {
				System.out.println("RozkladRownomierny.main()" + r.losuj());
		}
	}
}

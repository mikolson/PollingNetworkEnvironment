/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.wroc.pwr.iis.rozklady;

import java.util.Random;


/**
 * @author Misiek
 */
public class RozkladPoissona extends Random implements IRozkladPrawdopodobienstwa {
	private static final long serialVersionUID = 6161475517205700044L;
	
	private int lambda;

    public RozkladPoissona(int parametr) {
        setLambda(parametr);
    }
    
    public void setLambda(int lambda) {
        this.lambda = lambda;
    }

    public int losuj() {
        double elambda = Math.exp(-1 * lambda);
        double product = 1;
        int count = 0;
        int result = 0;
        while (product >= elambda) {
            product *= nextDouble();
            result = count;
            count++; // keep result one behind
        }
        return result;
    }
}

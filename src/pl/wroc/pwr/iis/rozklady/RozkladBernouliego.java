/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.wroc.pwr.iis.rozklady;

import java.util.Random;

import pl.wroc.pwr.iis.rozklady.random.RandomGenerator;


/**
 * Rozkład działa w następujący sposób :
 * 	przy inicjalizacji podaje sie prawdopodobienstwo wystapienia
 *  zdarzenia X
 *  
 *  nastepnie przy kazdym losowaniu dokladnie z podanym 
 *  prawdopodobienstwem losowane jest wystapienie jednego 
 *  zdarzenia X
 *   
 * @author Misiek
 */
public class RozkladBernouliego extends Random implements IRozkladPrawdopodobienstwa {
	private static final long serialVersionUID = 6161475517205700044L;
	
	private final int prawdopodobienstwo;

    public RozkladBernouliego(int prawdopodobienstwo) {
    	this.prawdopodobienstwo = prawdopodobienstwo;
    }

    public int losuj() {
    	return (int) (RandomGenerator.getDefault().nextInt(100)) < prawdopodobienstwo ? 1:0;
    }
}

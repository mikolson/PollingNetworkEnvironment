/*
 * RozkladPrawdopodobienstwa.java
 * 
 * Created on 2007-10-19, 01:20:21
 */

package pl.wroc.pwr.iis.rozklady;

public interface IRozkladDyskretny {
    int losuj();
    int losuj(double kwantCzasu);
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.wroc.pwr.iis.rozklady.dyskretne;

import pl.wroc.pwr.iis.rozklady.IRozkladDyskretny;


/**
 * @author Misiek
 */
public class RozkladJednostajnyDyskretny implements IRozkladDyskretny {

    private int wartosc;

    public RozkladJednostajnyDyskretny() {
    }

    public RozkladJednostajnyDyskretny(int wartosc) {
        setWartosc(wartosc);
    }
    
    public void setWartosc(int wartosc) {
        this.wartosc = wartosc;
    }

    public int losuj() {
        return wartosc;
    }

	@Override
	public int losuj(double kwantCzasu) {
		return wartosc;
	}
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.wroc.pwr.iis.rozklady;


/**
 * @author Misiek
 */
public class RozkladJednostajny implements IRozkladPrawdopodobienstwa {

    private int wartosc;

    public RozkladJednostajny() {
    }

    public RozkladJednostajny(int wartosc) {
        setWartosc(wartosc);
    }
    
    public void setWartosc(int wartosc) {
        this.wartosc = wartosc;
    }

    public int losuj() {
        return wartosc;
    }
}

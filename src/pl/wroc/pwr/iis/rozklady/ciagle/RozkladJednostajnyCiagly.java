/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.wroc.pwr.iis.rozklady.ciagle;

import pl.wroc.pwr.iis.rozklady.IRozkladCiagly;


/**
 * @author Misiek
 */
public class RozkladJednostajnyCiagly implements IRozkladCiagly {

    private double wartosc;

    public RozkladJednostajnyCiagly() {
    }

    public RozkladJednostajnyCiagly(double wartosc) {
        setWartosc(wartosc);
    }
    
    public void setWartosc(double wartosc) {
        this.wartosc = wartosc;
    }

    public double losuj() {
        return wartosc;
    }

}

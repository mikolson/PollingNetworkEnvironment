/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.wroc.pwr.iis.polling.model.object.polling;

/**
 *
 * @author Misiek
 */
public class Zgloszenie {
    protected int czasOczekiwania;

    public Zgloszenie() {
    }
    
    public void zwiekszCzasOczekiwania() {
        czasOczekiwania++;
    }
    
    public void usunCzasOczekiwania(){ 
        czasOczekiwania = 0;
    }

    public int getCzasOczekiwania() {
        return czasOczekiwania;
    }

}

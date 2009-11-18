/*
 * Polaczenia.java
 * 
 * Created on 2007-10-19, 16:50:04
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.wroc.pwr.iis.polling.model.object.polling;


/**
 *
 * @author Misiek
 */
public class Polaczenie {
    float prawdopodobienstwo = 0;
    Kolejka kolejkaDocelowa = null;

    public Polaczenie(Kolejka kolejkaDocelowa, float prawdopowodbienstwo) {
        this.kolejkaDocelowa = kolejkaDocelowa;
        this.prawdopodobienstwo = prawdopowodbienstwo;
    }

    public float getPrawdopodobienstwo() {
        return prawdopodobienstwo;
    }

    public void setPrawdopodobienstwo(float prawdopodobienstwo) {
        assert (prawdopodobienstwo >= 100 | prawdopodobienstwo < 0) : "Błędne parametry";
        
        this.prawdopodobienstwo = prawdopodobienstwo;
    }

	public Kolejka getKolejkaDocelowa() {
		return kolejkaDocelowa;
	}

}

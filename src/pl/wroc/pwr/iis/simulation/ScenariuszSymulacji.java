/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.wroc.pwr.iis.simulation;

import java.util.ArrayList;

import pl.wroc.pwr.iis.polling.model.object.polling.Serwer;

/**
 *
 * @author Misiek
 */
public class ScenariuszSymulacji {
	// Parametry
	int iloscKrokowSymulacji = 0;
	ArrayList<Serwer> serwery = new ArrayList<Serwer>();

	//
	
	public int getIloscKrokowSymulacji() {
		return iloscKrokowSymulacji;
	}

	public void setIloscKrokowSymulacji(int iloscKrokowSymulacji) {
		this.iloscKrokowSymulacji = iloscKrokowSymulacji;
	}

	public ArrayList<Serwer> getSerwery() {
		return serwery;
	}

	public void setSerwery(ArrayList<Serwer> serwery) {
		this.serwery = serwery;
	}
	
	public void addSerwer(Serwer serwer) {
		this.serwery.add(serwer);
	}
	
	public void removeSerwer(Serwer serwer) {
		this.serwery.remove(serwer);
	}
	
}

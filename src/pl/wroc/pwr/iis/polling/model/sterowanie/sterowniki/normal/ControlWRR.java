/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.normal;

import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Sterownik;

/**
 * Weighted Round Robin 
 * given number of requests 
 * 
 * @author Michał Stanek <michal.stanek@pwr.wroc.pl>
 */
public class ControlWRR extends Sterownik {
	private int action = 0;
	private final int length;
	private int l;
	public ControlWRR(int length) {
		super(null);
		this.length = length;
		l = 0;
	}

	@Override
	public int getDecyzjaSterujaca(float ocenaStanu, int[] stan, int iloscAkcji) {
//		if (l++ > length) {
//			action = (action + 1) % iloscAkcji;
//			l = 0;
//		}
//		return action;
	}
	
}

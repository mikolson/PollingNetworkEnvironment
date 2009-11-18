package pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaOceny;

import pl.wroc.pwr.iis.polling.model.object.polling.Kolejka;

public abstract class OcenaSytuacji_A {

	protected float kara = 1;

	public OcenaSytuacji_A() {
		super();
	}

	/**
	 * Kara liczona na zasadzie:
	 *  result = ilosc_cykli * kara * waga
	 * 
	 * ilosc_cykli - ilosc cykli, ponad limit
	 * 
	 * @param kolejka
	 * @return
	 */
	protected float doliczKare(Kolejka kolejka) {
		float result = 0;
		int czas = kolejka.getCzasOczekiwania();
		int max = kolejka.getMaxCzasOczekiwania();
	
		if (czas - max < 0) {
			result = (czas-max) * kara * kolejka.getWaga();
		}
		
		return result;
	}

	public float getKara() {
		return kara;
	}

	public void setKara(float kara) {
		this.kara = kara;
	}

	
}
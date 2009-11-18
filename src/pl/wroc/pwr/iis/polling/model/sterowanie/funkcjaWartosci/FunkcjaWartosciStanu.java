package pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaWartosci;

import pl.wroc.pwr.iis.rozklady.random.RandomGenerator;

public class FunkcjaWartosciStanu extends FunkcjaWartosci {
	float[] wartosci;
	
	public FunkcjaWartosciStanu(int maxStanow) {
		super();
		wartosci = new float[maxStanow];
	}
	
	public float getWartosc(int stan) {
		return wartosci[stan];
	}

	@Override
	public void zainicjujLosowo() {
		for (int i = 0; i < wartosci.length; i++) {
			wartosci[i] = (float) RandomGenerator.getDefault().nextDouble();
		}
	}

	@Override
	public void wyczysc() {
		for (int i = 0; i < wartosci.length; i++) {
			wartosci[i] = 0;
		}
	}
}

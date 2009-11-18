/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Qlearning;

import pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaWartosci.FunkcjaWartosciAkcji;
import pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Sterownik;
import pl.wroc.pwr.iis.polling.model.sterowanie.strategie.Strategia_A;


/**
 * @author Misiek
 */
public class SarsaOnPolicy extends Sterownik {
private static final int BRAK_USTAWIONEJ_WARTOSCI = -1;
	
	protected int 	poprzedniaAkcja = BRAK_USTAWIONEJ_WARTOSCI;
	protected int 	poprzedniStan = BRAK_USTAWIONEJ_WARTOSCI;
    protected float poprzednieWzmocnienie = BRAK_USTAWIONEJ_WARTOSCI;
	
    /**
     * Tablica zawierajaca wartosci Q(x,a)
     */
    protected final FunkcjaWartosciAkcji Q;

	private float alfa; 
	private float dyskont;
	
	public SarsaOnPolicy(Strategia_A strategia, float alfa, float dyskont) {
		super(strategia);
		this.alfa = alfa;
		this.dyskont = dyskont;
		Q = strategia.getFunkcjaWartosciAkcji();
	}

	@Override
	public int getDecyzjaSterujaca(float ocenaStanu, int[] stan, int iloscAkcji) {
		/*
		 * Po przejsciu do kolejnego stanu nalezy poprawić informacje o stanie
		 * w którym byliśmy 
		 */ 
		 int wybranaAkcja = strategia.getAkcja(stan, iloscAkcji); // Akcja wybrana dla tego kroku
		 
		 if (poprzedniStan != BRAK_USTAWIONEJ_WARTOSCI) { // poprawa następuje tylko wtedy kiedy był poprzedni stan 
			 //Wartość Q dla poprzedniego stanu
			 float Q_s_a = Q.getWartosc(poprzedniStan, poprzedniaAkcja);
			 float r = ocenaStanu;
			 // Max wartosc Q w biezacym stanie
			 float Q_sn_a = Q.getWartosc(strategia.getNumerStanu(stan), wybranaAkcja);
			 float nowaWartoscQ = Q_s_a + alfa * (r + dyskont * Q_sn_a - Q_s_a);

			 // Ustawienie nowej wartośći akcji
			 Q.setWartosc(poprzedniStan, poprzedniaAkcja, nowaWartoscQ);
		 }
		
		poprzedniStan = strategia.getNumerStanu(stan);
		poprzedniaAkcja = wybranaAkcja;
		poprzednieWzmocnienie = ocenaStanu;
		
		return poprzedniaAkcja;
	}
	
	@Override
	public int startSterowania() {
		poprzedniaAkcja = BRAK_USTAWIONEJ_WARTOSCI;
		poprzedniStan = BRAK_USTAWIONEJ_WARTOSCI;
	    poprzednieWzmocnienie = BRAK_USTAWIONEJ_WARTOSCI;
		return 0;
	}

}

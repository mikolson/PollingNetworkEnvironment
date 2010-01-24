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
public class QLearning extends Sterownik {
	private static final int BRAK_USTAWIONEJ_WARTOSCI = -1;
	
	protected int 	poprzedniaAkcja = BRAK_USTAWIONEJ_WARTOSCI;
	protected int 	poprzedniStan = BRAK_USTAWIONEJ_WARTOSCI;
    protected double poprzednieWzmocnienie = BRAK_USTAWIONEJ_WARTOSCI;
	
    /**
     * Tablica zawierajaca wartosci Q(x,a)
     */
    protected final FunkcjaWartosciAkcji Q;

	private float alfa; 
	private float dyskont;
	
	public QLearning(Strategia_A strategia, float alfa, float dyskont) {
		super(strategia);
		this.alfa = alfa;
		this.dyskont = dyskont;
		Q = strategia.getFunkcjaWartosciAkcji();
		Q.setMin_poprawa(alfa);
	}

	@Override
	public int getDecyzjaSterujaca(double ocenaStanu, int[] stan, int iloscAkcji) {
		/*
		 * Po przejsciu do kolejnego stanu nalezy poprawić informacje o stanie
		 * w którym byliśmy 
		 */ 
//		if (poprzedniStan != 7) {
//			System.out.println("QLearning.getDecyzjaSterujaca(): " + stan[0] + " : " + stan[1] + " : " + stan[2] + " ocena: " + ocenaStanu + " akcja: " + poprzedniaAkcja );
//		}
		poprzedniStan = strategia.getOstatniStan();
		 if (poprzedniStan != Strategia_A.BRAK_USTAWIONEJ_WARTOSCI) { // poprawa następuje tylko wtedy kiedy był poprzedni stan 
			 //Wartość Q dla poprzedniego stanu
			 double Q_s_a = Q.getWartosc(poprzedniStan, poprzedniaAkcja);
			 double r = ocenaStanu;
			 // Max wartosc Q w biezacym stanie
			 double maxQ_sn_a = Q.getMaxAkcja(strategia.getNumerStanu(stan), iloscAkcji).wartosc;
			 double nowaWartoscQ = Q_s_a + alfa * (r + dyskont * maxQ_sn_a - Q_s_a);
//			 float nowaWartoscQ = r + dyskont * maxQ_sn_a;
			 Q.setWartosc(poprzedniStan, poprzedniaAkcja, nowaWartoscQ);
//			 Q.poprawWartosc(poprzedniStan, poprzedniaAkcja, nowaWartoscQ);
			 System.out.println("\tStara wartosc: " + Q_s_a + " : Nowa wartość: " + nowaWartoscQ + " : Reinforcement: " + ocenaStanu);
			 
//			 System.out.println();
			 System.out.println("\tPoprzedni:" + poprzedniStan + " : " 
	 					+ Q.getWartosc(poprzedniStan, 0) + " : " 
	 					+ Q.getWartosc(poprzedniStan, 1) + " : "
	 					+ Q.getWartosc(poprzedniStan, 2)
	 					);

			 
			 System.out.println("\tstan:" + strategia.getNumerStanu(stan) + " : " 
					 					+ Q.getWartosc(strategia.getNumerStanu(stan), 0) + " : " 
					 					+ Q.getWartosc(strategia.getNumerStanu(stan), 1) + " : "
					 					+ Q.getWartosc(strategia.getNumerStanu(stan), 2)
					 					);
//			 
			 // Ustawienie nowej wartośći akcji
		 } 
		
		poprzedniStan = strategia.getNumerStanu(stan);
		poprzedniaAkcja = strategia.getAkcja(stan, iloscAkcji);
		poprzednieWzmocnienie = ocenaStanu;
		
		System.out.println("Ackcja: " + poprzedniaAkcja +  "  Max akcja: " + Q.getMaxAkcja(poprzedniStan, iloscAkcji).numer);
		
		return poprzedniaAkcja;
	}
	
	@Override
	public int startSterowania() {
		poprzedniaAkcja = BRAK_USTAWIONEJ_WARTOSCI;
		poprzedniStan = BRAK_USTAWIONEJ_WARTOSCI;
	    poprzednieWzmocnienie = BRAK_USTAWIONEJ_WARTOSCI;
		
		return 0;
	}

	@Override
	public int koniecSterowania() {
		return super.koniecSterowania();
	}

	public String toStringHeader() {
		return  "Reinforcement;Action;State;Alpha;Gamma";
	}
	
	@Override
	public String toString() {
		StringBuffer out = new StringBuffer();
		
		double[] res = new double[]{
			poprzednieWzmocnienie, poprzedniaAkcja, poprzedniStan, alfa, dyskont 
		};
		
		for (int i = 0; i < res.length-1; i++) {
			out.append(res[i]);
			out.append(";");
		}
		out.append(res[res.length-1]);
		
		return out.toString();
	}
	
}

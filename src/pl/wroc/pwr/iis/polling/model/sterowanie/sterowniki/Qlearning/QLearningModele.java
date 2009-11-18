/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.wroc.pwr.iis.polling.model.sterowanie.sterowniki.Qlearning;

import pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaWartosci.FunkcjaWartosciAkcji;
import pl.wroc.pwr.iis.polling.model.sterowanie.strategie.Strategia_A;


/**
 * @author Misiek
 */
public class QLearningModele extends QLearningWariancja {
	protected static final int STATYSTYCZNY_PROG_ILOSCI_POMIAROW = 30;
	
	final int MAX_MODELI = 3;
//    private static final double WSP_KOREKTY_MODELI = 0.1;
//    private static final double PROG_WIARYGODNOSCI_MODELI = 0.5;
//    protected float granicaOdchylenia = 4;
//    protected float granicaOdchylenia = 5.5f;
    private static final double WSP_KOREKTY_MODELI = 0.9;
    private static final double PROG_WIARYGODNOSCI_MODELI = 0.3;
    
    // Tworzenie wielu modeli 
    FunkcjaWartosciAkcji[] model = new FunkcjaWartosciAkcji[MAX_MODELI];
    double[] wiarygodnoscModelu   = new double[MAX_MODELI];
    int wykorzystanychModeli = 1;
    int aktualnyModel = 0;
    
	@Override
	public int startSterowania() {
		for (int i = 0; i < model.length; i++) {
			model[i] = new FunkcjaWartosciAkcji(strategia.getIloscStanow(), strategia.getIloscAkcji());
			wiarygodnoscModelu[i] = 0;
			model[i].setMin_poprawa(alfa);
		}
		
		wiarygodnoscModelu[0] = 1;
		wykorzystanychModeli = 1;
		
		return 0;
	}
	
	public QLearningModele(Strategia_A strategia, float alfa, float dyskont, int iloscAkcji) {
		super(strategia, alfa, dyskont, iloscAkcji);
	}
	
	@Override
	public int getDecyzjaSterujaca(float ocenaStanu, int[] stan, int iloscAkcji) {
		final int aktStan  	= strategia.getNumerStanu(stan);
		final int prevStan 	= strategia.getOstatniStan();
		final int prevAkcja = strategia.getOstatniaAkcja();
		final float r 		= ocenaStanu;
		
		this.shake = false;
//		final float r = ocenaStanu;
		
		if (prevStan != Strategia_A.BRAK_USTAWIONEJ_WARTOSCI) { // poprawa następuje tylko wtedy kiedy był poprzedni stan 
			model[aktualnyModel].poprawObserwacje(prevStan,prevAkcja,r);
			 // Liczone po otrzy

		     boolean zmianaModelu = false;
		     int numerNajlepszegoModelu = aktualnyModel;
			 double najlepszyModel = 0; // wsp Wiarygodnosci najlepszego modelu
			 // Sprawdzenie czy dla danego modelu można wogóle zrobić porównanie statystyczne
			 if ( model[aktualnyModel].getIloscObserwacji(prevStan, prevAkcja) > STATYSTYCZNY_PROG_ILOSCI_POMIAROW //) { 
					 && strategia.czyStabilna()) {
				 // Ustalenie stopnia wiarygodności aktualnego odczytu dla kazdego z modeli
				 for (int i = 0; i < wykorzystanychModeli; i++) {
					float delta = odlegloscObserwacji(model[i], prevStan, prevAkcja, r);
					float granicaOdchylenia = granicaOdchylenieObserwacji(model[i], prevStan, prevAkcja);
					float korekta = 0;
					if (delta <= granicaOdchylenia) {
//						korekta = (float) (-Math.pow(delta,2)/(Math.pow(granicaOdchylenia, 2)) + 1);
						korekta = 1.0f;
					} else {
//						korekta = granicaOdchylenia / delta;
						korekta = -1.0f;
					}
					
//					wiarygodnoscModelu[i] = wiarygodnoscModelu[i] - WSP_KOREKTY_MODELI*(korekta - wiarygodnoscModelu[i]);
					wiarygodnoscModelu[i] = Math.max(0, Math.min(1, wiarygodnoscModelu[i] +  WSP_KOREKTY_MODELI*(korekta)));
					
					// Korekta najlepszego modelu
					zmianaModelu= false;
					if (wiarygodnoscModelu[i]-0.001 > najlepszyModel) {
						najlepszyModel = wiarygodnoscModelu[i];
						if (aktualnyModel != i) { // Sprawdzenie czy nastąpiła zmiana modeli
							numerNajlepszegoModelu = i;
							zmianaModelu = true;
						} 
					}
					
				 }
				 // Aktualny model się zdezauktualizował 
				 if(wiarygodnoscModelu[aktualnyModel] <= PROG_WIARYGODNOSCI_MODELI) {
					 shake =true;
					 
			 		System.out.println("\n\nAktualny model: " + aktualnyModel + "("+ wiarygodnoscModelu[aktualnyModel] + ") -> " + numerNajlepszegoModelu + "(" + wiarygodnoscModelu[numerNajlepszegoModelu]);
					 aktualnyModel = numerNajlepszegoModelu;
					 
					// Sprawdzenie czy przypadkiem ktorys z modeli nie spelnia ograniczenia
					 if (wiarygodnoscModelu[numerNajlepszegoModelu] > PROG_WIARYGODNOSCI_MODELI) {
						 aktualnyModel = numerNajlepszegoModelu;
						 model[aktualnyModel].wyczyscObserwacje();
					 } else {
						 // Stworz nowy model o ile nie przekroczono już liczby możliwych modeli
						 if(wykorzystanychModeli < MAX_MODELI){
							 System.out.println("!!!! TWORZĘ MODEL !!!!");
							 aktualnyModel = dodajModel(model[numerNajlepszegoModelu]); 
							 zrobShake(alfaShake, dyskontShake, temperatureShake);
							 shake =true;
						 } else { // Szybsza korekta najlepszego dotychczas wykorzystywanego modelu
							 if (wiarygodnoscModelu[aktualnyModel] < PROG_WIARYGODNOSCI_MODELI) {
								 zrobShake(alfaShake, dyskontShake, temperatureShake);
								 wiarygodnoscModelu[aktualnyModel] = 1;
								 shake =true;
								 System.out.println("\n\n!!!! SHAKE ISTNIEJĄCEGO MODELU - (" + aktualnyModel + " )" );
							 } else {
								 System.out.println("Przełączenie modeli ale próg był zachowany - pracuję bez shaka");
								 wiarygodnoscModelu[aktualnyModel] = 1;
							 }
						 }
					 }
				 }
					 
			 }
			 // Poprawienie parametrów uczenia - powrót wartości alfa, gamma, oraz temperatury
			 poprawParametry(model[aktualnyModel]);
			 
			 // Max wartosc Q w biezacym stanie
			 float maxQ_sn_a = model[aktualnyModel].getMaxAkcja(aktStan, iloscAkcji).wartosc;
			 float nowaWartoscQ = r + currentDyskont * maxQ_sn_a;	 
			 model[aktualnyModel].poprawWartosc(prevStan, prevAkcja, nowaWartoscQ);
			 
//			 float maxQ_sn_a = Q.getMaxAkcja(aktStan, iloscAkcji).wartosc;
//			 float nowaWartoscQ = r + currentDyskont * maxQ_sn_a;	 
//			 Q.poprawWartosc(prevStan, prevAkcja, nowaWartoscQ);
		 }
		
		polaczModele(aktStan);
//		strategia.setFunkcjaWartosciAkcji(model[aktualnyModel]);
		return strategia.getAkcja(stan, iloscAkcji); // Od tego miejsca bedzie to juz aktualna akcja;
	}

	private void polaczModele(int stan) {
		// Kumuluje wartosci wszystkich modeli spelniajacych warunek, że
		// ich przystosowanie jest wieksze od zadanego progu
		
//		for (int akcja = 0; akcja < strategia.getIloscAkcji(); akcja++) {
//			float wynik = 0;
////			wynik = model[aktualnyModel].getWartosc(stan, akcja);
//			for (int m = 0; m < wykorzystanychModeli; m++) {
//				if (wiarygodnoscModelu[m] > PROG_WIARYGODNOSCI_MODELI) {
//					wynik += (float) (model[m].getWartosc(stan, akcja) * wiarygodnoscModelu[m]);
////					wynik += (float) (model[m].getWartosc(stan, a) * 1/wykorzystanychModeli);
//				}
//			}
//			Q.setWartosc(stan, akcja, wynik);
//		}
//		
////		Q.ustaw(model[0]);
		
		// Wpisuje wartosc tylko biezacego modelu
		Q.ustaw(model[aktualnyModel]);
	}
	
	
//	private void polaczModele(int stan) {
//		for (int akcja = 0; akcja < strategia.getIloscAkcji(); akcja++) {
//			float wynik = 0;
////			wynik = model[aktualnyModel].getWartosc(stan, akcja);
//			for (int m = 0; m < wykorzystanychModeli; m++) {
//				if (wiarygodnoscModelu[m] > PROG_WIARYGODNOSCI_MODELI) {
//					wynik += (float) (model[m].getWartosc(stan, akcja) * wiarygodnoscModelu[m]);
////					wynik += (float) (model[m].getWartosc(stan, a) * 1/wykorzystanychModeli);
//				}
//			}
//			Q.setWartosc(stan, akcja, wynik);
//		}
//		
////		Q.ustaw(model[0]);
////		Q.ustaw(model[aktualnyModel]);
//	}

	private int dodajModel(FunkcjaWartosciAkcji modelBazowy) {
		wiarygodnoscModelu[wykorzystanychModeli] = 1;
		model[wykorzystanychModeli].ustaw(modelBazowy);
		this.wykorzystanychModeli++;
		return wykorzystanychModeli-1;
	}
	
	public int getIloscModeli() {
		return this.wykorzystanychModeli;
	}
}

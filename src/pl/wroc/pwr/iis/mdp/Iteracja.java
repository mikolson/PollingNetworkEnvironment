package pl.wroc.pwr.iis.mdp;

import static java.lang.Math.*;

public class Iteracja {
	static final int N = 20;
	static final int A = 10000;
	private static final float EPSILON = 0.00001f;
	
	/**  Macierz prawdopodobie�st przej�� do stan�w */
	static float[][] P = new float[N*A][N];  
	/**  Macierz */
	static float[][] R = new float[N*A][N];
	/**  Macierz prawdopodobie�st przej�� do stan�w */
	static float[] V = new float[N];

	/**
	 * Metoda wypelnia macierz prawdopodobieństwa
	 * 
	 * @param p macierz prawdopodobie�stwa
	 * @param akcji liczba akcji
	 */
	protected void wypelnij_P(float[][] p, int akcji) {
		float suma, s; 
		int n = p.length / akcji;
		for (int a = 0; a < akcji; a++) {
			suma = 0;
			
			for (int j = 0; j < n; j++) {
			  suma += abs(sin(j-a));	
			}
			
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
			       s = (float)abs(pow(sin(i - a),2));
			       p[i*akcji+a][j] = (float)(s + abs(sin(j-a)))/(n*s + suma);
				}
			}
		}
	}
	
	/**
	 * Wypelnia macierz nagrod
	 * @param r macierz warto�ci nagrody
	 * @param akcji ilo�� akcji w systemie
	 */
	protected void wypelnij_R(float[][] r, int akcji) {
	  int wymiar = r.length / akcji;
	  for (int i = 0; i < wymiar; i++){
	    for (int j = 0; j < wymiar; j++) {
	      for (int a = 0; a < akcji; a++) {
	         //r[i*akcji+a][j] = (float)exp(sin(i+j+a+3)); // +3 ze wzgledu na indeksy od 0
	    	  r[i*akcji+a][j] = (i-a)/(j+1); // +3 ze wzgledu na indeksy od 0
	      }
	    }
	  } 
	}
	
	/**
	 * Sprawdzenie czy w kazdym wierszu macierzy jest jedynka
	 * 
	 * @param m Macierz do sprawdzenia
	 * @param akcji Liczba akcji
	 */
	public void is_one(float[][] m) {
		for (int i = 0; i < m.length; i++) {
			float suma = 0; 
			for (int j = 0; j < m[i].length; j++) {
				suma += m[i][j];
			}
			
			// je�eli  pole ma warto�� wi�ksz� ni� zadany epsilon
			if ( abs(suma - 1) > EPSILON) { 
				System.out.printf("W wierszu %d jest r�wne %f\n", new Object[]{i, suma} );
			}
		}
	}
	
	float suma_V(float[][] p, float[] V, final int i, final int a, final int akcji) {
		float result = 0;
		int j;

		for (j = 0; j < N; j++) {
			result += p[i * akcji + a][j] * V[j];
		}

		return result;
	}

	/**
	 * @param p
	 * @param r
	 * @param akcji
	 * @param epsilon
	 * @param beta
	 * @return Macierz V, b�d�ca funkcj� warto�ci dla ka�dego ze stan�w
	 */
	public float[] iteracja(float[][] p, float[][] r, int akcji,	float epsilon, float beta) {

		int n = p.length / akcji;

		float[] V = new float[n];
		float[] tmp_V = new float[n];
		float v, delta, max_delta;
		float max_akcja, estim_r = 0;

		float[][] estim_R = new float[n][akcji];
		int i, j, a;

		// Inicjalizacja warto�ci
		for (i = 0; i < n; i++) {
			V[i] = i;
		}

		// Obliczenie macierzy warto�ci estymowanych dla ka�dej akcji
		for (a = 0; a < akcji; a++) {
			for (i = 0; i < n; i++) {
				for (j = 0; j < n; j++) {
					estim_r += r[i * akcji + a][j] * p[i * akcji + a][j];
				}
				estim_R[i][a] = estim_r;
			}
		}

		do {
			// Obliczenie funkcji wartosci dla n-tej chwili czasu na
			// podstawie stanu systemu z chwili N-1
			for (i = 0; i < n; i++) {
				max_akcja = -1;
				for (a = 0; a < akcji; a++) {
					v = estim_R[i][a] + 
							beta * suma_V(p, V, i, a, akcji);
					if (v > max_akcja)
						max_akcja = v;
				}
				tmp_V[i] = max_akcja;
			}

			max_delta = 0;
			// Przepisanie nowych wartosci V do macierzy funkcji wartosci
			for (i = 0; i < n; i++) {
				delta = abs(V[i] - tmp_V[i]);
				max_delta = max(max_delta, delta);
				V[i] = tmp_V[i];
			}

		} while ((beta / (1 - beta) * max_delta) >= epsilon);// while
		System.out.println("Delta: " + max_delta);
		return V;
	}
	
	public void wyswietl(float[] tab) {
		for (int i = 0; i < tab.length; i++) {
			System.out.println(i + ": " + tab[i]);
		}
	}
	
	public static void main(String[] args) {
		long time = System.currentTimeMillis();
		Iteracja iteracja = new Iteracja();
		
		iteracja.wypelnij_P(P, A);
		iteracja.wypelnij_R(R, A);
		iteracja.is_one(P);
		
		float[] v = iteracja.iteracja(P, R, A, 0.01f, 0.9f);
		iteracja.wyswietl(v);
		
		System.out.printf("Czas wykonania: %d [ms]\n", new Object[]{System.currentTimeMillis() - time});
	}
}

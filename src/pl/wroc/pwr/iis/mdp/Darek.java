package pl.wroc.pwr.iis.mdp;

public class Darek {
	
	public static void main(String[] args) {
		int L = 4;
		int R = 4;
		
		double[] X = new double[L+R];
		double poprawa = 0;
		double epsilon = 0.0001;
		do {
			poprawa = 0;
			for (int i = 0; i < R; i++) {
				
				double suma = 0;
				for (int j = 0; j < L; j++) {
					suma += getP_i_j(i, j) * X[j+R];
				}
				
				X[i] = getA_i(i) /  suma;
			}
			
			for (int i = R; i < X.length; i++) {
				X[i] = getU_j(i); 
			}
			
		} while( poprawa < epsilon );
	}
	
	
	private static double getA_i(int i) {
		double[] wynik = {4,3,2,1};
		return wynik[i];
	} 
	
	private static int getP_i_j(int i, int j) {
		int[][] wynik = { {1,1,0,0}, {0,1,1,0}, {0,0,1,1}, {1,0,0,1}};
		return wynik[i][j];
	}
	
	private static double getU_j(int j) {
		double[] wynik = {8,6,4,2};
		return wynik[j];
	}

}

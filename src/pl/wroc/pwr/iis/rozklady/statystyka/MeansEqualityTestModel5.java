package pl.wroc.pwr.iis.rozklady.statystyka;


import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

public class MeansEqualityTestModel5 {

	private double [] X;
	private double [] Y;
	private double alpha; //0.01, 0.05

	public MeansEqualityTestModel5( List< Double > X, List< Double > Y ) {
		this.X = new double[ X.size( ) ];
		for (int i = 0; i < X.size(); i++) {
			this.X[i] = X.get(i);
		}
		this.Y = new double[ Y.size( ) ];
		for (int i = 0; i < Y.size(); i++) {
			this.Y[i] = Y.get(i);
		}
		this.alpha = 0.01;
	}

	public MeansEqualityTestModel5( double [] X, double [] Y ) {
		this(X,Y,0.01);
	}

	public MeansEqualityTestModel5( double [] X, double [] Y, double alpha ) {
		this.X = X;
		this.Y = Y;
		this.alpha = alpha;
	}

	public Boolean apply(BufferedWriter out) throws IOException {
		if ( this.X.length != this.Y.length ) {
			return null;
		}
		double Z = 0;
		double Sz = 0;
		double [] Zi = new double[ this.X.length ];

		for ( int i = 0; i < this.X.length; i++ ) {
			Zi[ i ] = this.X[ i ] - this.Y[ i ];
		}

		for ( int i = 0; i < Zi.length; i++ ) {
			Z = Z + Zi[ i ];
		}
		Z = Z / Zi.length;

		for ( int i = 0; i < Zi.length; i++ ) {
			Sz = Sz + (Zi[i] - Z)*(Zi[i] - Z);
		}
		Sz = Math.sqrt( Sz / Zi.length );

		double t = ( Z * Math.sqrt( Zi.length - 1 ) ) / Sz;
		double H1Begin = new TStudentQuantile( 1 - this.alpha, Zi.length - 1 ).apply( );

		out.append("# Licznosc  :" + Zi.length);
			out.append("\n");
		out.append("# Srednia   :" + Z);
			out.append("\n");
		out.append("# Odch. st. :" + Sz);
			out.append("\n");
		out.append("# Przedzial :<" + H1Begin + ", +inf)");
			out.append("\n");
		out.append("# Wartosc t :" + t);
			out.append("\n");

		if ( t >= H1Begin ) {
			out.append("# Hipoteza zerowa odrzucona\n");
			return false;
		}
		out.append("# Brak przeslanek do odrzucenia hipotezy zerowej\n");
		return true;
	}
}

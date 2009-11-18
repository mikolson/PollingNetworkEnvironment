package pl.wroc.pwr.iis.rozklady.statystyka;
import JSci.maths.statistics.TDistribution;



public class TStudentQuantile {

	private double value;
	private int dof;

	public TStudentQuantile( double value, int dof ) {
		this.value = value;
		this.dof = dof;
	}

	public Double apply( ) {
		TDistribution dist = new TDistribution(this.dof);
		double x = 0;
		while (dist.cumulative(x) < this.value) {
			x += 0.0001;
		}
		return x;
	}

	public static void main( String [] args ) {
		TStudentQuantile quantile = new TStudentQuantile(0.99, 1);
		double value = quantile.apply();
		System.out.println(value);
	}
}

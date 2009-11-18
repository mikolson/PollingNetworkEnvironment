package pl.wroc.pwr.iis.rozklady.random;

import edu.cornell.lassp.houle.RngPack.RanMT;
/**
 * AbstractRandomGenerator based on RngPack RanMT generator.
 */
public class RandomGenerator {
	private RanMT random = new RanMT();
//	private Ranlux random = new Ranlux();
	
	private static RandomGenerator defaultRandom = new RandomGenerator();
    public static RandomGenerator getDefault() {
    	return defaultRandom;
    }
	
    
	public RandomGenerator() {
	}
	public RandomGenerator(long seed) {
		setSeed(seed);
	}
    
	public static void setDefaultSeed(long seed) {
       defaultRandom.setSeed(seed);
	}	
    
    public void setSeed(long seed) {
       random = new RanMT(seed);
//       random = new Ranlux(seed);
    }
    
    public double nextDouble() {
        return random.raw();
    }
    
    public double nextGaussian() {
        return random.gaussian();
    }
    
    public int nextInt(int n) {
        return random.choose(n);
    }
    
    public boolean nextBoolean() {
        return random.coin();
    }
    
    public static void main(String[] args) {
		RandomGenerator rngPackGenerator = new RandomGenerator();
		
		for (int i = 0; i < 100; i++) {
			System.out.println(rngPackGenerator.nextDouble());
		}
	}
}
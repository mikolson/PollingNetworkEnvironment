package pl.wroc.pwr.iis;

import junit.framework.Test;
import junit.framework.TestSuite;
import pl.wroc.pwr.iis.polling.model.KolejkaTest;
import pl.wroc.pwr.iis.polling.model.SerwerTest;
import pl.wroc.pwr.iis.polling.model.rozklady.RozkladPoissonaTest;
import pl.wroc.pwr.iis.polling.model.rozklady.RozkladRownomiernyTest;
import pl.wroc.pwr.iis.polling.model.sterowanie.deterministyczne.SterownikNaZmianeTest;
import pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaOceny.routery.OcenaTimeRosnacoOrginalnaTest;
import pl.wroc.pwr.iis.polling.model.sterowanie.funkcjaWartosci.FunkcjaWartosciTest;
import pl.wroc.pwr.iis.polling.model.sterowanie.reprezentacjaStanu.StanLiczbaSpelnionychOgraniczenTest;
import pl.wroc.pwr.iis.polling.model.sterowanie.strategie.StrategiaEZachlannaTest;
import pl.wroc.pwr.iis.rozklady.LosujTest;
import pl.wroc.pwr.iis.rozklady.RozkladBernouliegoTest;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for pl.wroc.pwr.iis");
		//$JUnit-BEGIN$
			suite.addTestSuite(SerwerTest.class);
			suite.addTestSuite(KolejkaTest.class);
			
			suite.addTestSuite(RozkladPoissonaTest.class);
			suite.addTestSuite(RozkladRownomiernyTest.class);
			suite.addTestSuite(LosujTest.class);
			suite.addTestSuite(RozkladBernouliegoTest.class);
			
			suite.addTestSuite(SterownikNaZmianeTest.class);
			suite.addTestSuite(OcenaTimeRosnacoOrginalnaTest.class);
			suite.addTestSuite(FunkcjaWartosciTest.class);
			
			suite.addTestSuite(StanLiczbaSpelnionychOgraniczenTest.class);
			suite.addTestSuite(StrategiaEZachlannaTest.class);
		//$JUnit-END$
		return suite;
	}

}

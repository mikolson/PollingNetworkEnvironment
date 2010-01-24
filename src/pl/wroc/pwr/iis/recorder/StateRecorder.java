/*
 * Copyright 2009 Michal Stanek
 * http://github.com/mikolson/PollingNetworkEnvironment
 * mstanek82@gmail.com
 * http://mstanek.blogspot.com
 */


package pl.wroc.pwr.iis.recorder;

public class StateRecorder {
	int step = 0;
	String[] header = new String[] {
			"Step",
			"Load", 
			"Breaking QoS[]", 
			"Avg. number of requests[]", 
			"Avg. waiting time[]",
			"Avg. time till served",
		};
	
	
}

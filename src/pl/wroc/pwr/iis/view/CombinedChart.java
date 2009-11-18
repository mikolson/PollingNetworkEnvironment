package pl.wroc.pwr.iis.view;
import java.awt.Color;
import java.awt.Font;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.TextAnchor;

/**
 * A demonstration application showing how to create a vertical combined chart.
 *
 */
public class CombinedChart extends ApplicationFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7066056943842984716L;

	/**
     * Constructs a new demonstration application.
     *
     * @param tytul  the frame title.
     */
    public CombinedChart(final String tytul, String podpisKolejki, int krok, double[][] wykres1, double[][] wykres2) {
        super(tytul);
        final JFreeChart chart = createCombinedChart(tytul, createDataset1(podpisKolejki, wykres1, krok), createDataset1(podpisKolejki, wykres2, krok));
        final ChartPanel panel = new ChartPanel(chart, true, true, true, false, true);
        panel.setPreferredSize(new java.awt.Dimension(1000, 900));
        setContentPane(panel);
    }
    
    /**
     * Creates a combined chart.
     *
     * @return The combined chart.
     */
    private JFreeChart createCombinedChart(String tytul, XYDataset wykres1, XYDataset wykres2) {

        // create subplot 1...
        final XYItemRenderer renderer1 = new StandardXYItemRenderer();
//        	XYLineAndShapeRenderer renderer1 = new XYLineAndShapeRenderer();
//        	 renderer1.setShapesVisible(true);
//             renderer1.setShapesFilled(false);

        final NumberAxis rangeAxis1 = new NumberAxis("Function 1");
        final XYPlot subplot1 = new XYPlot(wykres1, null, rangeAxis1, renderer1);
        subplot1.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);

        // create subplot 2...
        final XYItemRenderer renderer2 = new StandardXYItemRenderer();

        final NumberAxis rangeAxis2 = new NumberAxis("Function 2");
        rangeAxis2.setAutoRangeIncludesZero(false);
        final XYPlot subplot2 = new XYPlot(wykres2, null, rangeAxis2, renderer2);
        subplot2.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT);

        // parent plot...
        final CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new NumberAxis("Iteration"));
        plot.setGap(10.0);
        
        // add the subplots...
        plot.add(subplot1, 1);
        plot.add(subplot2, 1);
        plot.setOrientation(PlotOrientation.VERTICAL);
        
        XYTextAnnotation annotation = null;
        Font font = new Font("SansSerif", Font.PLAIN, 9);
            
        annotation = new XYTextAnnotation("1 queue", 50000, 100);
        annotation.setFont(font);
        annotation.setTextAnchor(TextAnchor.HALF_ASCENT_LEFT);
        plot.addAnnotation(annotation);

        
        JFreeChart chart = new JFreeChart(tytul, JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        chart.setBackgroundPaint(Color.white);

        // return a new chart containing the overlaid plot...
        return chart;
        
    }

    /**
     * Creates a sample dataset.
     *
     * @return Series 1.
     */
    public static XYDataset createDataset1(String podpis, double[][] dane, int krok) {
    	final XYSeriesCollection collection = new XYSeriesCollection();
        
    	
    	for (int i = 0; i < dane.length; i++) {
    		XYSeries series = new XYSeries(podpis+i);
    		
    		int suma = 0;
    		for (int j = 0; j < dane[i].length; j++) {
    			series.add(suma, dane[i][j]);
    			suma += krok;
			}
    		collection.addSeries(series);    	
		}
        
        return collection;
    }
}

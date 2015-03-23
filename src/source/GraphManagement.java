package source;



import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class GraphManagement extends JFrame{

	public GraphManagement(String title, ArrayList<Stats> s) {
		super(title);
		
		final XYDataset dataset = createDataset(s);
        final JFreeChart chart = createChart(dataset);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);
		// TODO Auto-generated constructor stub
        
        
		this.pack();
        RefineryUtilities.centerFrameOnScreen(this);
        this.setVisible(true);
	}


	
	
	public XYDataset createDataset(ArrayList<Stats> s)
	{
        XYSeriesCollection dataset = new XYSeriesCollection();
		int counter = 1;
		for(Stats t: s)
		{
			ArrayList<Integer> c = t.getCars();
			ArrayList<Double> r = t.getRenewables();
			XYSeries series = new XYSeries("" + counter);
			counter++;
			for(int i = 0; i < t.getCars().size(); i++)
			{
				Double d = 1.0;
				System.out.println("cars: " + c.get(i) + " energy: " + r.get(i));
				series.add(c.get(i), r.get(i));
				d = d + 1.0;
			}
			dataset.addSeries(series);
			
		}

        

		return dataset;
		
	}
	
	public JFreeChart createChart(XYDataset dataset)
	{
			
		JFreeChart chart = ChartFactory.createXYLineChart(
	            "Line Chart Demo 6",      // chart title
	            "X",                      // x axis label
	            "Y",                      // y axis label
	            dataset,                  // data
	            PlotOrientation.VERTICAL,
	            true,                     // include legend
	            true,                     // tooltips
	            false                     // urls
	        );
		
		
		
		XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setRange(0, 100);
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        
		return chart;
		
	}
	
	public void showGraph(GraphManagement graph)
	{ 

	}
	
}

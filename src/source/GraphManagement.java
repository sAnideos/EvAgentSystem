package source;



import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;

@SuppressWarnings("serial")
public class GraphManagement extends JFrame{

	public GraphManagement(String title) {
		super(title);
		

	}


	public void showMultiTestGraph(String title, ArrayList<Test> s)
	{
		final XYDataset dataset = createDataset(s, title);
        final JFreeChart chart = createChart(dataset, title);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);
		// TODO Auto-generated constructor stub
        
        
		this.pack();
        RefineryUtilities.centerFrameOnScreen(this);
        this.setVisible(true);
	}
	
	
	public void showSingleTestGraph(String title, Test stats, int actives[])
	{
		for(int i = 0; i < actives.length; i++)
		{
			XYSeriesCollection dataset = new XYSeriesCollection();
			if(actives[i] == 1)
			{
				XYSeries series = new XYSeries("Energy");
				for(Double e: stats.getTotal_energy())
				{
					//series.add(x, y);
				}
			}
		}
	}
	
	
	public XYDataset createDataset(ArrayList<Test> s, String title)
	{
        XYSeriesCollection dataset = new XYSeriesCollection();
		int counter = 1;

		for(Test t: s)
		{
			ArrayList<Double> c = t.getCars();
			ArrayList<Double> r = null;
			if(title.compareTo("Renewables") == 0)
			{
				r = t.getRenewables();
			}
			else if(title.compareTo("Energy") == 0)
			{
				r = t.getTotal_energy();
			}
			else if(title.compareTo("Non Renewables") == 0)
			{
				r = t.getNon_renewables();
			}
			else if(title.compareTo("Renewables/Total") == 0)
			{
				r = t.getRenewables_total();
			}
			else if(title.compareTo("Cars Charged") == 0)
			{
				r = t.getCars_charged();
			}
			else if(title.compareTo("Slots Used") == 0)
			{
				r = t.getSlots();
			}
			
				
			XYSeries series = new XYSeries(t.getName());
			counter++;
			for(int i = 0; i < t.getCars().size(); i++)
			{
				Double d = 1.0;
				series.add(c.get(i), r.get(i));
				d = d + 1.0;
			}
			dataset.addSeries(series);
			
		}

        

		return dataset;
		
	}
	
	public JFreeChart createChart(XYDataset dataset, String title)
	{
			
		JFreeChart chart = ChartFactory.createXYLineChart(
				title,      // chart title
	            "Cars",                      // x axis label
	            "Consumption",                      // y axis label
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
        rangeAxis.setRange(0, 110);
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        
		return chart;
		
	}
	
	public void showGraph(GraphManagement graph)
	{ 

	}
	
}

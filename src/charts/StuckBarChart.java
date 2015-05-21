package charts;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.SubCategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.GroupedStackedBarRenderer;
import org.jfree.data.KeyToGroupMap;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.StandardGradientPaintTransformer;

import source.Results;
import customDynamic.Slot;


@SuppressWarnings("serial")
public class StuckBarChart extends ApplicationFrame {


    public StuckBarChart(final String title, Results customResults, Results cplexResults, Results staticResults) {
        super(title);
        final CategoryDataset dataset = createDataset(customResults, cplexResults, staticResults);
        final JFreeChart chart = createChart(dataset);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(590, 350));
        setContentPane(chartPanel);
    }
    

    private CategoryDataset createDataset(Results customResults, Results cplexResults, Results staticResults) {
        DefaultCategoryDataset result = new DefaultCategoryDataset();


        
        class Element
        {
        	private Integer val;
        	private String att;
        	
        	public Element(Integer val, String att)
        	{
        		this.val = val;
        		this.att = att;
        	}

			public Integer getVal() {
				return val;
			}

			public String getAtt() {
				return att;
			}
        	
        }
        
    	class aComparator implements Comparator<Element> {
    		@Override
    		public int compare(Element d0, Element d1) {
    			
    			if (d0.getVal() > d1.getVal()) {
    				return 1;
    			} else if (d0.getVal() < d1.getVal()) {
    				return -1;
    			} else {
    				return 0;
    			}
    		}
    	}
        System.out.println("Cplex resutls: " + staticResults.getLoad()[9]);
        for(int i = 0; i < customResults.getLoad().length; i++)
        {
        	ArrayList<Element> sort = new ArrayList<Element>();
        	sort.add(new Element(customResults.getChargers(), "CD Chargers"));
        	sort.add(new Element(customResults.getLoad()[i], "CD Load"));
        	sort.add(new Element(customResults.getEnergy()[i][2], "CD Energy"));
        	Collections.sort(sort, new aComparator());
        	result.addValue(sort.get(0).getVal(), sort.get(0).getAtt(), i + ""); // CD stands for Custom Dynamic
        	result.addValue(sort.get(1).getVal() - sort.get(0).getVal(), sort.get(1).getAtt(), i + "");
        	result.addValue(sort.get(2).getVal() - sort.get(1).getVal(), sort.get(2).getAtt(), i + "");
        	
        	
        	sort = new ArrayList<Element>();
        	sort.add(new Element(cplexResults.getChargers(), "Cplex Chargers"));
        	sort.add(new Element(cplexResults.getLoad()[i], "Cplex Load"));
        	sort.add(new Element(cplexResults.getEnergy()[i][2], "Cplex Energy"));
        	Collections.sort(sort, new aComparator());
        	result.addValue(sort.get(0).getVal(), sort.get(0).getAtt(), i + ""); // CD stands for Custom Dynamic
        	result.addValue(sort.get(1).getVal() - sort.get(0).getVal(), sort.get(1).getAtt(), i + "");
        	result.addValue(sort.get(2).getVal() - sort.get(1).getVal(), sort.get(2).getAtt(), i + "");
        	
        	
        	sort = new ArrayList<Element>();
        	sort.add(new Element(staticResults.getChargers(), "Static Chargers"));
        	sort.add(new Element(staticResults.getLoad()[i], "Static Load"));
        	sort.add(new Element(staticResults.getEnergy()[i][2], "Static Energy"));
        	Collections.sort(sort, new aComparator());
        	result.addValue(sort.get(0).getVal(), sort.get(0).getAtt(), i + ""); // CD stands for Custom Dynamic
        	result.addValue(sort.get(1).getVal() - sort.get(0).getVal(), sort.get(1).getAtt(), i + "");
        	result.addValue(sort.get(2).getVal() - sort.get(1).getVal(), sort.get(2).getAtt(), i + "");
        	
        	//System.out.println(cplexLoad[0].length + " + " + customLoad[0].length);
        	
        }
        
        return result;
    }
    
    /**
     * Creates a sample chart.
     * 
     * @param dataset  the dataset for the chart.
     * 
     * @return A sample chart.
     */
    private JFreeChart createChart(final CategoryDataset dataset) {

        final JFreeChart chart = ChartFactory.createStackedBarChart(
            "Load Chart",  // chart title
            "Category",                  // domain axis label
            "Value",                     // range axis label
            dataset,                     // data
            PlotOrientation.VERTICAL,    // the plot orientation
            true,                        // legend
            true,                        // tooltips
            false                        // urls
        );
        
        GroupedStackedBarRenderer renderer = new GroupedStackedBarRenderer();
        KeyToGroupMap map = new KeyToGroupMap("G1");
        map.mapKeyToGroup("CD Chargers", "G1");
        map.mapKeyToGroup("CD Load", "G1");
        map.mapKeyToGroup("CD Energy", "G1");
        
        map.mapKeyToGroup("Cplex Chargers", "G2");
        map.mapKeyToGroup("Cplex Load", "G2");
        map.mapKeyToGroup("Cplex Energy", "G2");
        
        map.mapKeyToGroup("Static Chargers", "G3");
        map.mapKeyToGroup("Static Load", "G3");
        map.mapKeyToGroup("Static Energy", "G3");
        
//        map.mapKeyToGroup("Product 2 (US)", "G2");
//        map.mapKeyToGroup("Product 2 (Europe)", "G2");
//        map.mapKeyToGroup("Product 2 (Asia)", "G2");
//        map.mapKeyToGroup("Product 2 (Middle East)", "G2");
//        map.mapKeyToGroup("Product 3 (US)", "G3");
//        map.mapKeyToGroup("Product 3 (Europe)", "G3");
//        map.mapKeyToGroup("Product 3 (Asia)", "G3");
//        map.mapKeyToGroup("Product 3 (Middle East)", "G3");
        renderer.setSeriesToGroupMap(map); 
        
        renderer.setItemMargin(0.0);
        Paint p1 = new GradientPaint(
            0.0f, 0.0f, new Color(0x22, 0x22, 0xFF), 0.0f, 0.0f, new Color(0x88, 0x88, 0xFF)
        );
        renderer.setSeriesPaint(0, p1);
        renderer.setSeriesPaint(3, p1);
        renderer.setSeriesPaint(6, p1);
         
        Paint p2 = new GradientPaint(
            0.0f, 0.0f, new Color(0x22, 0xFF, 0x22), 0.0f, 0.0f, new Color(0x88, 0xFF, 0x88)
        );
        renderer.setSeriesPaint(1, p2); 
        renderer.setSeriesPaint(4, p2); 
        renderer.setSeriesPaint(7, p2); 
        
        Paint p3 = new GradientPaint(
            0.0f, 0.0f, new Color(0xFF, 0x22, 0x22), 0.0f, 0.0f, new Color(0xFF, 0x88, 0x88)
        );
        renderer.setSeriesPaint(2, p3);
        renderer.setSeriesPaint(5, p3);
        renderer.setSeriesPaint(8, p3);
            
        renderer.setGradientPaintTransformer(
            new StandardGradientPaintTransformer(GradientPaintTransformType.HORIZONTAL)
        );
        
        SubCategoryAxis domainAxis = new SubCategoryAxis("Algorithm / Slot");
        domainAxis.setCategoryMargin(0.3);
        //domainAxis.addSubCategory("Chargers");
       // domainAxis.addSubCategory("Load");
      //  domainAxis.addSubCategory("Energy");
        
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setDomainAxis(domainAxis);
        //plot.setDomainAxisLocation(AxisLocation.TOP_OR_RIGHT);
        plot.setRenderer(renderer);
        plot.setFixedLegendItems(createLegendItems());
        return chart;
        
    }

    /**
     * Creates the legend items for the chart.  In this case, we set them manually because we
     * only want legend items for a subset of the data series.
     * 
     * @return The legend items.
     */
    private LegendItemCollection createLegendItems() {
        LegendItemCollection result = new LegendItemCollection();
//        LegendItem item1 = new LegendItem("US", new Color(0x22, 0x22, 0xFF));
  //      LegendItem item2 = new LegendItem("Europe", new Color(0x22, 0xFF, 0x22));
    //    LegendItem item3 = new LegendItem("Asia", new Color(0xFF, 0x22, 0x22));
      //  LegendItem item4 = new LegendItem("Middle East", new Color(0xFF, 0xFF, 0x22));
//        result.add(item1);
  //      result.add(item2);
    //    result.add(item3);
      //  result.add(item4);
        return result;
    }
    
    public void drawBarChart() 
    {
        //final StuckBarChart demo = new StuckBarChart("Stacked Bar Chart Demo 4", customLoad);
        this.pack();
        RefineryUtilities.centerFrameOnScreen(this);
        this.setVisible(true);
    }

}
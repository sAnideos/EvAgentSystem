package source;

import charts.StuckBarChart;
import customDynamic.Dynamic;

public class MainClass {


	public static void main(String[] args) {
		
		StatsManagement sm = new StatsManagement();
		String path = "C:/Users/Andreas Sitaras/Desktop/Files/big_test2.txt";
		//Model m = new Model(path, null);
//		//Results cplexResults = m.realTimeRun(path);
//		Results r = m.staticRun(-1);
//		r.printMap();
//		System.out.println(r.toString());
//		Dynamic d = new Dynamic(path, null);
//		Test t = d.multiRun(2, 2);
//		t.setTestName("Custom");
//		sm.addStats(t);
//		System.out.println(t.toString());
//		Results r = m.realTimeRun(-1);
//		System.out.println(r.toString());
//		r.printMap();
		//Test t = m.multiRealTimeRun(2, 2);
		//System.out.println(t.toString());
		
////		
////		d.multiRun();
//		//Results cDyn = d.run(-1);
//		
//		Test test = m.multiRunsStatic(2, 2, path, null);
//		test.setTestName("Static");
//		sm.addStats(test);
//		
//		sm.showGraph("Energy");
//		sm.showGraph("Renewables");
//		sm.showGraph("Energy");
//		sm.showGraph("Non Renewables");
//		sm.showGraph("Renewables/Total");
//		sm.showGraph("Cars Charged");
//		sm.showGraph("Slots Used");
//		Results r = d.run(-1);
//		r.printMap();
//		System.out.println(r.toString());
		
		//System.out.println(test.toString());
		
		// read and run static algorithm
		//DataGenerator dt = new DataGenerator(0, 0, 0, 0);
		//dt.readFromFile(path);
		//Model model = new Model();
		//Results staticResults = model.createAndRunModel(dt.getCarsByStartTime(-1), dt.getTime_slots(), 
				//dt.getEnergy(), dt.getChargers(), dt.getRenewable_energy(), dt.getNon_renewable_energy(), 0.0, 1.0, 0.0, 0, -1);
		
//
//		
//		StuckBarChart chart = new StuckBarChart("Leave me alone", cResults, cplexResults, staticResults);
//		chart.drawBarChart();
//		
//		System.out.println("Custom Dynamic charged: " + cResults.getCharged() + ", Cplex Dynamic charged: "
//				+ "" + cplexResults.getCharged() + ", Static charged: " + staticResults.getCharged());
//		
//		System.out.println("Custom Dynamic used: " + cResults.getEnergyUsed() + ", Cplex Dynamic used: "
//				+ "" + cplexResults.getEnergyUsed() + ", Static used: " + staticResults.getEnergyUsed());
//		
//		
//		
//		System.out.println("Custom Dynamic map: ");
//		cResults.printMap();
//		
//		System.out.println("Cplex Dynamic map: ");
//		cplexResults.printMap();
//		
//		System.out.println("Static map: ");
//		staticResults.printMap();
		//System.out.println(staticResults.toString());
		//staticResults.printMap();
		//System.out.println(staticResults.toString());
		//staticResults.printEnergyMap();
		
	}

}
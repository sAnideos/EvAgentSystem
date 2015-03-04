package source;

public class Run {

	private DataGenerator dt;
	
	
	public Run()
	{
		dt = new DataGenerator(1000, 500, 250, 150);
		dt.generateCarData();
		dt.generateEnergyData();
		dt.generateDiverseEnergy();
		//dt.readFromFile();
	}
	
	public void start()
	{
		Model model = new Model();
		model.createAndRunModel(dt.getCars(), dt.getTime_slots(),
				dt.getEnergy(), dt.getChargers(), dt.getRenewable_energy(), dt.getNon_renewable_energy());
		
		/*
		// increases every vehicle parking time by 2
		System.out.println();
		System.out.println("Increasing parking time by 2 for every vehicle...");
		System.out.println("-------------------------------------------------------");
		for(int i = 0; i < 10; i++)
		{
			dt.increaseParkingTime(2, i);
		}
		
		model = new Model();
		model.createAndRunModel(dt.getCars(), dt.getTime_slots(),
				dt.getEnergy(), dt.getChargers(), dt.getD_energy());
				*/
		
	}
	
	
	
}

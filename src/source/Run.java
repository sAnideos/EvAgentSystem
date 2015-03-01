package source;

public class Run {

	private DataGenerator dt;
	
	//300, 150, 100, 75 me auta ta noumera -  auto me tin energeia sa metavliti apofashs - 0.11 secs to allo me energeia xwris diaxwrismo (palioteri ekdosi 3.49)
	public Run()
	{
		dt = new DataGenerator(500, 200, 150, 100);
		//dt.generateCarData();
		//dt.generateDiverseEnergy();
		//dt.generateEnergyData();
		dt.readFromFile();
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

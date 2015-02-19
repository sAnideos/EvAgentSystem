package source;

public class Run {

	private DataGenerator dt;
	
	
	public Run()
	{
		dt = new DataGenerator(10, 10, 5);
		dt.generateCarData();
		dt.generateEnergyData();
	}
	
	public void start()
	{
		Model model = new Model();
		model.createAndRunModel(dt.getCars(), dt.getTime_slots(),
				dt.getEnergy(), dt.getChargers());
	}
	
	
	
}

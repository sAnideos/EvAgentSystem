package source;

import java.util.ArrayList;
import java.util.Random;

public class DataGenerator {

	
	private int evs;
	private int time_slots;
	private int chargers;
	private ArrayList<Car> cars;
	private int[] energy;
	public int getTime_slots() {
		return time_slots;
	}

	public int getChargers() {
		return chargers;
	}

	private Random rand;
	
	
	public DataGenerator(int evs, int time_slots, int chargers)
	{
		this.evs = evs;
		this.time_slots = time_slots;
		this.chargers = chargers;
		cars = new ArrayList<Car>();
		rand = new Random();
	}
	
	public void generateCarData()
	{
		/*
		System.out.println("Vehicles: " + evs);
		System.out.println("Time Slots: " + time_slots);
		System.out.println("Chargers: " + chargers);
		*/
		for(int i = 0; i < evs; i++)
		{
			int n;
			Car car = new Car();
			
			n = rand.nextInt(time_slots - 1);
			car.setStartTime(n);
			
			n = rand.nextInt(time_slots - car.getStartTime()) + car.getStartTime();
			
			car.setEndTime(n);
			
			n = rand.nextInt((car.getEndTime() - car.getStartTime()) + 1) + 1;
			
			car.setNeeds(n);
			
			cars.add(car);
			/*
			System.out.print("Ev: " + (i + 1) + " Start time: " + car.getStartTime());
			System.out.println(" End time: " + car.getEndTime() + " Needs: " + car.getNeeds());
			*/
		}
	}
	
	public void generateEnergyData()
	{
		energy = new int[time_slots];
		for(int i = 0; i < time_slots; i++)
		{
			energy[i] = rand.nextInt(20) + 1;	
			//System.out.println(energy[i]);
		}
	}

	public ArrayList<Car> getCars() {
		return cars;
	}

	public int[] getEnergy() {
		return energy;
	}
	
	
	
	

	
	
	
	
	
	
}

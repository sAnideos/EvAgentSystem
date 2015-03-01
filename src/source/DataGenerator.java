package source;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public class DataGenerator {

	
	private int evs;
	private int time_slots;
	private int chargers;
	private ArrayList<Car> cars;
	private int[] energy;

	private int[] renewable_energy;
	private int[] non_renewable_energy;
	private int energy_range;
	
	

	public int getTime_slots() {
		return time_slots;
	}

	public int getChargers() {
		return chargers;
	}

	private Random rand;
	
	
	public DataGenerator(int evs, int time_slots, int chargers, int energy_range)
	{
		this.evs = evs;
		this.time_slots = time_slots;
		this.chargers = chargers;
		this.energy_range = energy_range;
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
			energy[i] = rand.nextInt(energy_range) + 1;	
			//System.out.println(energy[i]);
		}
	}
	

	public void generateDiverseEnergy()
	{
		renewable_energy = new int[time_slots];
		non_renewable_energy = new int[time_slots];
		
		for(int i = 0; i < time_slots; i++)
		{
			int random = rand.nextInt(energy_range) + 1;
			renewable_energy[i] = random;
			random = rand.nextInt(energy_range) + 1;
			non_renewable_energy[i] = random;
		}
		
	}
	


	public int[] getRenewable_energy() {
		return renewable_energy;
	}

	public int[] getNon_renewable_energy() {
		return non_renewable_energy;
	}


	// increases parking time for vehicle 'car' by 'inc'
	public void increaseParkingTime(int inc, int car)
	{
		int old_start = cars.get(car).getStartTime();
		int old_end = cars.get(car).getEndTime();
		int new_start = old_start;
		int new_end = old_end;
		
		
		if((old_end + inc) > (time_slots - 1))
		{
			int res = 0;
			new_end = time_slots - 1;
			res = old_end + inc - time_slots + 1;
			if((old_start - res) >= 0)
			{
				new_start = old_start - res;
			}
		}
		else
		{
			new_end = old_end + inc;
		}
		
		cars.get(car).setStartTime(new_start);
		cars.get(car).setEndTime(new_end);
	}

	public ArrayList<Car> getCars() {
		return cars;
	}

	public int[] getEnergy() {
		return energy;
	}
	
	
	// reads data from file
	/* file structure
	 * first line - #evs
	 * second line - #time_slots
	 * third line - #chargers
	 * then the triplets mean - (start time, end time, energy the ev needs)
	 * and the last lines of integers is the energy in each time slot
	 */
	public void readFromFile()
	{
		FileInputStream fstream = null;
		try {
			fstream = new FileInputStream("Data.txt");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		
		String line;
		
		try {
			line = br.readLine();
			evs = Integer.parseInt(line);
			line = br.readLine();
			time_slots =  Integer.parseInt(line);
			line = br.readLine();
			chargers = Integer.parseInt(line);
			
			energy = new int[time_slots];
			
			//System.out.println(time_slots + " " + evs + " " + chargers);
			
			for(int i = 0; i < evs; i++)
			{
				line = br.readLine();
				String[] temp = line.split(" ");
				int[] car_properties = new int[temp.length];
				for(int j = 0; j < temp.length; j ++)
				{
					car_properties[j] = Integer.parseInt(temp[j]);	
				}
				//System.out.println("");
				Car car = new Car();
				car.setStartTime(car_properties[0]);
				car.setEndTime(car_properties[1]);
				car.setNeeds(car_properties[2]);
				//System.out.println("Ev: " + i + " Starts at: " + car.getStartTime() + 
						//" ends at: " + car.getEndTime() + " needs: " + car.getNeeds());
				cars.add(car);
			}

			renewable_energy = new int[time_slots];
			non_renewable_energy = new int[time_slots];

			for(int i = 0; i < time_slots; i++)
			{
				line = br.readLine();

				String[] temp = line.split(" ");
				int[] energy_amount = new int[temp.length];
				for(int j = 0; j < temp.length; j ++)
				{
					energy_amount[j] = Integer.parseInt(temp[j]);
				}
				renewable_energy[i] = energy_amount[0];
				non_renewable_energy[i] = energy_amount[1];	
				System.out.print(renewable_energy[i] + ", ");
				System.out.println(non_renewable_energy[i]);

				//energy[i] = Integer.parseInt(line);
				//System.out.println(energy[i]);

			}
			fstream.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		
	}

	
	
	
	
	
	
}

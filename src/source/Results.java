package source;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class Results {

	
	private int[][] final_map; // shows when a car will charge
	private int energy_used; // percentage of the energy used
	private int[] load;
	private int chargers;
	private int[][] initial_energy; // 0 - renewables, 1 - non_renewables, 2 - all energy
	private int[] renewables_used;
	int ct;
	

	
	
	public Results(int[][] final_map, int energy_used, int chargers, int[][] energy, int[] ren_used)
	{
		this.final_map = final_map;
		this.energy_used = energy_used;
		this.chargers = chargers;
		this.initial_energy = energy;
		this.renewables_used = ren_used;
		ct = final_map[0].length - 4;
		computeLoad();
		if(energy_used == 0)
		{
			computeEnergyUsed();
		}
	}

	
	public void computeLoad()
	{
		int ct = final_map[0].length;
		load = new int[ct];
		for(int t = 0; t < ct; t++)
		{
			int load_counter = 0;
			for(int ev = 0; ev < final_map.length; ev++)
			{
				if(final_map[ev][t] == 1)
				load_counter++;
			}
			load[t] = load_counter;
		}
		
	}
	
	
	public void printMap()
	{
		for(int ev = 0; ev < final_map.length; ev++)
		{
			for(int t = 0; t < ct; t++)
			{
				System.out.print(final_map[ev][t] + " ");
			}
			StringBuilder str = new StringBuilder();
			str.append("Was available from: " + final_map[ev][ct] + " to ");
			str.append(final_map[ev][ct + 1] + " with needs: ");
			str.append(final_map[ev][ct + 2] + " min and " + final_map[ev][ct + 3] + " max.");
			System.out.println(str.toString());
		}
		System.out.println("-------------------");
		for(int t = 0; t < ct; t++)
		{
			System.out.print(initial_energy[t][0] + " ");
		}

		System.out.println("Renewables");


		for(int t = 0; t < ct; t++)
		{
			System.out.print(initial_energy[t][1] + " ");
		}
		System.out.println("Non Renewables");
		
		for(int t = 275; t < ct; t++)
		{
			System.out.print(load[t] + " ");
		}
		System.out.println("Load");
		
		for(int t = 0; t < ct; t++)
		{
			System.out.print(initial_energy[t][2] - load[t] + " ");
		}
		System.out.println("Remaining Energy");
		
		
	}

	
	
	private int energy_units;
	private int all_energy_units;
	private int ren_units;
	private int all_ren_units;
	private int non_ren_units;
	private int all_non_ren_units;
	private int ren_all_units;
	private int all_ren_all_units;
	private int cars_charged;	
	private int all_cars;
	private int slots_used;
	private int all_slots;
	
	
	public String slotsUsedString()
	{
		String str = "(" + slots_used + "/" + all_slots + ")";
		return str;
	}
	
	
	public String carsChargedString()
	{
		String str = "(" + cars_charged + "/" + all_cars + ")";
		return str;
	}
	
	
	public String renPerAllEnergyString()
	{
		String str = "(" + ren_all_units + "/" + all_ren_all_units + ")";
		return str;
	}
	
	public String nonRenEnergyUsedString()
	{
		String str = "(" + non_ren_units + "/" + all_non_ren_units + ")";
		return str;
	}
	
	public String renEnergyUsedString()
	{
		String str = "(" + ren_units + "/" + all_ren_units + ")";
		return str;
	}
	
	public String energyUsedString()
	{
		String str = "(" + energy_units + "/" + all_energy_units + ")";
		return str;
	}
	
	
	
	public int[][] getInitial_energy() {
		return initial_energy;
	}


	public int[] getRenewables_used() {
		return renewables_used;
	}


	public int getCt() {
		return ct;
	}


	public int getEnergy_units() {
		return energy_units;
	}


	public int getAll_energy_units() {
		return all_energy_units;
	}


	public int getRen_units() {
		return ren_units;
	}


	public int getAll_ren_units() {
		return all_ren_units;
	}


	public int getNon_ren_units() {
		return non_ren_units;
	}


	public int getAll_non_ren_units() {
		return all_non_ren_units;
	}


	public int getRen_all_units() {
		return ren_all_units;
	}


	public int getAll_ren_all_units() {
		return all_ren_all_units;
	}


	public int getCars_charged() {
		return cars_charged;
	}


	public int getAll_cars() {
		return all_cars;
	}


	public int getSlots_used() {
		return slots_used;
	}


	public int getAll_slots() {
		return all_slots;
	}


	public void computeEnergyUsed()
	{
		double used = 0;
		double all = 0; // all energy
		for(int t = 0; t < ct; t++)
		{
			used += load[t];
			all += initial_energy[t][2];
		}
		
		energy_used = (int)Math.round(((used / all)*100)) ;
	}

	
	public double getRenewablesUsedPercentage()
	{
		double percentage;
		double r_usage = 0.0;
		double all = 0.0;
		for(int t = 0; t < ct; t++)
		{
			r_usage += renewables_used[t];
			all += initial_energy[t][0];
		}
		percentage = ((r_usage / all)*100);
		
		ren_units = (int)r_usage;
		all_ren_units = (int)all;
		
		return round(percentage, 2);
	}
	
	public double getEnergyUsedPercentage()
	{
		double percentage;
		double usage = 0.0;
		double all = 0.0;
		for(int t = 0; t < ct; t++)
		{
			usage += load[t];
			all += initial_energy[t][2];
		}
		percentage = ((usage / all)*100);
		
		energy_units = (int)usage;
		all_energy_units = (int)all;
		
		return round(percentage, 2);
	}
	
	
	public double getNonRenewablesUsedPercentage()
	{
		double percentage;
		double r_usage = 0.0;
		double all = 0.0;
		for(int t = 0; t < ct; t++)
		{
			r_usage += (load[t] - renewables_used[t]);
			all += (initial_energy[t][2] - initial_energy[t][0]);
		}
		
		non_ren_units = (int)r_usage;
		all_non_ren_units = (int)all;
		
		
		if((r_usage == 0.0) && (all == 0.0))
		{
			return 0.0;
		}
		
		percentage = ((r_usage / all)*100);
		

		
		return round(percentage, 2);
	}
	
	
	public double getRenewablesPerAllPercentage()
	{
		double percentage;
		double r_usage = 0.0;
		double all = 0.0;
		for(int t = 0; t < ct; t++)
		{
			r_usage += renewables_used[t];
			all += (load[t]);
		}
		
		ren_all_units = (int)r_usage;
		all_ren_all_units = (int)all;
		
		
		if((r_usage == 0.0) && (all == 0.0))
		{
			return 0.0;
		}
		percentage = ((r_usage / all)*100);
		
		
		
		
		return round(percentage, 2);
	}
	
	
	public double getCarChargedPercentage()
	{
		double percentage;
		double all = final_map.length;
		double charged = 0.0;
		for(int ev = 0; ev < final_map.length; ev++)
		{
			for(int t = 0; t < ct; t++)
			{
				if(final_map[ev][t] == 1)
				{
					charged++;
					break;
				}
			}
		}
		percentage = ((charged / all)*100);
		
		cars_charged = (int)charged;
		all_cars = (int)all;
		
		return round(percentage, 2);
	}
	
	
	public double getSlotsUsedPercentage()
	{
		double percentage;
		double charged = 0.0;
		System.out.println(final_map.length + ", " + final_map[0].length);
		for(int ev = 0; ev < final_map.length; ev++)
		{
			for(int t = 0; t < ct; t++)
			{
				if(final_map[ev][t] == 1)
				{
					charged++;
				}
			}
		}
		
		double k = ((final_map[0].length - 4)*chargers);
		
		
		slots_used = (int)charged;
		all_slots = (int)k;
		
		
		if((charged == 0.0) && (k == 0.0))
		{
			return 0.0;
		}
		
		percentage = ((charged / k)*100);
		
		return round(percentage, 2);
	}
	
	
	public int[][] getFinal_map() {
		return final_map;
	}


	public int getEnergy_used() {
		return energy_used;
	}


	public int[] getLoad() {
		return load;
	}


	public int[][] getEnergy() {
		return initial_energy;
	}


	public int getChargers() {
		return chargers;
	}




	public int[][] getFinalMap() {
		return final_map;
	}


	public int getEnergyUsed() {
		return energy_used;
	}

	public void printEnergyMap()
	{
		for(int i = 0; i < initial_energy.length; i++)
		{
				System.out.println("Ren: " + initial_energy[i][0] + " + Non: " + initial_energy[i][1]);
				System.out.println("Used: " + renewables_used[i] + " ren");
				System.out.println("--");
		}
	}
	
	public String toString()
	{
		StringBuilder str = new StringBuilder();
		str.append("Renewables used: " + this.getRenewablesUsedPercentage() + "% \n");
		str.append("Energy used: " + this.getEnergyUsedPercentage() + "% \n");
		str.append("Non Renewables used: " + this.getNonRenewablesUsedPercentage() + "% \n");
		str.append("Renewables/All used: " + this.getRenewablesPerAllPercentage() + "% \n");
		str.append("Cars charged: " + this.getCarChargedPercentage() + "% \n");
		str.append("Slots Used: " + this.getSlotsUsedPercentage() + "% \n");
		
		return str.toString();
	}
	
	

	public HashMap<Integer, ArrayList<Integer>> getSlotToCar()
	{
		HashMap<Integer, ArrayList<Integer>> slot_to_car = new HashMap<Integer, ArrayList<Integer>>();;
		
		
		for(int ev = 0; ev < final_map.length; ev++)
		{
			for(int t = 0; t < ct; t++)
			{
				if(final_map[ev][t] == 1)
				{
					if(slot_to_car.get(t) == null)
					{
						ArrayList<Integer> temp = new ArrayList<Integer>();
						temp.add(ev);
						slot_to_car.put(t, temp);
					}
					else
					{
						slot_to_car.get(t).add(ev);
					}
				}
			}
		}
		
		return slot_to_car;
	}
	
	
	public ArrayList<String> getCarToSlot()
	{
		ArrayList<String> car_to_slot = new ArrayList<String>();
		
		for(int ev = 0; ev < final_map.length; ev++)
		{
			StringBuilder list = new StringBuilder();
			for(int t = 0; t < ct; t++)
			{
				if(final_map[ev][t] == 1)
				{
					list.append(t + ", ");
				}
			}

			if(list.length() == 0)
			{
				car_to_slot.add("Didn't charge!");
			}
			else
			{
				car_to_slot.add(list.toString().substring(0, list.toString().length() - 2));
			}
		}
		
		return car_to_slot;
	}
	
	
	public String getCarInfo(int ev)
	{
		StringBuilder str = new StringBuilder();
		str.append("Was available from: " + final_map[ev][ct] + " to ");
		str.append(final_map[ev][ct + 1] + " with needs: ");
		str.append(final_map[ev][ct + 2] + " min and " + final_map[ev][ct + 3] + " max.");
		return str.toString();
	}
	
	// round a double number with the desired precision
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	
}

package source;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

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
		
		for(int t = 0; t < ct; t++)
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
		
		percentage = ((r_usage / all)*100);
		
		
		return round(percentage, 2);
	}
	
	
	public double getCarChargedPercentage()
	{
		double percentage;
		double all_cars = final_map.length;
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
		percentage = ((charged / all_cars)*100);
		
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
		percentage = ((charged / ((final_map[0].length - 4)*chargers))*100);
		
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
	
	
	// round a double number with the desired precision
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	
}

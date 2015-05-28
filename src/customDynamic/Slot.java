package customDynamic;

import java.util.ArrayList;

public class Slot {

	private int slot; // the slot's id
	//private int initial_energy;
	private int[] initial_energy; // array that holds information about energy: 0 - ren, 1 - non ren, 2 - all
	private int[] energy; // array that holds information about energy: 0 - ren, 1 - non ren, 2 - all, to be edited


	private ArrayList<Integer> cars_charging;
	private int load;
	private double score;
	
	
	public Slot(int ren_energy, int non_ren_energy, int slot)
	{
		
		initial_energy = new int[3];
		initial_energy[0] = ren_energy;
		initial_energy[1] = non_ren_energy;
		initial_energy[2] = initial_energy[0] + initial_energy[1];
		
		energy = new int[3];
		energy[0] = ren_energy;
		energy[1] = non_ren_energy;
		energy[2] = energy[0] + energy[1];
		
		this.slot = slot;
		this.load = 0;
		cars_charging = new ArrayList<Integer>();
	}
	
	public int getInitialEnergy() {
		return initial_energy[2];
	}
	
	public int getInitialRenEnergy() {
		return initial_energy[0];
	}

	public int getInitialNonRenEnergy()
	{
		return initial_energy[1];
	}

	public void addCar(int c)
	{
		cars_charging.add(c);
	}
	
	public int getLoad()
	{
		return load;
	}
	
	public int getSlot() {
		return slot;
	}
	
	public int getAllEnergy()
	{
		return energy[2];
	}

	public int updateLoadAndEnergy()
	{
		load++;
		energy[2]--;
		if(energy[0] > 0)
		{
			energy[0]--;
			return 0;
		}
		else
		{
			energy[1]--;
			return 1;
		}
	}
	
	public void resetSlot(int i)
	{
		load--;
		energy[2]++;
		energy[i]++;
	}
	
	public double getScore(double w1, double w2, double w3, int chargers)
	{
		double load_score = (double)(chargers - load);
		double ren_energy_score = (double)(energy[2]);
		score = w1 * load_score + w2 * ren_energy_score;
		return score;
		//return 1.0;
	}
	
	public boolean available(int chargers)
	{
		if(load >= chargers || energy[2] <= 0)
		{
			return false;
		}
		return true;
	}
}

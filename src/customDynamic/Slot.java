package customDynamic;

import java.util.ArrayList;

public class Slot {

	private int slot; // the slot's id
	private int ren_energy;
	private int non_ren_energy;
	private int all_energy;
	private int initial_energy;


	private ArrayList<Integer> cars_charging;
	private int load;
	
	
	public Slot(int ren_energy, int non_ren_energy, int slot)
	{
		this.ren_energy = ren_energy;
		this.non_ren_energy = non_ren_energy;
		this.all_energy = ren_energy + non_ren_energy;
		this.initial_energy = this.all_energy;
		this.slot = slot;
		this.load = 0;
		cars_charging = new ArrayList<Integer>();
	}
	
	public int getInitialEnergy() {
		return initial_energy;
	}
	
	public int getRenEnergy() {
		return ren_energy;
	}

	public void setRenEnergy(int ren_energy) {
		this.ren_energy = ren_energy;
	}

	public int getNonRenEnergy() {
		return non_ren_energy;
	}

	public void setNonRenEnergy(int non_ren_energy) {
		this.non_ren_energy = non_ren_energy;
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

	public void updateLoadAndEnergy()
	{
		load++;
		all_energy--;
	}
	
	public boolean available(int chargers)
	{
		if(load >= chargers || all_energy < 0)
		{
			return false;
		}
		return true;
	}
}

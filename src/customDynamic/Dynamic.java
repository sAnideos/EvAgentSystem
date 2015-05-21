package customDynamic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import charts.MatlabCode;
import source.Car;
import source.DataGenerator;
import source.Results;
import source.Test;

public class Dynamic {
	
	
	private ArrayList<Car> evs; // evs sorted by start time
	private Slot[] slots; // array of slots
	private int ct; // number of slots
	private int chargers; // number of chargers
	private int charged;
	private int[][] energy_map;
	private int[] renewables_used; // renewables used in every slot, to use it in the results
	private int[][] final_map;
	private DataGenerator dt;
	private String path;
	
	public Dynamic(String path)
	{
		this.path = path;
		this.resetData();
	}
	
	
	private void resetData()
	{
		dt = new DataGenerator(0, 0, 0, 0);
		dt.readFromFile(path);
		
		ct = dt.getTime_slots();
		chargers = dt.getChargers();
		charged = 0;
		
		slots = new Slot[ct];
		
		energy_map = new int[dt.getTime_slots()][3];
		renewables_used = new int[dt.getTime_slots()];

		for(int i = 0; i < dt.getTime_slots(); i++)
		{
			energy_map[i][0] = dt.getRenewable_energy()[i];
			energy_map[i][1] = dt.getNon_renewable_energy()[i];
			energy_map[i][2] = dt.getRenewable_energy()[i] + dt.getNon_renewable_energy()[i];
			renewables_used[i] = 0;
		}
		
		
		for(int i = 0; i < slots.length; i++)
		{
			slots[i] = new Slot(energy_map[i][0], energy_map[i][1], i);
		}

	}
	
	public Test multiRun(int start, int rate)
	{
		int count = 0;
		Test test = new Test(); // to save the data of multi runs
		while(start <= dt.getCarsNum())
		{
			count++;
			this.resetData();
			
			Results results = this.run(start);
			test.addCars((double)start);
			test.addCars_charged(results.getCarChargedPercentage());
			test.addTotal_energy(results.getEnergyUsedPercentage());
			test.addRenewables(results.getRenewablesUsedPercentage());
			test.addRenewables_total(results.getRenewablesPerAllPercentage());
			test.addNon_renewables(results.getNonRenewablesUsedPercentage());
			test.addSlots(results.getSlotsUsedPercentage());
			System.out.println(results.toString());
			
			if((start + rate) >= dt.getCarsNum())
			{
				count++;
				this.resetData();
				start = dt.getCarsNum();
				
				results = this.run(start);
				
				test.addCars((double)start);
				test.addCars_charged(results.getCarChargedPercentage());
				test.addTotal_energy(results.getEnergyUsedPercentage());
				test.addRenewables(results.getRenewablesUsedPercentage());
				test.addRenewables_total(results.getRenewablesPerAllPercentage());
				test.addNon_renewables(results.getNonRenewablesUsedPercentage());
				test.addSlots(results.getSlotsUsedPercentage());
				System.out.println(results.toString());
				
				break;
			}
			else
			{
				start += rate;
			}
			
			
			
		}
		System.out.println("Count " + count);
		return test;
	}
	
	
	public Results run(int cars_num)
	{
		

		evs = dt.getCarsByStartTime(cars_num);

		
		ArrayList<Car> currentCars = new ArrayList<Car>();

		// for each slot, check incoming cars and put them in a good position
		for(int t = 0; t < ct; t++)
		{
			for(Car ev: evs)
			{
				if(ev.getStartTime() == t)
				{
					currentCars.add(ev);
				}
				else if (ev.getStartTime() > t)
				{
					break;
				}
			}
		}
			evs = new ArrayList<Car>();
			// for every car now in the station find some slots for it to charge
			int counter = 0;
			for(Car ev: currentCars)
			{
				int start_time = ev.getStartTime();
				int end_time = ev.getEndTime() + 1;
				//int needs = ev.getNeeds();
				
//				if(ev.getNeeds() > 15)
//				{
//					counter++;
//					evs.add(ev);
//					continue;
//				}
				
				ArrayList<Slot> available_slots = new ArrayList<Slot>(); // contains the slots that the car is available to charge, it is going to be sorted
																		// to find the slots with the less load
				
				for(int i = start_time; i < end_time; i++)
				{
					available_slots.add(slots[i]);
				}
				Collections.sort(available_slots, new LoadComparator());
				int ren_used = 0; // it is used to reset the slots back to normal
				for(int i = 0; i < available_slots.size(); i++)
				{
					Slot temp_slot = available_slots.get(i);
					if(temp_slot.available(chargers))
					{
						int s = temp_slot.getSlot(); //  timeslot id
						int type = slots[s].updateLoadAndEnergy(); // type of energy used = 0 - renewable, 1 - non_renewable
						if(type == 0)
						{
							ren_used ++;
							renewables_used[s]++;
						}
						//slots[s].addCar(1);
						ev.addSlot(temp_slot.getSlot());
						ev.updateNeeds();
					}
					if(ev.getNeeds() == 0)
					{
						charged++;
						break;
					}
				}
				if(ev.getNeeds() > 0) // an den exei fortisei plirws midenizetai i lista me ta midenika tou
				{
					for(Integer s: ev.getSlots()) // episis gyrizoun pisw stis proigoumenes times tous ta slots pou xrisimopoiise to oxima pou de fortise
					{
						if(ren_used > 0)
						{
							slots[s].resetSlot(0);
							renewables_used[s]--;
							ren_used--;
						}
						else
						{
							slots[s].resetSlot(1);
						}
					}
					ev.resetSlots();
				}
				evs.add(ev);
				
			}
			
			
			
			
		final_map = new int[evs.size()][ct + 4];
		
		for(int ev = 0; ev < evs.size(); ev++)
		{
			int temp = ct;
			Car temp_car = evs.get(ev);
			final_map[ev][temp] = temp_car.getInitial_start_time();
			temp++;
			final_map[ev][temp] = temp_car.getEndTime();
			temp++;
			final_map[ev][temp] = temp_car.getInitialMinNeeds();
			temp++;
			final_map[ev][temp] = temp_car.getInitial_needs();
		}
		for(int i = 0; i < evs.size(); i++)
		{
			Car ev = evs.get(i);
			for(int t = 0; t < ct; t++)
			{
				if(ev.getSlots().contains(t))
				{
					final_map[i][t] = 1;
				}
				else
				{
					final_map[i][t] = 0;
				}
			}
		}
		
		
		int[] energy = new int[ct];

		for(int t = 0; t < ct; t++)
		{
			energy[t] = slots[t].getInitialEnergy();
		}

		Results results = new Results(final_map, 0, chargers, energy_map, renewables_used);
		
//		for(Car ev: evs)
//		{
//
//			for(Integer d: ev.getSlots())
//			{
//				System.out.print(d + " ");
//			}
//			System.out.println("\n");
//		}
		
		
		MatlabCode mtlb = new MatlabCode();
		mtlb.produceCode(final_map);
		
		
		return results;
		
	}
	
	// compare by load
//	public class LoadComparator implements Comparator<Slot> {
//		@Override
//		public int compare(Slot d0, Slot d1) {
//			if (d0.getLoad() > d1.getLoad()) {
//				return 1;
//			} else if (d0.getLoad() < d1.getLoad()) {
//				return -1;
//			} else {
//				return 0;
//			}
//		}
//	}
	
	//compare by slot
	public class LoadComparator implements Comparator<Slot> {
		@Override
		public int compare(Slot d0, Slot d1) {
			
			double w1 = 0.0, w2 = 1.0 - w1;
			if (d0.getScore(w1, w2, 0.0, chargers) > d1.getScore(w1, w2, 0.0, chargers)) {
				return -1;
			} else if (d0.getScore(w1, w2, 0.0, chargers) < d1.getScore(w1, w2, 0.0, chargers)) {
				return 1;
			} else {
				return 0;
			}
		}
	}
	
}

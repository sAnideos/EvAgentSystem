package customDynamic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import source.Car;
import source.DataGenerator;

public class Dynamic {
	
	
	private ArrayList<Car> evs; // evs sorted by start time
	private Slot[] slots; // array of slots
	private int ct; // number of slots
	private int chargers; // number of chargers
	private int charged;
	
	public Dynamic()
	{
		DataGenerator dt = new DataGenerator(0, 0, 0, 0);
		dt.readFromFile("C:/Users/Andreas Sitaras/Desktop/big_test2.txt");
		
		ct = dt.getTime_slots();
		chargers = dt.getChargers();
		charged = 0;
		
		slots = new Slot[ct];
		
		int[] renewables = dt.getRenewable_energy();
		int[] non_renewables = dt.getNon_renewable_energy();
		
		for(int i = 0; i < slots.length; i++)
		{
			slots[i] = new Slot(renewables[i], non_renewables[i], i);
		}
		
		evs = dt.getCarsByStartTime();
		
	}
	
	
	public void run()
	{
		
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
			for(Car ev: currentCars)
			{
				int start_time = ev.getStartTime();
				int end_time = ev.getEndTime() + 1;
				//int needs = ev.getNeeds();
				
				ArrayList<Slot> available_slots = new ArrayList<Slot>(); // contains the slots that the car is available to charge, it is going to be sorted
																		// to find the slots with the less load
				
				for(int i = start_time; i < end_time; i++)
				{
					available_slots.add(slots[i]);
				}

				Collections.sort(available_slots, new LoadComparator());
				
				for(int i = 0; i < end_time - start_time; i++)
				{
					Slot temp_slot = available_slots.get(i);
					if(temp_slot.available(chargers))
					{
						int s = temp_slot.getSlot(); //  timeslot id
						slots[s].updateLoadAndEnergy();
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
				evs.add(ev);
				
			}
			
			
		for(Car ev: evs)
		{
			for(int t = 0; t < ct; t++)
			{
				if(ev.getSlots().contains(t))
				{
					System.out.print("O ");
				}
				else
				{
					System.out.print("X ");
				}
			}
			System.out.println(ev.toString());
		}
		for(int t = 0; t < ct; t++)
		{
			System.out.print(slots[t].getInitialEnergy() + " ");
		}
		
		System.out.println();
		System.out.println("----------------------------");
		System.out.println("Stats: ");
		
		charged = Math.round((charged / (float) currentCars.size()) * 100);
		System.out.println("Charged: " + charged + "%");
		System.out.println("----------------------------");

		
//		for(Car ev: evs)
//		{
//
//			for(Integer d: ev.getSlots())
//			{
//				System.out.print(d + " ");
//			}
//			System.out.println("\n");
//		}
		
	}
	
	
	public class LoadComparator implements Comparator<Slot> {
		@Override
		public int compare(Slot d0, Slot d1) {
			if (d0.getLoad() > d1.getLoad()) {
				return 1;
			} else if (d0.getLoad() < d1.getLoad()) {
				return -1;
			} else {
				return 0;
			}
		}
	}
	
}

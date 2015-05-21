package source;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.UnknownObjectException;

//isws constructoras kai meta start gia na min ftiaxnw nees metavlites gia na epistrefw ta ArrayList ktl
public class Model {

	private int renewable_used = 0;
	private int non_renewable_used = 0;
	private int energy_used = 0;
	private int renewable_all_used = 0;
	private int charged = 0;
	private int slots_used = 0;
	private float used_r = 0; //renewable
	private float used_n = 0; //non renewable
	private float all_ren = 0;
	private float all_non = 0;
	//private HashMap<Integer, Car> car_to_slot = new HashMap<Integer, Car>();
	private ArrayList<Car> car_to_slot;
	private HashMap<Integer, ArrayList<Integer>> slot_to_car = new HashMap<Integer, ArrayList<Integer>>();
	private ArrayList<Integer> who_charge = new ArrayList<Integer>(); // krataei apo to charges poioi tha fortisoun, an enas ksekinise na fortizei tote tha fortisei sigoura
	private int[][] final_map;
	private int[] renewables_used;
	
	
	// to make them accessible from all the methods
	private IloNumVar[][] var; // decision variables' arrays
	private IloNumVar[] charges;
	private IloNumVar[][] ren_energy;
	private IloNumVar[][] non_ren_energy;
	private IloCplex cp;
	
	
	
	
	public void createAndRunModel(ArrayList<Car> evs, int ct, int[] energy, int chargers, int[] renewable_energy, int[] non_renewable_energy, double w1, double w2, double w3, int first_slot, int carsNumber)
	{
		
		System.out.println("The weights: " + w1 + ", " + w2 + ", " + w3);
		try {


			cp = new IloCplex(); // create the model
			
			if(first_slot == 0)
			{
				if(carsNumber == -1)
				{
					var = new IloNumVar[evs.size()][ct]; // decision variables' arrays
					charges = new IloNumVar[evs.size()];
					ren_energy = new IloNumVar[ct][];
					non_ren_energy = new IloNumVar[ct][];
					final_map = new int[evs.size()][ct + 4]; // +4 ... for the car info - start - end - min - max needs
				}
				else
				{
					var = new IloNumVar[carsNumber][ct]; // decision variables' arrays
					charges = new IloNumVar[carsNumber];
					ren_energy = new IloNumVar[ct][];
					non_ren_energy = new IloNumVar[ct][];
				}
				renewables_used = new int[ct];
			}
			
			
			for(int i = 0; i < evs.size(); i++)
			{
				evs.get(i).resetSlots(); // reseting slots that a car used in previous run

				for(int j = first_slot; j < ct; j++)
				{
					//System.out.println(i + ", " + j);
					var[i][j] = cp.boolVar("var(" + i + ", " + j + ")"); // creating boolean decision variables and giving them name
				}
				charges[i] = cp.boolVar("c(" + i + ")");
			}
			
			// 1)
			for(int ev = 0; ev < evs.size(); ev++)
			{

				int slots_need = evs.get(ev).getNeeds();
				//System.out.println(slots_need);
				IloLinearNumExpr p = cp.linearNumExpr(); // linear expression for the constraint
				
				for(int t = first_slot; t < ct; t++) // the time that the ev is available for charging
				{
						p.addTerm(1, var[ev][t]);
				}
				//System.out.println(p);
				cp.addLe(p, cp.prod(charges[ev], slots_need), "needs max");
				cp.addLe(cp.prod(charges[ev], evs.get(ev).getMinNeeds()), p, "needs min");
				//cp.addLe(p, 3);
				// for the github

			}

			
			
			for(int t = first_slot; t < ct; t++)
			{
				ren_energy[t] = new IloNumVar[renewable_energy[t]];

				for(int i = 0; i < renewable_energy[t]; i++)
				{
					ren_energy[t][i] = cp.boolVar("ren(" + t + ", " + i + ")");
				}
				
				non_ren_energy[t] = new IloNumVar[non_renewable_energy[t]];
				
				for(int i = 0; i < non_renewable_energy[t]; i++)
				{
					non_ren_energy[t][i] = cp.boolVar("non(" + t + ", " + i + ")");
				}
			}
			
			// 2) 
			for(int t = first_slot; t < ct; t++)
			{
				IloLinearNumExpr cars = cp.linearNumExpr();
				IloLinearNumExpr energy_ = cp.linearNumExpr();
				for(int ev = 0; ev < evs.size(); ev++)
				{
					int start = evs.get(ev).getStartTime();
					int end = evs.get(ev).getEndTime();
					if(t >= start && t <= end)
					{
						cars.addTerm(1, var[ev][t]);
					}
					else
					{
						cp.addEq(var[ev][t], 0.0);
					}
				}
				for(int en = 0; en < renewable_energy[t]; en++)
				{
					energy_.addTerm(1, ren_energy[t][en]);
				}
				for(int en = 0; en < non_renewable_energy[t]; en++)
				{
					energy_.addTerm(1, non_ren_energy[t][en]);
				}
				
				cp.addEq(energy_, cars, "energy = cars");
			}
			
			// 3)
			for(int t = first_slot; t < ct; t++) // the sum of evs that charge in a time slot must not exceed the num of slots
			{
				IloLinearNumExpr p = cp.linearNumExpr();
				
				for(int e = 0; e < evs.size(); e++)
				{
					//int start = evs.get(e).getStartTime();
					//int end = evs.get(e).getEndTime() + 1;
					//if(t >= start && t <= end)
					//{
						p.addTerm(1, var[e][t]);
					//}
				}
				
				cp.addLe(p, chargers, "chargers");
			}
			

			
			// 4)
			for(int t = first_slot; t < ct; t++) // energy constraint, the evs must not consume more than the available energy in the slot
			{						
				IloLinearNumExpr en = cp.linearNumExpr(); // the expression that need to be maximized, in this case, maximize
															// the total number of evs charging in all time slots
				
				for(int e = 0; e < evs.size(); e++)			 
				{

						en.addTerm(1, var[e][t]); // evs consume 2 energy units (not real number)

				}

				cp.addLe(en, renewable_energy[t] + non_renewable_energy[t], "available energy"); // energy used in a time slot must not exceed the available energy
			}
			
			// 5)
			for(Integer ev: who_charge)
			{
				cp.addEq(charges[ev], 1.0);
			}


			// ATIKEIMENIKES SYNARTISEIS
			System.out.println(w1 + ", " + w2 + ", " + w3);
			
			// 1)
			// antikeimeniki synartisi, megistpopoiisi asswn
			
			double all_energy = 0.0;
			for(int i = 0; i < ct; i++)
			{
				all_energy += renewable_energy[i];
				all_energy += non_renewable_energy[i];
			}
			double normalized_w = 100.0 / ((evs.size() * ct) + all_energy + evs.size());
			
			IloLinearNumExpr p_charges = cp.linearNumExpr();
			for(int ev = 0; ev < evs.size(); ev ++)
			{
				int start = evs.get(ev).getStartTime();
				int end = evs.get(ev).getEndTime() + 1;
				for(int time = start; time < end; time++)
				{
					p_charges.addTerm(normalized_w * w1, var[ev][time]);
				}
			}
			//normalized_w = 1.0 / evs.size();
			for(int ev = 0; ev < evs.size(); ev++)
			{
				p_charges.addTerm(normalized_w * w2, charges[ev]);
			}
			
			

			
			
			
			boolean stupid = false; // true gia to statiko

			//normalized_w = 1.0 / all_energy;
			IloLinearNumExpr p_energy = cp.linearNumExpr();
			if(stupid) // me to kolpo pou meiwnei tin aksia twn ananewsimwn
			{
			
				// na xrisimopoiei perissotero ananewsimes

				// gia to statiko
				for(int t = first_slot; t < ct; t++)
				{
					for(int en = 0; en < renewable_energy[t]; en++)
					{
						p_energy.addTerm(normalized_w * w3, ren_energy[t][en]);
					}
					for(int en = 0; en < non_renewable_energy[t]; en++)
					{
						p_energy.addTerm(normalized_w * w3, non_ren_energy[t][en]);
					}
					
				}
			}
			else // to kanoniko xwris to kolpo
			{
				
				
				
				class CarComp implements Comparator<Car>{
					 
				    @Override
				    public int compare(Car e1, Car e2) {
				        return e1.getEndTime() - (e2.getEndTime());
				    }
				}
				int last_car;
				if(evs.size() > 1)
				{
			        Car last = Collections.max(evs, new CarComp());
			        System.out.println("Employee with max salary: "+ last.getEndTime());
			        last_car = last.getEndTime();
				}
				else if(evs.size() >= 1)
				{
					last_car = evs.get(0).getEndTime();
				}
				else
				{
					last_car = 1;
				}
				
				double factor = 0.9;
				
				double r = ((first_slot + ct) * 0.5) - first_slot;
				double rate = 0.6 / ct;
				for(int t = first_slot; t < ct; t++)
				{
//					if(renewable_energy[t] < 0.3 * non_renewable_energy[t])
//					{
//						factor = 0.5;
//					}
					

					for(int en = 0; en < renewable_energy[t]; en++)
					{
						p_energy.addTerm((  normalized_w) * (w3 * factor), ren_energy[t][en]);
					}
					for(int en = 0; en < non_renewable_energy[t]; en++)
					{
						p_energy.addTerm((  normalized_w) * (w3 *  (1.0 - factor)), non_ren_energy[t][en]);
					}
//					if(t >= (first_slot + last_car) * 0.5)
//					{
//						System.out.println("This is the t: " + t + " and the ct: " + last_car);
//						factor = 0.5;
//					}
//					else
//					{
//						factor -= rate;
//					}
//					if(factor - rate > 0)
//						factor -= rate;
				}
			}
			// megistopoiisi oximnatwn pou fortizoun
			/*
			IloLinearNumExpr p_charges = cp.linearNumExpr();

			*/
			cp.addMaximize(cp.sum(p_charges, p_energy));
			

			
			if(cp.solve()) // solve the maximization problem and print the results
			{
				if(carsNumber == -1)
				{
					computeResults(cp, evs, ct, energy, chargers, renewable_energy, non_renewable_energy, first_slot);
				
				

				}
				else
				{

					for(int ev = 0; ev < evs.size(); ev++)
					{
						if(cp.getValue(var[ev][first_slot]) == 1.0)
						{
							final_map[ev][first_slot] = 1;
							evs.get(ev).updateNeeds();
							if(!who_charge.contains(ev))
								who_charge.add(ev);
						}
					}
					//who_charge = new ArrayList<Integer>();
					
					for(int en = 0; en < renewable_energy[first_slot]; en++)
					{		
						if(cp.getValue(ren_energy[first_slot][en]) == 1.0)
						{
							renewables_used[first_slot]++;
						}
					}
					
				}
			}

			

			} catch (IloException e) {
			System.err.println("Concert exception caught: " + e);
		}
		
	}

	
	public void computeResults(IloCplex cp, ArrayList<Car> evs, int ct, int[] energy, int chargers, int[] renewable_energy, int[] non_renewable_energy, int first_slot) throws UnknownObjectException, IloException
	{

		final_map = new int[evs.size()][ct + 4];
		
		
		for(int ev = 0; ev < evs.size(); ev++)
		{
			int temp = ct;
			Car temp_car = evs.get(ev);
			final_map[ev][temp] = temp_car.getInitial_start_time();
			temp++;
			final_map[ev][temp] = temp_car.getEndTime();
			temp++;
			final_map[ev][temp] = temp_car.getMinNeeds();
			temp++;
			final_map[ev][temp] = temp_car.getInitial_needs();
		}
		
		
		for(int ev = 0; ev < evs.size(); ev++)
		{
//			if(cp.getValue(var[ev][first_slot]) == 1.0)
//			{
//				final_map[ev][first_slot] = 1;
//				evs.get(ev).updateNeeds();
//			}

			for(int t = first_slot; t < ct; t++)
			{
				if(cp.getValue(var[ev][t]) == 1.0)
				{
					evs.get(ev).addSlot(t);
					// add to slot list
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
					
					final_map[ev][t] = 1;
				}
				else
				{
					final_map[ev][t] = 0;
				}
					
			}
		}
		
		who_charge = new ArrayList<Integer>();
		for(int ev = 0; ev < evs.size(); ev++)
		{
			if(cp.getValue(charges[ev]) == 1.0)
			{
				charged ++;
				who_charge.add(ev);
			}
		}
		

		for(int i = first_slot; i < ct; i++)
		{
			for(int en = 0; en < renewable_energy[i]; en++)
			{		
				if(cp.getValue(ren_energy[i][en]) == 1.0)
				{
					renewables_used[i]++;
				}
			}
		}
    	car_to_slot = evs;
    	
	}
	
	
	public Results realTimeRun(String path)
	{
		DataGenerator dt = new DataGenerator(0, 0, 0, 0);
		dt.readFromFile(path);
		//dt.readFromFile("F:/Eclipse Workshop/EvAgentSystem/temp_test.txt");

		ArrayList<Car> cars = dt.getCarsByStartTime(-1);
		
		
		final_map = new int[dt.getCarsNum()][dt.getTime_slots() + 4];
		
		for(int ev = 0; ev < cars.size(); ev++)
		{
			final_map[ev][dt.getTime_slots()] = cars.get(ev).getInitial_start_time();
			final_map[ev][dt.getTime_slots() + 1] = cars.get(ev).getEndTime();
			final_map[ev][dt.getTime_slots() + 2] = cars.get(ev).getInitialMinNeeds();
			final_map[ev][dt.getTime_slots() + 3] = cars.get(ev).getInitial_needs();
		}

		ArrayList<Car> currentCars = new ArrayList<Car>();
		int list_counter = 0;
		
		for(int slot = 0; slot < dt.getTime_slots(); slot++)
		{
			System.out.println("Slot: " + slot);
			if(list_counter < cars.size())
			{
				while(cars.get(list_counter).getStartTime() == slot)
				{
//					if(cars.get(list_counter).getInitial_needs() <= 15)
//					{
						currentCars.add(cars.get(list_counter));
//					}
					list_counter ++;
					if(list_counter == cars.size())
					{
						break;
					}
					System.out.println("Counter: " + list_counter);
				}
			}
			
			// updating current cars - going to be a function
			

//			int start = 10;
//			int rate = 5;
//			while(start <= dt.getCarsNum())
//			{
//		    	  	
//				System.out.println(dt.getCarsNum() + ", " + dt.getTime_slots()); 
//				createAndRunModel(dt.getCars(start), dt.getTime_slots(), dt.getEnergy(), 
//						dt.getChargers(), dt.getRenewable_energy(), dt.getNon_renewable_energy(), 0.0, 1.0, 0.0, slot, dt.getCarsNum());
//				
//				if((start + rate) >= dt.getCarsNum())
//				{
//					start = dt.getCarsNum();
//					
//					createAndRunModel(dt.getCars(start), dt.getTime_slots(), dt.getEnergy(), 
//							dt.getChargers(), dt.getRenewable_energy(), dt.getNon_renewable_energy(), 0.0, 1.0, 0.0, slot, dt.getCarsNum());
//					
//					break;
//				}
//				else
//				{
//					start += rate;
//				}
//
//			}
			
			System.out.println(dt.getCarsNum() + ", " + dt.getTime_slots()); 
			createAndRunModel(currentCars, dt.getTime_slots(), dt.getEnergy(), dt.getChargers(), 
					dt.getRenewable_energy(), dt.getNon_renewable_energy(), 0.0, 1.0, 0.0, slot, dt.getCarsNum());
			
			
			for(Car c: currentCars)
			{
				if(c.getStartTime() == c.getEndTime())
				{
					c.setNeeds(0);
					c.setMinNeeds(0);
				}
				c.setStartTime(c.getStartTime() + 1);
				int temp;
				temp = c.getEndTime() - c.getStartTime() + 1; // an de fortise, tote arkei to perithwrio pou dinei gia na fortisei?
				if(temp < c.getNeeds())
				{
					//c.setStartTime(c.getEndTime() + 1);
					//c.setNeeds(0);
				}
			}
		}

		
		for(int i = 0; i < dt.getCarsNum(); i++)
		{
			for(int j = 0; j < dt.getTime_slots(); j++)
			{
				//System.out.print(final_map[i][j] + " ");
			}
			//System.out.println(" " + currentCars.get(i).getInitial_start_time() + " - " + currentCars.get(i).getEndTime());
		}
		
		
		int[][] energy = new int[dt.getTime_slots()][3];
		for(int i = 0; i < dt.getTime_slots(); i++)
		{
			energy[i][0] = dt.getRenewable_energy()[i];
			energy[i][1] = dt.getNon_renewable_energy()[i];
			energy[i][2] = dt.getRenewable_energy()[i] + dt.getNon_renewable_energy()[i];
		}

		Results results = new Results(final_map, energy_used, dt.getChargers(), energy, renewables_used);
		return results;
		
	} 
	
	
	
	private int number_of_cars;
	public Test multiRunsStatic(int start, int rate, String path)
	{
		Test test = new Test();
		int count = 0;
		do
		{
	    	 count++;
	    	 System.out.println("Run: " + count);
			//compute
			Results results = this.staticRun(path, start);
			
			test.addCars((double)start);
			test.addCars_charged(results.getCarChargedPercentage());
			test.addTotal_energy(results.getEnergyUsedPercentage());
			test.addRenewables(results.getRenewablesUsedPercentage());
			test.addRenewables_total(results.getRenewablesPerAllPercentage());
			test.addNon_renewables(results.getNonRenewablesUsedPercentage());
			test.addSlots(results.getSlotsUsedPercentage());
			
			System.out.println("\n");
			//results.printMap();
			System.out.println(results.toString());
			if((start + rate) >= number_of_cars)
			{
				start = number_of_cars;
				
				//compute
				results = this.staticRun(path, start);
				
				test.addCars((double)start);
				test.addCars_charged(results.getCarChargedPercentage());
				test.addTotal_energy(results.getEnergyUsedPercentage());
				test.addRenewables(results.getRenewablesUsedPercentage());
				test.addRenewables_total(results.getRenewablesPerAllPercentage());
				test.addNon_renewables(results.getNonRenewablesUsedPercentage());
				test.addSlots(results.getSlotsUsedPercentage());
				
				
				System.out.println("\n");
				//results.printMap();
				System.out.println(results.toString());
				break;
			}
			else
			{
				start += rate;
			}
	
		} while(start <= number_of_cars);
		
		return test;
	}
	
	public Results staticRun(String path, int cars_num)
	{
		DataGenerator dt = new DataGenerator(0, 0, 0, 0);
		dt.readFromFile(path);
		number_of_cars = dt.getCarsNum();
		
		createAndRunModel(dt.getCarsByStartTime(cars_num), dt.getTime_slots(), 
				dt.getEnergy(), dt.getChargers(), dt.getRenewable_energy(), dt.getNon_renewable_energy(), 0.0, 1.0, 0.0, 0, -1);

		
		int [][] energy_map = new int[dt.getTime_slots()][3];
		for(int i = 0; i < dt.getTime_slots(); i++)
		{
			energy_map[i][0] = dt.getRenewable_energy()[i];
			energy_map[i][1] = dt.getNon_renewable_energy()[i];
			energy_map[i][2] = dt.getRenewable_energy()[i] + dt.getNon_renewable_energy()[i];			
		}
		
		Results results = new Results(final_map, energy_used, dt.getChargers(), energy_map, renewables_used);
		
		return results;
	}
	
	
	public void computeRealTimeResults()
	{
		
	}
	
	public int getRenEnergy()
	{
		return renewable_used;
	}
	
	public int getNonRenEnergy()
	{
		return non_renewable_used;
	}
	
	public int getEnergy()
	{
		return energy_used;
	}


	public ArrayList<Car> getCar_to_slot() {
		return car_to_slot;
	}

	public HashMap<Integer, ArrayList<Integer>> getSlot_to_car() {
		return slot_to_car;
	}

	public int getRenewable_all_used() {
		return renewable_all_used;
	}

	public double getSlots_used() {
		return slots_used;
	}

	public double getCharged() {
		return charged;
	}
	
	
	
	
}

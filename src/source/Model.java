package source;

import java.io.IOException;
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
					final_map = new int[evs.size()][ct];
				}
				else
				{
					var = new IloNumVar[carsNumber][ct]; // decision variables' arrays
					charges = new IloNumVar[carsNumber];
					ren_energy = new IloNumVar[ct][];
					non_ren_energy = new IloNumVar[ct][];
					final_map = new int[carsNumber][ct];
				}
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
					int end = evs.get(ev).getEndTime() + 1;
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
			
			
			double normalized_w = w1 / (evs.size() * ct);
			
			IloLinearNumExpr p_charges = cp.linearNumExpr();
			for(int ev = 0; ev < evs.size(); ev ++)
			{
				int start = evs.get(ev).getStartTime();
				int end = evs.get(ev).getEndTime() + 1;
				for(int time = start; time < end; time++)
				{
					p_charges.addTerm(normalized_w, var[ev][time]);
				}
			}
			normalized_w = w2 / evs.size();
			for(int ev = 0; ev < evs.size(); ev++)
			{
				p_charges.addTerm(normalized_w, charges[ev]);
			}
			
			

			
			
			
			boolean stupid = false; // true gia to statiko
			double all_energy = 0.0;
			for(int i = 0; i < ct; i++)
			{
				all_energy += renewable_energy[i];
				all_energy += non_renewable_energy[i];
			}
			normalized_w = w3 / all_energy;
			IloLinearNumExpr p_energy = cp.linearNumExpr();
			if(stupid) // me to kolpo pou meiwnei tin aksia twn ananewsimwn
			{
			
				// na xrisimopoiei perissotero ananewsimes

				// gia to statiko
				for(int t = first_slot; t < ct; t++)
				{
					for(int en = 0; en < renewable_energy[t]; en++)
					{
						p_energy.addTerm(normalized_w, ren_energy[t][en]);
					}
					for(int en = 0; en < non_renewable_energy[t]; en++)
					{
						p_energy.addTerm(normalized_w, non_ren_energy[t][en]);
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
				
				double factor = 0.8;
				
				double r = ((first_slot + ct) * 0.5) - first_slot;
				double rate = 0.6 / ct;
				System.out.println("The rate: " + rate);
				for(int t = first_slot; t < ct; t++)
				{
//					if(renewable_energy[t] < 0.3 * non_renewable_energy[t])
//					{
//						factor = 0.5;
//					}
					

					for(int en = 0; en < renewable_energy[t]; en++)
					{
						p_energy.addTerm((factor * normalized_w), ren_energy[t][en]);
					}
					for(int en = 0; en < non_renewable_energy[t]; en++)
					{
						p_energy.addTerm(((1.0 - factor) * normalized_w), non_ren_energy[t][en]);
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
					if(factor - rate > 0)
						factor -= rate;
					System.out.println("The factor: " + factor);
				}
			}
			System.out.println(p_energy);
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
							slots_used++;
							final_map[ev][first_slot] = 1;
							evs.get(ev).updateNeeds();
							System.out.println("Needs: " + evs.get(ev).getNeeds());
							if(!who_charge.contains(ev))
							who_charge.add(ev);
						}
					}
					
					//who_charge = new ArrayList<Integer>();
					for(int ev = 0; ev < evs.size(); ev++)
					{
						if(cp.getValue(charges[ev]) == 1.0)
						{
							//System.out.println("ti ginetai? " + ev);
							//System.out.println(evs.get(ev).getStartTime() + " - " + evs.get(ev).getEndTime() + " --> " + evs.get(ev).getNeeds()); 

						}
					}
					
					for(int en = 0; en < renewable_energy[first_slot]; en++)
					{		
						all_ren ++;
						if(cp.getValue(ren_energy[first_slot][en]) == 1.0)
						{
							used_r++;
						}
					}
					for(int en = 0; en < non_renewable_energy[first_slot]; en++)
					{
						all_non ++;
						if(cp.getValue(non_ren_energy[first_slot][en]) == 1.0)
						{
							used_n++;
						}
					}
					
					renewable_used = Math.round(((used_r / all_ren)*100));
					System.out.println("Used " + ((used_r / all_ren)*100) + "% of renewable energy (" + (int)used_r + "/" + (int)all_ren + ")");
					
					non_renewable_used = Math.round(((used_n / all_non)*100));
					System.out.println(((used_n / all_non)*100));
					System.out.println("Used " + ((used_n / all_non)*100) + "% of non renewable energy (" + (int)used_n + "/" + (int)all_non + ")");
					
					energy_used = Math.round((((used_r + used_n) / (all_ren + all_non))*100)) ;
					
			    	new DecimalFormat("#.00"); 
			    	renewable_all_used = Math.round((((used_r / (used_r + used_n))*100)));
			    	System.out.println("Energy used: " + energy_used + "%");
			    	System.out.println("Renewable/Energy used: " + renewable_all_used + "%");
					charged = Math.round((who_charge.size() / (float) carsNumber) * 100);
					System.out.println("Charged: " + charged + "%");
					
					//prints map
//					for(int ev = 0; ev < evs.size(); ev++)
//					{
//						
//						for(int t = first_slot; t < ct; t++)
//						{
//							if(cp.getValue(var[ev][t]) == 1.0)
//							{
//
//								System.out.print("O" + "  ");
//							}
//							else
//							{
//								System.out.print("X" + "  ");
//							}
//								
//						}
//						
//						System.out.println(evs.get(ev).getNeeds() + "    Was available from: " + evs.get(ev).getStartTime() + " to: " + evs.get(ev).getEndTime());
//					}
					
					
				}
			}

			

			} catch (IloException e) {
			System.err.println("Concert exception caught: " + e);
		}
		
	}

	public void getRealTimeStats()
	{
		
	}
	
	public void computeResults(IloCplex cp, ArrayList<Car> evs, int ct, int[] energy, int chargers, int[] renewable_energy, int[] non_renewable_energy, int first_slot) throws UnknownObjectException, IloException
	{

		
		for(int ev = 0; ev < evs.size(); ev++)
		{
			System.out.println(ev + ", " + first_slot);
			if(cp.getValue(var[ev][first_slot]) == 1.0)
			{
				final_map[ev][first_slot] = 1;
				evs.get(ev).updateNeeds();
				System.out.println("Needs: " + evs.get(ev).getNeeds());
			}
			int start = evs.get(ev).getStartTime();
			int end = evs.get(ev).getEndTime();
			int needs = evs.get(ev).getNeeds();
			for(int t = first_slot; t < ct; t++)
			{
				if(cp.getValue(var[ev][t]) == 1.0)
				{
					
					slots_used++;
					// add to car list
					//if(car_to_slot.get(ev) == null)
					//{
						evs.get(ev).addSlot(t);
						//car = new Car();
						//car.addSlot(t);
						//car_to_slot.put(ev, car);
					//}
					//else
					//{
						//car_to_slot.get(ev).addSlot(t);;
					//}
					
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
					
					System.out.print("O" + "  ");
				}
				else
				{
					System.out.print("X" + "  ");
				}
					
			}
			
			System.out.println(needs + "    Was available from: " + start + " to: " + end);
		}
		
		who_charge = new ArrayList<Integer>();
		for(int ev = 0; ev < evs.size(); ev++)
		{
			if(cp.getValue(charges[ev]) == 1.0)
			{
				charged ++;
				who_charge.add(ev);
				//System.out.println("Vehicle: " + (ev + 1) + " can be charged!");
			}
			else
			{
				//System.out.println("Vehicle: " + (ev + 1) + " cannot be charged...");
			}
		}
		
		//System.out.println("Charged: " + charged + ", All: " + evs.size());
		float all = evs.size();
		charged = (int) ((charged / all) * 100);
		
		for(int i = first_slot; i < ct; i++)
		{
			for(int ev = 0; ev < evs.size(); ev++)
			{
				if(cp.getValue(var[ev][i]) == 1.0)
				{
				}
			}
			//System.out.println("Energy used for time slot " + i + " is " + energy_used + " and the remaining"
					//+ " energy is " + (renewable_energy[i] + non_renewable_energy[i] - energy_used) + ".");
		}
		used_r = 0; //renewable
		used_n = 0; //non renewable
		all_ren = 0;
		all_non = 0;
		for(int i = first_slot; i < ct; i++)
		{
			for(int en = 0; en < renewable_energy[i]; en++)
			{		
				all_ren ++;
				if(cp.getValue(ren_energy[i][en]) == 1.0)
				{
					used_r++;
				}
			}
			for(int en = 0; en < non_renewable_energy[i]; en++)
			{
				all_non ++;
				if(cp.getValue(non_ren_energy[i][en]) == 1.0)
				{
					used_n++;
				}
			}
			//System.out.print("used " + ((used / non_renewable_energy[i])*100) + "% ");
			//System.out.println();
		}
		renewable_used = Math.round(((used_r / all_ren)*100));
		//System.out.println("Used " + ((used_r / all_ren)*100) + "% of renewable energy (" + (int)used_r + "/" + (int)all_ren + ")");
		
		non_renewable_used = Math.round(((used_n / all_non)*100));
		System.out.println(((used_n / all_non)*100));
		//System.out.println("Used " + ((used_n / all_non)*100) + "% of non renewable energy (" + (int)used_n + "/" + (int)all_non + ")");
		
		energy_used = Math.round((((used_r + used_n) / (all_ren + all_non))*100)) ;
		
    	new DecimalFormat("#.00"); 
    	renewable_all_used = Math.round((((used_r / (used_r + used_n))*100)));
    	System.out.println(used_n);
    	System.out.println((int) (((used_n / (used_r + used_n))*100)));
    	
    	//System.out.print(df.format(renewable_all_used) + "% of energy used was reanewable!");
    	
    	float all_slots = ct * chargers;
    	//System.out.println("All slots: " + all_slots);
    	//System.out.println("Slots used: " + slots_used);
    	slots_used = Math.round(((slots_used / all_slots) * 100));
    	
    	car_to_slot = evs;
	}
	
	
	public void realTimeRun()
	{
		DataGenerator dt = new DataGenerator(0, 0, 0, 0);
		dt.readFromFile("C:/Users/Andreas Sitaras/Desktop/big_test2.txt");
		//dt.readFromFile("F:/Eclipse Workshop/EvAgentSystem/temp_test.txt");

		ArrayList<Car> cars = dt.getCarsByStartTime();

		ArrayList<Car> currentCars = new ArrayList<Car>();
		int list_counter = 0;
		
		for(int slot = 0; slot < dt.getTime_slots(); slot++)
		{
			System.out.println("Slot: " + slot);
			if(list_counter < cars.size())
			{
				while(cars.get(list_counter).getStartTime() == slot)
				{
					currentCars.add(cars.get(list_counter));
					list_counter ++;
					if(list_counter == cars.size())
					{
						break;
					}
					System.out.println("Counter: " + list_counter);
				}
			}
			
			// updating current cars - going to be a function
			

			System.out.println(dt.getCarsNum() + ", " + dt.getTime_slots()); 
			createAndRunModel(currentCars, dt.getTime_slots(), dt.getEnergy(), dt.getChargers(), dt.getRenewable_energy(), dt.getNon_renewable_energy(), 0.0, 0.9, 0.1, slot, dt.getCarsNum());
//			try {
//				System.in.read();
//			} catch (IOException e) {
//				 //TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			
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

		float all_slots = dt.getTime_slots() * dt.getChargers();
    	slots_used = Math.round(((slots_used / all_slots) * 100));

    	System.out.println("Slots used: " + slots_used);
		
		for(int i = 0; i < dt.getCarsNum(); i++)
		{
			for(int j = 0; j < dt.getTime_slots(); j++)
			{
				//System.out.print(final_map[i][j] + " ");
			}
			//System.out.println(" " + currentCars.get(i).getInitial_start_time() + " - " + currentCars.get(i).getEndTime());
		}


		
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

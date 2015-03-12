package source;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

//isws constructoras kai meta start gia na min ftiaxnw nees metavlites gia na epistrefw ta ArrayList ktl
public class Model {

	private int renewable_used = 0;
	private int non_renewable_used = 0;
	private int energy_used = 0;
	private int renewable_all_used = 0;
	private int charged = 0;
	private int slots_used = 0;
	//private HashMap<Integer, Car> car_to_slot = new HashMap<Integer, Car>();
	private ArrayList car_to_slot;
	private HashMap<Integer, ArrayList<Integer>> slot_to_car = new HashMap<Integer, ArrayList<Integer>>();
	
	public void createAndRunModel(ArrayList<Car> evs, int ct, int[] energy, int chargers, int[] renewable_energy, int[] non_renewable_energy)
	{
		
		try {
			 
			IloCplex cp = new IloCplex(); // create the model
			IloNumVar[][] var = new IloNumVar[evs.size()][ct]; // decision variables' arrays
			IloNumVar[] charges = new IloNumVar[evs.size()];
			IloNumVar[][] ren_energy = new IloNumVar[ct][];
			IloNumVar[][] non_ren_energy = new IloNumVar[ct][];
			
			
			for(int i = 0; i < evs.size(); i++)
			{
				for(int j = 0; j < ct; j++)
				{
					var[i][j] = cp.boolVar("var(" + i + ", " + j + ")"); // creating boolean decision variables and giving them name
				}
				charges[i] = cp.boolVar("c(" + i + ")");
			}
			
			// 1)
			for(int ev = 0; ev < evs.size(); ev++)
			{

				int slots_need = evs.get(ev).getNeeds();
				IloLinearNumExpr p = cp.linearNumExpr(); // linear expression for the constraint
				
				for(int t = 0; t < ct; t++) // the time that the ev is available for charging
				{
						p.addTerm(1, var[ev][t]);
				}
				//System.out.println(p);
				cp.addEq(cp.prod(charges[ev], slots_need), p);

				//cp.addLe(p, 3);
				// for the github

			}
			//System.out.println(cp);
			
			
			for(int t = 0; t < ct; t++)
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
			for(int t = 0; t < ct; t++)
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
				}
				for(int en = 0; en < renewable_energy[t]; en++)
				{
					energy_.addTerm(1, ren_energy[t][en]);
				}
				for(int en = 0; en < non_renewable_energy[t]; en++)
				{
					energy_.addTerm(1, non_ren_energy[t][en]);
				}
				
				cp.addEq(energy_, cars);
			}

			
			// 3)
			for(int t = 0; t < ct; t++) // the sum of evs that charge in a time slot must not exceed the num of slots
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
				
				cp.addLe(p, chargers);
			}
			

			
			// 4)
			for(int t = 0; t < ct; t++) // energy constraint, the evs must not consume more than the available energy in the slot
			{						
				IloLinearNumExpr en = cp.linearNumExpr(); // the expression that need to be maximized, in this case, maximize
															// the total number of evs charging in all time slots
				
				for(int e = 0; e < evs.size(); e++)			 
				{

						en.addTerm(1, var[e][t]); // evs consume 2 energy units (not real number)

				}

				cp.addLe(en, renewable_energy[t] + non_renewable_energy[t]); // energy used in a time slot must not exceed the available energy
			}
			
			

			// ATIKEIMENIKES SYNARTISEIS
			
			// 1)
			// antikeimeniki synartisi, megistpopoiisi asswn
			IloLinearNumExpr p_charges = cp.linearNumExpr();
			for(int ev = 0; ev < evs.size(); ev ++)
			{
				int start = evs.get(ev).getStartTime();
				int end = evs.get(ev).getEndTime() + 1;
				for(int time = start; time < end; time++)
				{
					p_charges.addTerm(1, var[ev][time]);
				}
			}

			
			
			// na xrisimopoiei perissotero ananewsimes
			IloLinearNumExpr p_energy = cp.linearNumExpr();
			for(int t = 0; t < ct; t++)
			{
				for(int en = 0; en < renewable_energy[t]; en++)
				{
					p_energy.addTerm(10, ren_energy[t][en]);
				}
				for(int en = 0; en < non_renewable_energy[t]; en++)
				{
					p_energy.addTerm(1, non_ren_energy[t][en]);
				}
				
			}
			
			
			
			// megistopoiisi oximnatwn pou fortizoun
			/*
			IloLinearNumExpr p_charges = cp.linearNumExpr();
			for(int ev = 0; ev < evs.size(); ev++)
			{
				p_charges.addTerm(1, charges[ev]);
			}
			*/
			cp.addMaximize(cp.sum(p_charges, p_energy));
				
			
			if(cp.solve()) // solve the maximization problem and print the results
			{
				
				Car car;
				for(int t = 0; t < ct; t++)
				{
					int all_energy = renewable_energy[t] + non_renewable_energy[t];					
				}

				
				for(int ev = 0; ev < evs.size(); ev++)
				{
					
					int start = evs.get(ev).getStartTime();
					int end = evs.get(ev).getEndTime();
					int needs = evs.get(ev).getNeeds();
					for(int t = 0; t < ct; t++)
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
							
							//System.out.print("O" + "  ");
						}
						else
						{
							//System.out.print("X" + "  ");
						}
							
					}
					
					//System.out.println(needs + "    Was available from: " + start + " to: " + end);
				}
				
				
				for(int ev = 0; ev < evs.size(); ev++)
				{
					if(cp.getValue(charges[ev]) == 1.0)
					{
						charged ++;
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
				
				for(int i = 0; i < ct; i++)
				{
					int energy_used = 0;
					for(int ev = 0; ev < evs.size(); ev++)
					{
						if(cp.getValue(var[ev][i]) == 1.0)
						{
							energy_used += 1; // every ev that charges consumes 2 energy units
						}
					}
					//System.out.println("Energy used for time slot " + i + " is " + energy_used + " and the remaining"
							//+ " energy is " + (renewable_energy[i] + non_renewable_energy[i] - energy_used) + ".");
				}
				float used_r = 0; //renewable
				float used_n = 0; //non renewable
				float all_ren = 0;
				float all_non = 0;
				for(int i = 0; i < ct; i++)
				{
					//System.out.print("Renewable (available: " + renewable_energy[i] + "): ");
					float used = 0;
					for(int en = 0; en < renewable_energy[i]; en++)
					{		
						all_ren ++;
						if(cp.getValue(ren_energy[i][en]) == 1.0)
						{
							used++;
							used_r++;
						}
					}
					//System.out.print("used " + ((used / renewable_energy[i])*100) + "% ");
					//System.out.println();
					//System.out.print("Non Renewable (available: " + non_renewable_energy[i] + "): ");
					used = 0;
					for(int en = 0; en < non_renewable_energy[i]; en++)
					{
						all_non ++;
						if(cp.getValue(non_ren_energy[i][en]) == 1.0)
						{
							used++;
							used_n++;
						}
					}
					//System.out.print("used " + ((used / non_renewable_energy[i])*100) + "% ");
					//System.out.println();
				}
				renewable_used = (int) ((used_r / all_ren)*100);
				//System.out.println("Used " + ((used_r / all_ren)*100) + "% of renewable energy (" + (int)used_r + "/" + (int)all_ren + ")");
				
				non_renewable_used = (int) ((used_n / all_non)*100);
				//System.out.println("Used " + ((used_n / all_non)*100) + "% of non renewable energy (" + (int)used_n + "/" + (int)all_non + ")");
				
				energy_used = (int) (((used_r + used_n) / (all_ren + all_non))*100);
				
	        	DecimalFormat df = new DecimalFormat("#.00"); 
	        	renewable_all_used = (int) (((used_r / (used_r + used_n))*100));
	        	
	        	//System.out.print(df.format(renewable_all_used) + "% of energy used was reanewable!");
	        	
	        	float all_slots = ct * chargers;
	        	//System.out.println("All slots: " + all_slots);
	        	//System.out.println("Slots used: " + slots_used);
	        	slots_used = (int)((slots_used / all_slots) * 100);
	        	
	        	car_to_slot = evs;
			}

			

			} catch (IloException e) {
			System.err.println("Concert exception caught: " + e);
		}
		
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


	@SuppressWarnings("unchecked")
	public ArrayList<Car> getCar_to_slot() {
		return car_to_slot;
	}

	public HashMap<Integer, ArrayList<Integer>> getSlot_to_car() {
		return slot_to_car;
	}

	public int getRenewable_all_used() {
		return renewable_all_used;
	}

	public int getSlots_used() {
		return slots_used;
	}

	public int getCharged() {
		return charged;
	}
	
	
	
	
}

package source;

import java.util.ArrayList;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class Model {

	
	public void createAndRunModel(ArrayList<Car> evs, int ct, int[] energy, int chargers, int[][] d_energy)
	{
		
		try {
			 
			IloCplex cp = new IloCplex(); // create the model
			IloNumVar[][] var = new IloNumVar[evs.size()][ct]; // decision variables' arrays
			IloNumVar[] charges = new IloNumVar[evs.size()];
			IloNumVar[][][] diverse_energy = new IloNumVar[ct][2][];
					
			
			
			for(int i = 0; i < evs.size(); i++)
			{
				for(int j = 0; j < ct; j++)
				{
					var[i][j] = cp.boolVar("var(" + i + ", " + j + ")"); // creating boolean decision variables and giving them name
				}
				charges[i] = cp.boolVar("c(" + i + ")");
			}
			
			for(int ev = 0; ev < evs.size(); ev++)
			{
				int start = evs.get(ev).getStartTime();
				int end = evs.get(ev).getEndTime() + 1; // +1 gt ama einai idios o start me end (1 wra available dld), de tha treksei i for
				int slots_need = evs.get(ev).getNeeds();
				IloLinearNumExpr p = cp.linearNumExpr(); // linear expression for the constraint
				
				for(int t = start; t < end; t++) // the time that the ev is available for charging
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
				diverse_energy[t][0] = new IloNumVar[d_energy[t][0]];

				for(int i = 0; i < d_energy[t][0]; i++)
				{
					diverse_energy[t][0][i] = cp.boolVar("ren(" + t + ", " + i + ")");
				}
				
				diverse_energy[t][1] = new IloNumVar[d_energy[t][1]];
				
				for(int i = 0; i < d_energy[t][1]; i++)
				{
					diverse_energy[t][1][i] = cp.boolVar("non(" + t + ", " + i + ")");
				}
			}
			

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
				for(int en = 0; en < d_energy[t][0]; en++)
				{
					energy_.addTerm(1, diverse_energy[t][0][en]);
				}
				for(int en = 0; en < d_energy[t][1]; en++)
				{
					energy_.addTerm(1, diverse_energy[t][1][en]);
				}
				
				cp.addLe(energy_, cars);
			}

			
			
			for(int t = 0; t < ct; t++) // the sum of evs that charge in a time slot must not exceed the num of slots
			{
				IloLinearNumExpr p = cp.linearNumExpr();
				
				for(int e = 0; e < evs.size(); e++)
				{
					int start = evs.get(e).getStartTime();
					int end = evs.get(e).getEndTime() + 1;
					if(t >= start && t <= end)
					{
						p.addTerm(1, var[e][t]);
					}
				}
				
				cp.addLe(p, chargers);
			}
			

			
			
			for(int t = 0; t < ct; t++) // energy constraint, the evs must not consume more than the available energy in the slot
			{						
				IloLinearNumExpr en = cp.linearNumExpr(); // the expression that need to be maximized, in this case, maximize
															// the total number of evs charging in all time slots
				
				for(int e = 0; e < evs.size(); e++)			 
				{

						en.addTerm(1, var[e][t]); // evs consume 2 energy units (not real number)

				}

				cp.addLe(en, d_energy[t][0] + d_energy[t][1]); // energy used in a time slot must not exceed the available energy
			}
			
			

			
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
				for(int en = 0; en < d_energy[t][0]; en++)
				{
					p_energy.addTerm(10, diverse_energy[t][0][en]);
				}
				for(int en = 0; en < d_energy[t][1]; en++)
				{
					p_energy.addTerm(1, diverse_energy[t][1][en]);
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
				for(int t = 0; t < ct; t++)
				{
					int all_energy = d_energy[t][0] + d_energy[t][1];
					System.out.print(all_energy + " ");
									
				}
				System.out.println();
				
				for(int ev = 0; ev < evs.size(); ev++)
				{
					
					int start = evs.get(ev).getStartTime();
					int end = evs.get(ev).getEndTime();
					int needs = evs.get(ev).getNeeds();
					for(int t = 0; t < ct; t++)
					{
						if(cp.getValue(var[ev][t]) == 1.0)
						{
							System.out.print("O" + "  ");
						}
						else
						{
							System.out.print("X" + "  ");
						}
							
					}
					
					System.out.println(needs + "    Was available from: " + start + " to: " + end);
				}
				
				
				for(int ev = 0; ev < evs.size(); ev++)
				{
					if(cp.getValue(charges[ev]) == 1.0)
					{
						System.out.println("Vehicle: " + (ev + 1) + " can be charged!");
					}
					else
					{
						System.out.println("Vehicle: " + (ev + 1) + " cannot be charged...");
					}
				}
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
					System.out.println("Energy used for time slot " + i + " is " + energy_used + " and the remaining"
							+ " energy is " + (d_energy[i][0] + d_energy[i][1] - energy_used) + ".");
				}
				for(int i = 0; i < ct; i++)
				{
					System.out.print("Renewable (available: " + d_energy[i][0] + "): ");
					for(int en = 0; en < d_energy[i][0]; en++)
					{
						System.out.print(cp.getValue(diverse_energy[i][0][en]) + " ");
					}
					System.out.println();
					System.out.print("Non Renewable (available: " + d_energy[i][1] + "): ");
					for(int en = 0; en < d_energy[i][1]; en++)
					{
						System.out.print(cp.getValue(diverse_energy[i][1][en]) + " ");
					}
					System.out.println();
				}
				
			}

			

			} catch (IloException e) {
			System.err.println("Concert exception caught: " + e);
		}
		
	}
	
	
}

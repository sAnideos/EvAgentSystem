package source;

import java.util.ArrayList;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class Model {

	
	public void createAndRunModel(ArrayList<Car> evs, int ct, int[] energy, int chargers)
	{
		
		try {
			 
			IloCplex cp = new IloCplex(); // create the model
			IloNumVar[][] var = new IloNumVar[evs.size()][ct]; // decision variables' arrays

			
			for(int i = 0; i < evs.size(); i++)
			{
				for(int j = 0; j < ct; j++)
				{
					var[i][j] = cp.boolVar("var(" + i + ", " + j + ")"); // creating boolean decision variables and giving them name
				}
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
				cp.addEq(slots_need, p);
				
				//cp.addLe(p, 3);
				// for the github

			}
			
			
			for(int t = 0; t < ct; t++) // the sum of evs that charge in a time slot must not exceed the num of slots
			{
				IloLinearNumExpr p = cp.linearNumExpr();
				
				for(int e = 0; e < evs.size(); e++)
				{
					p.addTerm(1, var[e][t]);
				}
				
				cp.addLe(p, chargers);
			}
			
			IloLinearNumExpr p = cp.linearNumExpr();
			
			
			for(int t = 0; t < ct; t++) // energy constraint, the evs must not consume more than the available energy in the slot
			{						
				IloLinearNumExpr en = cp.linearNumExpr(); // the expression that need to be maximized, in this case, maximize
				for(int e = 0; e < evs.size(); e++)			 // the total number of evs charging in all time slots
				{
					int start = evs.get(e).getStartTime();
					int end = evs.get(e).getEndTime() + 1;
					for(int time = start; time < end; time++)
					{
						p.addTerm(1, var[e][time]);
					}

					en.addTerm(2, var[e][t]); // evs consume 2 energy units (not real number)
				}
				cp.addLe(en, energy[t]); // energy used in a time slot must not exceed the available energy
			}
			//System.out.println(cp);
			cp.addMaximize(p);
			
				
			
			if(cp.solve()) // solve the maximization problem and print the results
			{
				System.out.println();
				for(int t = 0; t < ct; t++)
				{
					System.out.print(energy[t] + " ");
									
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
			}

			

			} catch (IloException e) {
			System.err.println("Concert exception caught: " + e);
		}
		
	}
	
	
}

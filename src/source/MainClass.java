package source;


import java.util.Random;

import ilog.cplex.*;
import ilog.concert.*;

public class MainClass {



 public static void main(String[] args) {
     
	 
	 
	 /*
	 int evs = 10;
	 int ct = 11;
	 int chargers = 4;
	 int[][] ctime = new int[evs][3];
	 int[] energy = new int[ct]; //energy available at charging pont
	 
	 Random rand = new Random();
	 
	 
	 for(int i = 0; i < ct; i++)
	 {
		
		 int  n = rand.nextInt(20) + 10;
		 energy[i] = n;
	 }
	 
	 for(int i = 0; i < evs; i++)  // randomize time that ev has to charge
	 {
		
		 int  n = rand.nextInt(5) + 1;
		 ctime[i][0] = n; // how many time slots it needs
		 
		 n = rand.nextInt(5);
		 ctime[i][1] = n; // when it is available to start charging
		 ctime[i][2] = n + ctime[i][0]; // when it must leave the station
	 }
	 
	 
	 try {
		 
		IloCplex cp = new IloCplex(); // create the model
		IloNumVar[][] var = new IloNumVar[evs][ct]; // decision variables' arrays
		IloRange[][] rng = new IloRange[1][];
		
		rng[0] = new IloRange[1];
		
		for(int i = 0; i < evs; i++)
		{
			for(int j = 0; j < ct; j++)
			{
				var[i][j] = cp.boolVar("var(" + i + ", " + j + ")"); // creating boolean decision variables and giving them name
			}
		}
		
		for(int ev = 0; ev < evs; ev++)
		{
			IloLinearNumExpr p = cp.linearNumExpr(); // linear expression for the constraint
			
			for(int t = ctime[ev][1]; t <= ctime[ev][2]; t++) // the time that the ev is available for charging
			{
					p.addTerm(1, var[ev][t]);
			}
			//System.out.println(p);
			cp.addEq(ctime[ev][0], p);
			
			//cp.addLe(p, 3);

		}
		
		
		for(int t = 0; t < ct; t++) // the sum of evs that charge in a time slot must not exceed the num of slots
		{
			IloLinearNumExpr p = cp.linearNumExpr();
			
			for(int e = 0; e < evs; e++)
			{
				p.addTerm(1, var[e][t]);
			}
			
			cp.addLe(p, chargers);
		}
		
		IloLinearNumExpr p = cp.linearNumExpr();
		
		
		for(int t = 0; t < ct; t++) // energy constraint, the evs must not consume more than the available energy in the slot
		{		
			IloLinearNumExpr en = cp.linearNumExpr(); // the expression that need to be maximized, in this case, maximize
			for(int e = 0; e < evs; e++)			 // the total number of evs charging in all time slots
			{
				if(t >= ctime[e][1] && t <= ctime[e][2])
				{
					p.addTerm(1, var[e][t]);
				}
				else
				{
					p.addTerm(0, var[e][t]);
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
			
			for(int ev = 0; ev < evs; ev++)
			{
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
				
				System.out.println(ctime[ev][0] + "    Was available from: " + ctime[ev][1] + " to: " + ctime[ev][2]);
			}
		}

		

		} catch (IloException e) {
		System.err.println("Concert exception caught: " + e);
	}
	*/
	 
	 Run run = new Run();
	 run.start();
	 
 }
 

}
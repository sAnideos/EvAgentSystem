package source;

import java.util.ArrayList;

public class StatsManagement {

	private ArrayList<Stats> stats_list = new ArrayList<Stats>();
	
	public void addStats(Stats s)
	{
		stats_list.add(s);
	}
	
	public void printStats()
	{
		for(Stats t : stats_list)
		{
			System.out.println("w1: " + t.getW1());
			for(Double d: t.getRenewables())
			{
				System.out.println(d);
			}
		}
	}
	
	public void showGraph()
	{
		GraphManagement g = new GraphManagement("Graph", stats_list);
	}
	
}

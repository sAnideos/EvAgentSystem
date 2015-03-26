package source;

import java.util.ArrayList;

public class StatsManagement {

	private ArrayList<Test> stats_list = new ArrayList<Test>();
	
	public void addStats(Test s)
	{
		stats_list.add(s);
	}
	
	public void printStats()
	{
		for(Test t : stats_list)
		{
			System.out.println("w1: " + t.getW1());
			for(Double d: t.getRenewables())
			{
				System.out.println(d);
			}
		}
	}
	
	public void showGraph(String title)
	{
		GraphManagement g = new GraphManagement(title);
		g.showMultiTestGraph(title, stats_list);
	}
	
	public void singleTestGraph(String title, int actives[])
	{
		GraphManagement g = new GraphManagement(title);
		g.showSingleTestGraph(title, stats_list.get(stats_list.size() - 1), actives);
	}
	
	public void removeTest(String name)
	{
		for(int i = 0; i < stats_list.size(); i++)
		{
			if(stats_list.get(i).getName().equals(name))
			{
				stats_list.remove(i);
				break;
			}
		}
	}
	
}

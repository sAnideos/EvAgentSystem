package source;

import java.util.ArrayList;

public class Car {

	private int start_time; // ti wra mporei na er8ei sto sta8mo
	private int end_time; // ti wra prepei na fygei apo to sta8mo
	private int needs; // how much energy it needs (in time slots)
	private int min_needs;
	private int completed; // how much of 'needs' is completed
	private int initial_start_time = -1;
	private int initial_needs;
	private ArrayList<Integer> slots_used = new ArrayList<Integer>();
	
	
	public int getStartTime() {
		return start_time;
	}
	public void setStartTime(int start_time) {
		this.start_time = start_time;
		if(initial_start_time == -1)
		{
			initial_start_time = start_time;
		}
	}
	public int getEndTime() {
		return end_time;
	}
	public void setEndTime(int end_time) {
		this.end_time = end_time;
	}
	public int getNeeds() {
		return needs;
	}
	public void setNeeds(int needs) {
		this.needs = needs;
		initial_needs = needs;
	}
	
	public void setMinNeeds(int needs)
	{
		min_needs = needs;
	}
	
	public int getMinNeeds()
	{
		return min_needs;
	}
	
	public int getCompleted() {
		return completed;
	}
	public void setCompleted(int completed) {
		this.completed = completed;
	}
	
	public int getInitial_start_time() {
		return initial_start_time;
	}
	public void setInitial_start_time(int initial_start_time) {
		this.initial_start_time = initial_start_time;
	}
	public int getInitial_needs() {
		return initial_needs;
	}
	public void setInitial_needs(int initial_needs) {
		this.initial_needs = initial_needs;
	}
	
	public void updateNeeds()
	{
		needs--;
		min_needs--;
	}
	
	public void addSlot(Integer s)
	{
		slots_used.add(s);
	}
	
	public void resetSlots()
	{
		slots_used = new ArrayList<Integer>();
	}
	
	public ArrayList<Integer> getSlots()
	{
		return slots_used;
	}
	
	public String toString()
	{
		StringBuilder str = new StringBuilder();
		
		str.append("Available from: " + start_time + " to " + end_time);
		str.append(" with needs: " + initial_needs);
		return str.toString();
	}
	
}

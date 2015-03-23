package source;

import java.util.ArrayList;

public class Car {

	private int start_time; // ti wra mporei na er8ei sto sta8mo
	private int end_time; // ti wra prepei na fygei apo to sta8mo
	private int needs; // how much energy it needs (in time slots)
	private int completed; // how much of 'needs' is completed
	private ArrayList<Integer> slots_used = new ArrayList<Integer>();
	
	
	public int getStartTime() {
		return start_time;
	}
	public void setStartTime(int start_time) {
		this.start_time = start_time;
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
	}
	public int getCompleted() {
		return completed;
	}
	public void setCompleted(int completed) {
		this.completed = completed;
	}
	
	public void updateNeeds()
	{
		needs--;
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
	
}

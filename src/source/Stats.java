package source;

import java.util.ArrayList;

public class Stats {

	private ArrayList<Double> total_energy;
	private ArrayList<Double> renewables;
	private ArrayList<Double> non_renewables;
	private ArrayList<Double> renewables_total;
	private ArrayList<Integer> cars_charged;
	private ArrayList<Integer> slots;
	private ArrayList<Integer> cars;
	private double w1;
	private double w2;
	private double w3;
	
	
	public Stats()
	{
		total_energy = new ArrayList<Double>();
		renewables = new ArrayList<Double>();
		non_renewables = new ArrayList<Double>();
		renewables_total = new ArrayList<Double>();
		cars_charged = new ArrayList<Integer>();
		slots = new ArrayList<Integer>();
		cars = new ArrayList<Integer>();
	}
	
	public ArrayList<Double> getTotal_energy() {
		return total_energy;
	}
	public void addTotal_energy(Double e) {
		total_energy.add(e);
	}
	public ArrayList<Double> getRenewables() {
		return renewables;
	}
	public void addRenewables(Double e) {
		renewables.add(e);
	}
	public ArrayList<Double> getNon_renewables() {
		return non_renewables;
	}
	public void addNon_renewables(Double e) {
		non_renewables.add(e);
	}
	public ArrayList<Double> getRenewables_total() {
		return renewables_total;
	}
	public void addRenewables_total(Double e) {
		renewables_total.add(e);
	}
	public ArrayList<Integer> getCars_charged() {
		return cars_charged;
	}
	public void addCars_charged(Integer e) {
		cars_charged.add(e);
	}
	public ArrayList<Integer> getSlots() {
		return slots;
	}
	public void addSlots(Integer e) {
		slots.add(e);
	}
	public double getW1() {
		return w1;
	}
	public void setW1(double w1) {
		this.w1 = w1;
	}
	public double getW2() {
		return w2;
	}
	public void setW2(double w2) {
		this.w2 = w2;
	}
	public double getW3() {
		return w3;
	}
	public void setW3(double w3) {
		this.w3 = w3;
	}

	public ArrayList<Integer> getCars() {
		return cars;
	}

	public void addCars(Integer e) {
		cars.add(e);
	}
	
	

	
	
	
	
}

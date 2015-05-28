package source;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class Test {

	private String test_name;
	private ArrayList<Double> total_energy;
	private ArrayList<Double> renewables;
	private ArrayList<Double> non_renewables;
	private ArrayList<Double> renewables_total;
	private ArrayList<Double> cars_charged;
	private ArrayList<Double> slots;
	private ArrayList<Double> cars;
	private double w1;
	private double w2;
	private double w3;
	
	
	public Test()
	{
		total_energy = new ArrayList<Double>();
		renewables = new ArrayList<Double>();
		non_renewables = new ArrayList<Double>();
		renewables_total = new ArrayList<Double>();
		cars_charged = new ArrayList<Double>();
		slots = new ArrayList<Double>();
		cars = new ArrayList<Double>();
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
	public ArrayList<Double> getCars_charged() {
		return cars_charged;
	}
	public void addCars_charged(Double e) {
		cars_charged.add(e);
	}
	public ArrayList<Double> getSlots() {
		return slots;
	}
	public void addSlots(Double e) {
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

	public ArrayList<Double> getCars() {
		return cars;
	}

	public void addCars(Double e) {
		cars.add(e);
	}
	
	public void setTestName(String name)
	{
		this.test_name = name;
	}
	
	public String getName()
	{
		return test_name;
	}

	
	public String toString()
	{

		StringBuilder str = new StringBuilder();
		
		
		str.append("Cars: ");
		for(int i = 0; i < total_energy.size(); i++)
		{
			str.append(cars.get(i) + " ");
		}
		
		str.append("\nTotal energy used: ");
		for(int i = 0; i < total_energy.size(); i++)
		{
			str.append(total_energy.get(i) + " ");
		}
		
		str.append("\nTotal renewables used: ");
		for(int i = 0; i < total_energy.size(); i++)
		{
			str.append(renewables.get(i) + " ");
		}
		
		str.append("\nTotal non renewables used: ");
		for(int i = 0; i < non_renewables.size(); i++)
		{
			str.append(total_energy.get(i) + " ");
		}
		
		str.append("\nTotal renewables/all energy used: ");
		for(int i = 0; i < total_energy.size(); i++)
		{
			str.append(renewables_total.get(i) + " ");
		}
		
		str.append("\nTotal cars charged: ");
		for(int i = 0; i < total_energy.size(); i++)
		{
			str.append(cars_charged.get(i) + " ");
		}
		
		str.append("\nTotal slots used: ");
		for(int i = 0; i < total_energy.size(); i++)
		{
			str.append(slots.get(i) + " ");
		}
		
		return str.toString();
		
	}
	
	
	
	public int getEnergyAverage()
	{
		double average = 0;
		double count = 0;
		
		for(Double d: total_energy)
		{
			count++;
			average += d;
		}
		
		return (int)Math.round(average/count);
	}
	

	public int getReneablesAverage()
	{
		double average = 0;
		double count = 0;
		
		for(Double d: renewables)
		{
			count++;
			average += d;
		}
		
		return (int)Math.round(average/count);
	}
	
	public int getRenewablesPerAllAverage()
	{
		double average = 0;
		double count = 0;
		
		for(Double d: renewables_total)
		{
			count++;
			average += d;
		}
		
		return (int)Math.round(average/count);
	}
	
	public int getNonRenewablesAverage()
	{
		double average = 0;
		double count = 0;
		
		for(Double d: non_renewables)
		{
			count++;
			average += d;
		}
		
		return (int)Math.round(average/count);
	}
	
	public int getCarsChargedAverage()
	{
		double average = 0;
		double count = 0;
		
		for(Double d: cars_charged)
		{
			count++;
			average += d;
		}
		
		return (int)Math.round(average/count);
	}
	
	public int getSlotsUsedAverage()
	{
		double average = 0;
		double count = 0;
		
		for(Double d: slots)
		{
			count++;
			average += d;
		}
		
		return (int)Math.round(average/count);
	}
	

	
	
}

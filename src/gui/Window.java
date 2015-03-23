package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JButton;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JProgressBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import source.Car;
import source.DataGenerator;
import source.GraphManagement;
import source.Model;
import source.Stats;
import source.StatsManagement;

import javax.swing.JTextPane;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import java.awt.Font;

import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JCheckBox;

public class Window {

	// model
	private Model model;
	private DataGenerator dt;
	private int energy_range = 1;
	private int evs = 1;
	private int time_slots = 2;
	private int chargers = 1;
	private double w1 = 0.35, w2 = 0.35, w3 = 0.30;
	private ArrayList<Car> car_to_slot;
	private HashMap<Integer, ArrayList<Integer>> slot_to_car;
	private int read_file = 0; // 1 - button "File" was pressed, 0 - user input
	private StatsManagement sm = new StatsManagement();
	
	public class MeThread implements Runnable {

		private Stats s;
		
	    public void run() {

	    	s = new Stats();
	    	
	    	btnCompute.setEnabled(false);
	    	btnRandom.setEnabled(false);
	    	btnFile.setEnabled(false);
	    	btnSaveAs.setEnabled(false);
	    	moreSlotsSlider.setEnabled(false);
	    	moreRenSlider.setEnabled(false);
	    	
	    	
	    	
	    	
			int start = 15;
			int rate = 5;
			while(start <= dt.getCarsNum())
			{
		    	  	
				computeResults(start);
				setStatsManagement(start);

				
				if((start + rate) > dt.getCarsNum())
				{
					start = dt.getCarsNum();
					
					computeResults(start);
					
					setStatsManagement(start);
					
					break;
				}
				else
				{
					start += rate;
				}

			}
	    	btnCompute.setEnabled(true);
	    	btnRandom.setEnabled(true);
	    	btnFile.setEnabled(true);
	    	btnSaveAs.setEnabled(true);
	    	moreSlotsSlider.setEnabled(true);
	    	moreRenSlider.setEnabled(true);
	    	
			sm.addStats(s);
	    	sm.printStats();
	    }


	    public void setStatsManagement(int cars_n)
	    {

	    	s.addCars_charged(model.getCharged());
			s.addNon_renewables((double) (model.getNonRenEnergy()));
			s.addRenewables((double) model.getRenEnergy());
			s.addRenewables_total((double) model.getRenewable_all_used());
			s.addSlots(model.getSlots_used());
			s.addTotal_energy((double) model.getEnergy());
			s.addCars(cars_n);
			s.setW1(w1);
			s.setW2(w2);
			s.setW3(w3);

	    }
	}
	
	
	
	private class ActionHandler implements ActionListener {

	// HashMap	
//    	Set<Integer> k = car_to_slot.keySet();
//    	for(Integer key: k)
//    	{
//    		for(Integer t: car_to_slot.get(key))
//    		{
//    			System.out.println(t);
//    		}
//    		
//    	}
		@Override
		public void actionPerformed(ActionEvent e) {
			
			if(e.getActionCommand().compareTo("compute") == 0)
			{
	        	table_model_car.setRowCount(0);
	        	table_model_slot.setRowCount(0);
				consoleScreen.setText(null);
				//System.out.println("time_slots: " + time_slots);
				if(!(read_file == 1) && (!fileCheckBox.isSelected()))
				{
					dt = new DataGenerator(evs, time_slots, chargers, energy_range);
					dt.generateCarData();
					dt.generateEnergyData();
					dt.generateDiverseEnergy();			
					fileCheckBox.setEnabled(false);
				}
				read_file = 0;
				//consoleScreen.setText("" + read_file);
				
				if(multiRunsCheckBox.isSelected())
				{
            		Thread thread = (new Thread(new MeThread()));
            		thread.start();
				}
				else
				{
					//System.out.println(dt.getCars());
					computeResults(-1);
				}
				
				
			}
			else if(e.getActionCommand().compareTo("save_as") == 0)
			{
				if(dt != null)
				{
					JFileChooser chooser = new JFileChooser();
					chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					int option = chooser.showSaveDialog(frmElectricVehicleAgent); // parentComponent must a component like JFrame, JDialog...
					if (option == JFileChooser.APPROVE_OPTION) {
					   File selectedFile = chooser.getSelectedFile();
					   String path = selectedFile.getAbsolutePath();
	
					   path = path.replaceAll(".txt", "");
					   path = path.concat(".txt");
					   File f = new File(path);
					   if(f.exists()) 
					   { 
						   int response = JOptionPane.showConfirmDialog(null, //
						            "Do you want to replace the existing file?", //
						            "Confirm", JOptionPane.YES_NO_OPTION, //
						            JOptionPane.QUESTION_MESSAGE);
						    if (response == JOptionPane.YES_OPTION) {
						    	dt.writeToFile(path);
						    } 
	
					   }
					   else
					   {
						   dt.writeToFile(path);
					   }
					   
					}
					energyAllProgressBar.setBackground(Color.RED);
					btnSaveAs.setEnabled(false);
				}
				else
				{
					consoleScreen.setText("Compute the solution first!");
				}
			}
			else if(e.getActionCommand().compareTo("random") == 0)
			{

        	   Random r = new Random();
        	   int temp = r.nextInt(carSlider.getMaximum()) + 1;
        	   carTextPane.setText("" + temp);
        	   carSlider.setValue(temp);
        	   //System.out.println("evs: " + evs);
        	   energySlider.setMaximum(temp);
        	   //System.out.println("energy: " + energy_range);

        	   temp = r.nextInt(temp) + 1;
        	   energyTextPane.setText("" + temp);
        	   energySlider.setValue(temp);

        	   temp = r.nextInt(100) + 2;
        	   slotTextPane.setText("" + temp);
        	   slotSlider.setValue(temp);
        	  //System.out.println("time_slots: " + time_slots);


        	   temp = r.nextInt(100) + 1;
        	   chargerTextPane.setText("" + temp);
        	   chargerSlider.setValue(temp);
        	   //System.out.println("chargers: " + chargers);

			}
			
		}
		
	}
	

	
private class ChangeHandler implements ChangeListener {
		
//		private JTextPane energyTextPane;
//		private JTextPane carTextPane;
//		private JTextPane slotTextPane;
//		private JTextPane chargerTextPane;
	
		
        public void stateChanged(ChangeEvent event)
        {
           // update text field when the slider value changes
           JSlider source = (JSlider) event.getSource();
           
           if(source.getName().equals("energy"))
           {
        	   energy_range = source.getValue();
        	   energyTextPane.setText("" + energy_range);
        	   //System.out.println("energy: " + energy_range);
           }
           else if (source.getName().equals("car"))
           {
        	   evs = source.getValue();
        	   carTextPane.setText("" + evs);
        	   //System.out.println("evs: " + evs);
        	   energySlider.setMaximum(evs);
           }
           else if (source.getName().equals("slot"))
           {
        	   time_slots = source.getValue();
        	   slotTextPane.setText("" + time_slots);
        	  //System.out.println("time_slots: " + time_slots);
           }
           else if(source.getName().equals("charger"))
           {
        	   chargers = source.getValue();
        	   chargerTextPane.setText("" + chargers);
        	   //System.out.println("chargers: " + chargers);
           }
           else if(source.getName().equals("ren_w"))
           {
				w2 = (source.getValue() / 100.0);
				DecimalFormat df = new DecimalFormat("0.00"); 
				String temp = df.format(w2).replace(",", ".");
				renWeightPane.setText(temp);
				w2 = Double.parseDouble(temp);
				double why = (Math.round((1.0 - w2 - w1) * 100.0)) / 100.0;
				w3 = why;
				temp = df.format(w3).replace(",", ".").replace("-", "");
				
				chargedWeightPane.setText(temp);
				moreChargeSlider.setValue((int)(w3 * 100));
				System.out.println("w1: " + w1 + " w2: " + w2 + " w3: " + w3);
           }
           else if(source.getName().equals("slots_w")) // edw to varos tha kataligei panta se 0 h' 5, den kserw an einai kako, emena kalo mou fainetai... dld de tha ginei pote 0.71 px
           {
				w1 = (source.getValue() / 100.0);
				
				if(source.getValue() % 10 == 5)
				{
					int temp;
					temp = source.getValue() - 5;
					
					w2 = ((100 - temp) / 2) / 100.0;
					w3 = w2 + 0.05;
				}
				else
				{
					w2 = ((100.0 - source.getValue()) / 2) / 100.0;
					w3 = w2;
				}
				
				//System.out.println("W2: " + w2 + " W3: " + w3);
				//w2 = (1.0 - w1) / 2;
				//System.out.println(w2);
				DecimalFormat df = new DecimalFormat("0.00"); 
				String temp = df.format(w1).replace(",", ".");
				slotsWeightPane.setText(temp);
				
				
				temp = df.format(w2).replace(",", ".");
				renWeightPane.setText(temp + "");
				
				chargedWeightPane.setText(w3  + "");
				moreChargeSlider.setValue((int)(w3) * 100);
				
				
				double why = (Math.round((1.00 - w1) * 100.0)) / 100.0;
				moreRenSlider.setMaximum((int)(why * 100.0));
				moreRenSlider.setValue((int)(w2 * 100.0));
				moreRenSlider.setMajorTickSpacing(5);

				
				//System.out.println("The maximum: " + (int)(Double.parseDouble(temp) * 100));
				
           }
        }
	}
	
	
	private JFrame frmElectricVehicleAgent;
	private JPanel settingsPanel;
	private JPanel controlPanel;
	private JLabel lblEnergy;
	private JSlider energySlider;
	private JLabel lblCars;
	private JSlider carSlider;
	private JLabel lblTimeSlots;
	private JSlider slotSlider;
	private JLabel lblChargers;
	private JButton btnRandom;
	private JSlider chargerSlider;
	private JButton btnFile;
	private JButton btnCompute;
	private JPanel statsPanel;
	private JLabel lblEnergyUsed;
	private JLabel lblRenewablesUsed;
	private JLabel lblNonRenewablesUsed;
	private JProgressBar energyProgressBar;
	private JProgressBar renProgressBar;
	private JProgressBar nonRenProgressBar;
	private JTextPane energyTextPane;
	private JTextPane carTextPane;
	private JTextPane slotTextPane;
	private JTextPane chargerTextPane;
	private DefaultTableModel table_model_car;
	private DefaultTableModel table_model_slot;
	private Border blackline = BorderFactory.createLineBorder(Color.black);
	private JTabbedPane tabbedPane;
	private JScrollPane bySlotPane;
	private JScrollPane byCarPane;
	private JTable bySlotTable;
	private JTable byCarTable;
	private JLabel lblCarsCharged;
	private JProgressBar chargedProgressBar;
	private JLabel lblTimeSlotsUsed;	
	private JProgressBar slotProgressBar;	
	private JLabel lblRenewablesAll;	
	private JProgressBar energyAllProgressBar;	
	private JScrollPane scrollPane;
	private JTextPane consoleScreen;
	private JButton btnSaveAs;
	private JButton btnCopyAll;
	private JButton plusBtnEnergy;
	private JButton plusBtnSlots;
	private JButton plusBtnChargers;
	private JButton minusBtnCars;
	private JButton minusBtnEnergy;
	private JButton minusBtnSlots;
	private JButton minusBtnChargers;
	private JSlider moreSlotsSlider;	
	private JSlider moreChargeSlider;	
	private JSlider moreRenSlider;
	private JTextPane slotsWeightPane;
	private JTextPane renWeightPane;
	private JTextPane chargedWeightPane;
	private JCheckBox multiRunsCheckBox;
	private JCheckBox fileCheckBox;
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel(
			        UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Window window = new Window();
					window.frmElectricVehicleAgent.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Window() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings("serial")
	private void initialize() {
		ActionHandler action = new ActionHandler();
		ChangeHandler change = new ChangeHandler();
		
		frmElectricVehicleAgent = new JFrame();
		frmElectricVehicleAgent.setResizable(false);
		frmElectricVehicleAgent.setTitle("Electric Vehicle Agent System");
		frmElectricVehicleAgent.setBounds(100, 100, 756, 611);
		frmElectricVehicleAgent.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmElectricVehicleAgent.getContentPane().setLayout(null);
		
		settingsPanel = new JPanel();
		settingsPanel.setBounds(10, 11, 292, 553);
		frmElectricVehicleAgent.getContentPane().add(settingsPanel);
		settingsPanel.setLayout(null);
		
		lblEnergy = new JLabel("Energy Range");
		lblEnergy.setBounds(10, 83, 89, 14);
		settingsPanel.add(lblEnergy);
		
		energySlider = new JSlider();
		energySlider.setMajorTickSpacing(5);
		energySlider.setToolTipText("");
		energySlider.setPaintTicks(true);
		energySlider.setMinimum(1);
		energySlider.setValue(1);
		energySlider.setBounds(10, 108, 163, 23);
		energySlider.setName("energy");
		energySlider.addChangeListener(change);
		settingsPanel.add(energySlider);
		
		lblCars = new JLabel("Cars");
		lblCars.setBounds(10, 11, 46, 14);
		settingsPanel.add(lblCars);
		
		carSlider = new JSlider();
		carSlider.setMaximum(250);
		carSlider.setMinorTickSpacing(5);
		carSlider.setValue(1);
		carSlider.setMinimum(1);
		carSlider.setPaintTicks(true);
		carSlider.setBounds(10, 36, 163, 23);
		carSlider.setName("car");
		carSlider.addChangeListener(change);
		settingsPanel.add(carSlider);
		
		lblTimeSlots = new JLabel("Time Slots");
		lblTimeSlots.setBounds(10, 142, 71, 14);
		settingsPanel.add(lblTimeSlots);
		
		slotSlider = new JSlider();
		slotSlider.setMaximum(287);
		slotSlider.setMinorTickSpacing(5);
		slotSlider.setPaintTicks(true);
		slotSlider.setValue(2);
		slotSlider.setMinimum(2);
		slotSlider.setBounds(10, 167, 163, 23);
		slotSlider.setName("slot");
		slotSlider.addChangeListener(change);
		settingsPanel.add(slotSlider);
		
		lblChargers = new JLabel("Chargers");
		lblChargers.setBounds(10, 212, 61, 14);
		settingsPanel.add(lblChargers);
		
		chargerSlider = new JSlider();
		chargerSlider.setMinorTickSpacing(5);
		chargerSlider.setValue(1);
		chargerSlider.setMinimum(1);
		chargerSlider.setPaintTicks(true);
		chargerSlider.setBounds(10, 237, 163, 23);
		chargerSlider.setName("charger");
		chargerSlider.addChangeListener(change);
		settingsPanel.add(chargerSlider);
		
		btnRandom = new JButton("Randomize");
		btnRandom.setActionCommand("random");
		btnRandom.addActionListener(action);
		btnRandom.setBounds(94, 485, 101, 23);
		settingsPanel.add(btnRandom);
		
		btnFile = new JButton("File");
		btnFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int option = chooser.showOpenDialog(frmElectricVehicleAgent); // parentComponent must a component like JFrame, JDialog...
				if (option == JFileChooser.APPROVE_OPTION) {
				   File selectedFile = chooser.getSelectedFile();
				   String path = selectedFile.getAbsolutePath();
				  // System.out.println(path.toString());
				   dt = new DataGenerator(0,0,0,0);
				   dt.readFromFile(path);
				   read_file = 1;
				   fileCheckBox.setEnabled(true);
				}
			}
		});
		btnFile.setBounds(10, 519, 101, 23);
		settingsPanel.add(btnFile);
		
		energyTextPane = new JTextPane();
		energyTextPane.setText("1");
		energyTextPane.setBounds(240, 111, 34, 20);
		energyTextPane.setBorder(blackline);
		energyTextPane.setEditable(false);
		settingsPanel.add(energyTextPane);
		
		carTextPane = new JTextPane();
		carTextPane.setText("1");
		carTextPane.setBounds(240, 39, 34, 20);
		carTextPane.setBorder(blackline);
		carTextPane.setEditable(false);
		settingsPanel.add(carTextPane);
		
		slotTextPane = new JTextPane();
		slotTextPane.setText("2");
		slotTextPane.setBounds(240, 170, 34, 20);
		slotTextPane.setBorder(blackline);
		slotTextPane.setEditable(false);
		settingsPanel.add(slotTextPane);
		
		chargerTextPane = new JTextPane();
		chargerTextPane.setText("1");
		chargerTextPane.setBounds(240, 240, 34, 20);
		chargerTextPane.setBorder(blackline);
		chargerTextPane.setEditable(false);
		settingsPanel.add(chargerTextPane);
		
		btnSaveAs = new JButton("Save As");
		btnSaveAs.setActionCommand("save_as");
		btnSaveAs.addActionListener(action);
		btnSaveAs.setBounds(173, 519, 101, 23);
		btnSaveAs.setEnabled(false);
		settingsPanel.add(btnSaveAs);
		
		JLabel lblObjectiveFunctionWeight = new JLabel("Objective Function Weight");
		lblObjectiveFunctionWeight.setHorizontalAlignment(SwingConstants.CENTER);
		lblObjectiveFunctionWeight.setBounds(46, 288, 184, 14);
		settingsPanel.add(lblObjectiveFunctionWeight);
		
		JButton plusBtnCars = new JButton("+");
		plusBtnCars.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				carSlider.setValue(carSlider.getValue() + 1);
			}
		});
		plusBtnCars.setMargin(new Insets(0,0,0,0));
		plusBtnCars.setBounds(183, 36, 24, 23);
		settingsPanel.add(plusBtnCars);
		
		plusBtnEnergy = new JButton("+");
		plusBtnEnergy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				energySlider.setValue(energySlider.getValue() + 1);
			}
		});
		plusBtnEnergy.setMargin(new Insets(0, 0, 0, 0));
		plusBtnEnergy.setBounds(183, 108, 24, 23);
		settingsPanel.add(plusBtnEnergy);
		
		plusBtnSlots = new JButton("+");
		plusBtnSlots.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				slotSlider.setValue(slotSlider.getValue() + 1);
			}
		});
		plusBtnSlots.setMargin(new Insets(0, 0, 0, 0));
		plusBtnSlots.setBounds(183, 167, 24, 23);
		settingsPanel.add(plusBtnSlots);
		
		plusBtnChargers = new JButton("+");
		plusBtnChargers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chargerSlider.setValue(chargerSlider.getValue() + 1);
			}
		});
		plusBtnChargers.setMargin(new Insets(0, 0, 0, 0));
		plusBtnChargers.setBounds(183, 237, 24, 23);
		settingsPanel.add(plusBtnChargers);
		
		minusBtnCars = new JButton("-");
		minusBtnCars.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				carSlider.setValue(carSlider.getValue() - 1);
			}
		});
		minusBtnCars.setMargin(new Insets(0, 0, 0, 0));
		minusBtnCars.setBounds(206, 36, 24, 23);
		settingsPanel.add(minusBtnCars);
		
		minusBtnEnergy = new JButton("-");
		minusBtnEnergy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				energySlider.setValue(energySlider.getValue() - 1);
			}
		});
		minusBtnEnergy.setMargin(new Insets(0, 0, 0, 0));
		minusBtnEnergy.setBounds(206, 108, 24, 23);
		settingsPanel.add(minusBtnEnergy);
		
		minusBtnSlots = new JButton("-");
		minusBtnSlots.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				slotSlider.setValue(slotSlider.getValue() - 1);
				
			}
		});
		minusBtnSlots.setMargin(new Insets(0, 0, 0, 0));
		minusBtnSlots.setBounds(206, 167, 24, 23);
		settingsPanel.add(minusBtnSlots);
		
		minusBtnChargers = new JButton("-");
		minusBtnChargers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chargerSlider.setValue(chargerSlider.getValue() - 1);
			}
		});
		minusBtnChargers.setMargin(new Insets(0, 0, 0, 0));
		minusBtnChargers.setBounds(206, 237, 24, 23);
		settingsPanel.add(minusBtnChargers);
		

		
		JLabel lblMoreSlots = new JLabel("More Slots");
		lblMoreSlots.setBounds(10, 448, 61, 14);
		settingsPanel.add(lblMoreSlots);
		
		JLabel lblMoreCharged = new JLabel("More Charged");
		lblMoreCharged.setBounds(206, 448, 84, 14);
		settingsPanel.add(lblMoreCharged);
		
		JLabel lblMoreRenewables = new JLabel("More Renewables");
		lblMoreRenewables.setBounds(94, 448, 89, 14);
		settingsPanel.add(lblMoreRenewables);
		
		
		slotsWeightPane = new JTextPane();
		slotsWeightPane.setText("0.35");
		slotsWeightPane.setEditable(false);
		slotsWeightPane.setBorder(blackline);
		slotsWeightPane.setBounds(10, 417, 34, 20);
		settingsPanel.add(slotsWeightPane);
		
		renWeightPane = new JTextPane();
		renWeightPane.setText("0.35");
		renWeightPane.setEditable(false);
		renWeightPane.setBorder(blackline);
		renWeightPane.setBounds(125, 417, 34, 20);
		settingsPanel.add(renWeightPane);
		
		chargedWeightPane = new JTextPane();
		chargedWeightPane.setText("0.30");
		chargedWeightPane.setEditable(false);
		chargedWeightPane.setBorder(blackline);
		chargedWeightPane.setBounds(240, 417, 34, 20);
		settingsPanel.add(chargedWeightPane);
		
		moreChargeSlider = new JSlider();
		moreChargeSlider.setEnabled(false);
		moreChargeSlider.setValue(30);
		moreChargeSlider.addChangeListener(change);
		moreChargeSlider.setName("charged_w");
		moreChargeSlider.setSnapToTicks(true);
		moreChargeSlider.setPaintTicks(true);
		moreChargeSlider.setOrientation(SwingConstants.VERTICAL);
		moreChargeSlider.setMajorTickSpacing(5);
		moreChargeSlider.setBounds(240, 314, 34, 96);
		settingsPanel.add(moreChargeSlider);
		
		moreRenSlider = new JSlider();
		moreRenSlider.setMaximum(35);
		moreRenSlider.setValue(35);
		moreRenSlider.setName("ren_w");
		moreRenSlider.addChangeListener(change);
		moreRenSlider.setSnapToTicks(true);
		moreRenSlider.setPaintTicks(true);
		moreRenSlider.setOrientation(SwingConstants.VERTICAL);
		moreRenSlider.setMajorTickSpacing(2);
		moreRenSlider.setBounds(125, 314, 34, 96);
		settingsPanel.add(moreRenSlider);
		
		
		
		moreSlotsSlider = new JSlider();
		moreSlotsSlider.setValue(35);
		moreSlotsSlider.setName("slots_w");
		moreSlotsSlider.addChangeListener(change);
		moreSlotsSlider.setMajorTickSpacing(5);
		moreSlotsSlider.setSnapToTicks(true);
		moreSlotsSlider.setPaintTicks(true);
		moreSlotsSlider.setOrientation(SwingConstants.VERTICAL);
		moreSlotsSlider.setBounds(10, 314, 34, 96);
		settingsPanel.add(moreSlotsSlider);
		

		
		
		
		
		
		controlPanel = new JPanel();
		controlPanel.setBounds(312, 427, 411, 71);
		frmElectricVehicleAgent.getContentPane().add(controlPanel);
		controlPanel.setLayout(null);
		
		btnCompute = new JButton("Compute");
		btnCompute.setActionCommand("compute");
		btnCompute.addActionListener(action);
		btnCompute.setBounds(168, 11, 89, 23);
		controlPanel.add(btnCompute);
		
		btnCopyAll = new JButton("Copy All");
		btnCopyAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				String myString = (energyProgressBar.getString() + " " + energyAllProgressBar.getString() + ""
						+ " " + chargedProgressBar.getString()).replaceAll("%", "");
				StringSelection stringSelection = new StringSelection (myString);
				Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();
				clpbrd.setContents (stringSelection, null);
				
			}
		});
		btnCopyAll.setBounds(312, 11, 89, 23);
		controlPanel.add(btnCopyAll);
		
		multiRunsCheckBox = new JCheckBox("Multi Runs");
		multiRunsCheckBox.setBounds(6, 11, 97, 23);
		controlPanel.add(multiRunsCheckBox);
		
		fileCheckBox = new JCheckBox("File");
		fileCheckBox.setBounds(6, 37, 97, 23);
		fileCheckBox.setEnabled(false);
		controlPanel.add(fileCheckBox);
		
		JButton btnPlot = new JButton("Plot");
		btnPlot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				sm.showGraph();
			}
		});
		btnPlot.setBounds(168, 45, 89, 23);
		controlPanel.add(btnPlot);
		
		statsPanel = new JPanel();
		statsPanel.setBounds(312, 225, 411, 194);
		frmElectricVehicleAgent.getContentPane().add(statsPanel);
		statsPanel.setLayout(null);
		
		lblEnergyUsed = new JLabel("Energy Used");
		lblEnergyUsed.setBounds(10, 11, 80, 14);
		statsPanel.add(lblEnergyUsed);
		
		lblRenewablesUsed = new JLabel("Renewables Used");
		lblRenewablesUsed.setBounds(10, 36, 116, 14);
		statsPanel.add(lblRenewablesUsed);
		
		lblNonRenewablesUsed = new JLabel("Non Renewables Used");
		lblNonRenewablesUsed.setBounds(10, 61, 134, 14);
		statsPanel.add(lblNonRenewablesUsed);
		
		energyProgressBar = new JProgressBar();
		energyProgressBar.setForeground(new Color(50, 205, 50));
		energyProgressBar.setEnabled(true);
		energyProgressBar.setStringPainted(true);
		energyProgressBar.setBounds(255, 11, 146, 14);
		statsPanel.add(energyProgressBar);
		
		renProgressBar = new JProgressBar();
		renProgressBar.setForeground(new Color(50, 205, 50));
		renProgressBar.setStringPainted(true);
		renProgressBar.setBounds(255, 36, 146, 14);
		statsPanel.add(renProgressBar);
		
		nonRenProgressBar = new JProgressBar();
		nonRenProgressBar.setForeground(new Color(220, 20, 60));
		nonRenProgressBar.setStringPainted(true);
		nonRenProgressBar.setValue(0);
		nonRenProgressBar.setMinimum(0);
		nonRenProgressBar.setMaximum(100);
		nonRenProgressBar.setBounds(255, 61, 146, 14);
		statsPanel.add(nonRenProgressBar);
		
		lblCarsCharged = new JLabel("Cars Charged");
		lblCarsCharged.setBounds(10, 111, 116, 14);
		statsPanel.add(lblCarsCharged);
		
		chargedProgressBar = new JProgressBar();
		chargedProgressBar.setForeground(new Color(0, 0, 205));
		chargedProgressBar.setStringPainted(true);
		chargedProgressBar.setBounds(255, 113, 146, 14);
		statsPanel.add(chargedProgressBar);
		
		lblTimeSlotsUsed = new JLabel("Time Slots Used");
		lblTimeSlotsUsed.setBounds(10, 136, 116, 14);
		statsPanel.add(lblTimeSlotsUsed);
		
		slotProgressBar = new JProgressBar();
		slotProgressBar.setForeground(new Color(0, 0, 0));
		slotProgressBar.setStringPainted(true);
		slotProgressBar.setBounds(255, 138, 146, 14);
		statsPanel.add(slotProgressBar);
		
		lblRenewablesAll = new JLabel("Renewables/All Energy Used");
		lblRenewablesAll.setBounds(10, 86, 174, 14);
		statsPanel.add(lblRenewablesAll);

		energyAllProgressBar = new JProgressBar();
		energyAllProgressBar.setBackground(Color.WHITE);
		energyAllProgressBar.setForeground(new Color(50, 205, 50));
		energyAllProgressBar.setStringPainted(true);
		energyAllProgressBar.setBounds(255, 86, 146, 14);
		statsPanel.add(energyAllProgressBar);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 161, 391, 29);
		statsPanel.add(scrollPane);
		
		consoleScreen = new JTextPane();
		scrollPane.setViewportView(consoleScreen);
		
		JButton btnCopy = new JButton("copy");
		btnCopy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				String myString = energyProgressBar.getString().replace("%", "");
				StringSelection stringSelection = new StringSelection (myString);
				Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();
				clpbrd.setContents (stringSelection, null);
				
			}
		});
		btnCopy.setBounds(190, 7, 55, 23);
		statsPanel.add(btnCopy);
		
		JButton button_2 = new JButton("copy");
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				String myString = energyAllProgressBar.getString().replace("%", "");
				StringSelection stringSelection = new StringSelection (myString);
				Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();
				clpbrd.setContents (stringSelection, null);
				
			}
		});
		button_2.setBounds(190, 82, 55, 23);
		statsPanel.add(button_2);
		
		JButton button_3 = new JButton("copy");
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String myString = chargedProgressBar.getString().replace("%", "");
				StringSelection stringSelection = new StringSelection (myString);
				Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();
				clpbrd.setContents (stringSelection, null);
				
			}
		});
		button_3.setBounds(190, 107, 55, 23);
		statsPanel.add(button_3);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(312, 11, 411, 215);
		frmElectricVehicleAgent.getContentPane().add(tabbedPane);
		
		//bySlotPane = new JScrollPane();
		//bySlotPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		//tabbedPane.addTab("By Slot", null, bySlotPane, null);
		

		bySlotTable = new JTable();
		bySlotTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		//bySlotPane.setViewportView(bySlotTable);

		
		byCarTable = new JTable();
		byCarTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		//byCarTable.setBounds(270, 11, 600, 800);
	
		//byCarPane.setViewportView(byCarTable);
		
		table_model_car = new DefaultTableModel(){

		    @Override
		    public boolean isCellEditable(int row, int column) {
		       //all cells false
		       return false;
		    }
		};
		
		table_model_slot = new DefaultTableModel(){

		    @Override
		    public boolean isCellEditable(int row, int column) {
		       //all cells false
		       return false;
		    }
		};
		table_model_car.addColumn("Car");
		table_model_car.addColumn("Time Slots");

		table_model_slot.addColumn("Slot");
		table_model_slot.addColumn("Cars");
		//table_model_slot.addRow(new Object[]{1,2});
		
		byCarTable.setModel(table_model_car);
		bySlotTable.setModel(table_model_slot);
		
		bySlotTable.getColumnModel().getColumn(0).setMaxWidth(30);
		bySlotTable.getColumnModel().getColumn(0).setMinWidth(30);
		
		byCarTable.getColumnModel().getColumn(0).setMaxWidth(30);
		byCarTable.getColumnModel().getColumn(0).setMinWidth(30);
		
		
		byCarTable.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(final MouseEvent e) {
		        if (e.getClickCount() == 1) {
		            JTable target = (JTable)e.getSource();
		            int row = target.getSelectedRow();
		            consoleScreen.setText(""+byCarTable.getModel().getValueAt(row, 0));
		            int position = (int) byCarTable.getModel().getValueAt(row, 0);
		            consoleScreen.setText("Was available from: " + car_to_slot.get(position - 1).getStartTime() + " to "
		            		+ car_to_slot.get(position - 1).getEndTime() + " and its needs was: " + car_to_slot.get(position - 1).getNeeds() + " energy units.");
		        }
		    }
		});

		//byCarTable.getColumnModel().getColumn(1).setMaxWidth(800);
		//byCarTable.getColumnModel().getColumn(1).setMinWidth(800);
		
		
		byCarPane = new JScrollPane(byCarTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		byCarPane.setVerticalScrollBar(byCarPane.createVerticalScrollBar());
		tabbedPane.addTab("By Car", null, byCarPane, null);
		

		bySlotPane = new JScrollPane(bySlotTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		bySlotPane.setVerticalScrollBar(bySlotPane.createVerticalScrollBar());
		tabbedPane.addTab("By Slot", null, bySlotPane, null);

		
		model = new Model();
		
	}
	
	
	
	
	public void computeResults(int cars_num)
	{
		model = new Model();
		System.out.println("Cars: "+ cars_num);
		model.createAndRunModel(dt.getCars(cars_num), dt.getTime_slots(),
				dt.getEnergy(), dt.getChargers(), dt.getRenewable_energy(), dt.getNon_renewable_energy(), w1, w2, w3);
		
		renProgressBar.setValue(model.getRenEnergy());
		nonRenProgressBar.setValue(model.getNonRenEnergy());
		energyProgressBar.setValue(model.getEnergy());
		energyAllProgressBar.setValue(model.getRenewable_all_used());
		slotProgressBar.setValue(model.getSlots_used());
		chargedProgressBar.setValue(model.getCharged());
		
		car_to_slot = model.getCar_to_slot();

		
		int counter = 1;
    	for(Car c : car_to_slot)
    	{
    		if(!c.getSlots().isEmpty())
    		{
	    		StringBuilder strb = new StringBuilder();
	    		for(Integer t: c.getSlots())
	    		{
	    			strb.append(t + ", ");
	    			
	    			//System.out.println(t);
	    		}
	    		String print = strb.toString();
	    		print = print.substring(0, print.length()-2);
	    		table_model_car.addRow(new Object[]{counter, print});
	    		
    		}
    		counter++;
    	}
    	
    	
    	
    	
    	int rows = table_model_car.getRowCount();
    	int max = -1;
    	for(int i = 0; i < rows; i++)
    	{
    		TableCellRenderer cellRenderer = byCarTable.getCellRenderer(i, 1);
            Object valueAt = byCarTable.getValueAt(i, 1);
            Component tableCellRendererComponent = cellRenderer.getTableCellRendererComponent(byCarTable, valueAt, false, false, i, 1);
            int heightPreferable = tableCellRendererComponent.getPreferredSize().width;
            max = Math.max(heightPreferable, max);
        }

		byCarTable.getColumnModel().getColumn(1).setMaxWidth(max + 25);
		byCarTable.getColumnModel().getColumn(1).setMinWidth(max + 25);
		
		
		// print slot to car
		slot_to_car = model.getSlot_to_car();
		
		Set<Integer> keyset = slot_to_car.keySet();
		
		for(Integer key : keyset)
		{
			StringBuilder strb = new StringBuilder();
			for(Integer t : slot_to_car.get(key))
			{
				strb.append((t + 1) + ", ");
			}
			String print = strb.toString();
			print = print.substring(0, print.length()-2);
			table_model_slot.addRow(new Object[]{key, print});
		}
		
    	rows = table_model_slot.getRowCount();
    	max = -1;
    	for(int i = 0; i < rows; i++)
    	{
    		TableCellRenderer cellRenderer = bySlotTable.getCellRenderer(i, 1);
            Object valueAt = bySlotTable.getValueAt(i, 1);
            Component tableCellRendererComponent = cellRenderer.getTableCellRendererComponent(bySlotTable, valueAt, false, false, i, 1);
            int heightPreferable = tableCellRendererComponent.getPreferredSize().width;
            max = Math.max(heightPreferable, max);
        }

    	bySlotTable.getColumnModel().getColumn(1).setMaxWidth(max + 25);
    	bySlotTable.getColumnModel().getColumn(1).setMinWidth(max + 25);
		
		btnSaveAs.setEnabled(true);
	}
}

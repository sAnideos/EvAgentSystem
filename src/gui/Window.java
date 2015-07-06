package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
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
import javax.swing.MenuSelectionManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicCheckBoxMenuItemUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import source.DataGenerator;
import source.Model;
import source.Results;
import source.Test;
import source.StatsManagement;

import javax.swing.JTextPane;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.JCheckBox;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JList;
import javax.swing.border.BevelBorder;
import javax.swing.JTextField;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JSpinner;

import customDynamic.Dynamic;

import javax.swing.JRadioButton;

public class Window {

	// model
	private Model model;
	private DataGenerator dt;
	private int energy_range = 1;
	private int evs = 1;
	private int time_slots = 2;
	private int chargers = 1;
	
	private int[] data = new int[4];
	private String path;
	
	private double w1 = 0.35, w2 = 0.35, w3 = 0.30;
	private double dw1 = 1.0, dw2 = 0.0, dw3 = 0.0; // weight for the dynamic... 1 - by less load, 2 - by more energy, 3 - consecutive
	private ArrayList<String> car_to_slot;
	private HashMap<Integer, ArrayList<Integer>> slot_to_car;
	private StatsManagement sm = new StatsManagement();
	private Test s;
	private int computed = 0; // 0 - not computed solution, 1 - computed solution
	private int algorithm = 0; // 0 - static, 1 - custom dynamic, 2 - cplex dynamic
	private Results r; // the results of the run
	private Test t; // the results of the multirun
	
	
	public static class StayOpenCheckBoxMenuItemUI extends BasicCheckBoxMenuItemUI {

		   protected void doClick(MenuSelectionManager msm) {
		      menuItem.doClick(0);
		   }

		   public static ComponentUI createUI(JComponent c) {
		      return new StayOpenCheckBoxMenuItemUI();
		   }
		}
	
	public class MeThread implements Runnable {

		
		
	    public void run() {

	    	s = new Test();
	    	
	    	btnCompute.setEnabled(false);
	    	btnRandom.setEnabled(false);
	    	mntmOpen.setEnabled(false);
	    	moreSlotsSlider.setEnabled(false);
	    	moreChargedSlider.setEnabled(false);
	    	btnAddTest.setEnabled(false);
	    	
	    	
			int start = (int) startSpinner.getValue();
			int rate = (int) rateSpinner.getValue();
			if(algorithm == 0) // offline
			{
				model = new Model(dt, w1, w2, w3);
				t = model.multiRunsStatic(start, rate);
				chargedProgressBar.setValue(t.getCarsChargedAverage());
				energyAllProgressBar.setValue(t.getRenewablesPerAllAverage()); 
				energyProgressBar.setValue(t.getEnergyAverage());
				nonRenProgressBar.setValue(t.getNonRenewablesAverage());
				renProgressBar.setValue(t.getReneablesAverage());
				slotProgressBar.setValue(t.getSlotsUsedAverage());
			}
			else if(algorithm == 1)//custom online
			{
				Dynamic d = new Dynamic(dt, dw1, dw2, dw3);
				t = d.multiRun(start, rate);
				chargedProgressBar.setValue(t.getCarsChargedAverage());
				energyAllProgressBar.setValue(t.getRenewablesPerAllAverage()); 
				energyProgressBar.setValue(t.getEnergyAverage());
				nonRenProgressBar.setValue(t.getNonRenewablesAverage());
				renProgressBar.setValue(t.getReneablesAverage());
				slotProgressBar.setValue(t.getSlotsUsedAverage());
			}
			else // cplex offline
			{
				model = new Model(dt, w1, w2, w3);
				t = model.multiRealTimeRun(start, rate);
				chargedProgressBar.setValue(t.getCarsChargedAverage());
				energyAllProgressBar.setValue(t.getRenewablesPerAllAverage()); 
				energyProgressBar.setValue(t.getEnergyAverage());
				nonRenProgressBar.setValue(t.getNonRenewablesAverage());
				renProgressBar.setValue(t.getReneablesAverage());
				slotProgressBar.setValue(t.getSlotsUsedAverage()); 
			}
	    	btnCompute.setEnabled(true);
	    	btnRandom.setEnabled(true);
	    	mntmOpen.setEnabled(true);
	    	moreSlotsSlider.setEnabled(true);
	    	moreChargedSlider.setEnabled(true);
	    	btnAddTest.setEnabled(true);
	    	
	    
	    }


	    public void setStatsManagement(double cars_n)
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
				computed = 1;
	        	table_model_car.setRowCount(0);
	        	table_model_slot.setRowCount(0);
				consoleScreen.setText(null);
				
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
				if(computed == 1)
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
						    	model.getDataGenerator().writeToFile(path);
						    } 
	
					   }
					   else
					   {
						   model.getDataGenerator().writeToFile(path);
					   }
					   
					}
					energyAllProgressBar.setBackground(Color.RED);
				}
				else
				{
					consoleScreen.setText("Compute the solution first!");
				}
			}
			else if(e.getActionCommand().compareTo("GenerateData") == 0)
			{
				dt = new DataGenerator(data[0], data[1], data[2], data[3]);
				dt.generateAllData();
			}
			else if(e.getActionCommand().compareTo("open") == 0)
			{
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int option = chooser.showOpenDialog(frmElectricVehicleAgent); // parentComponent must a component like JFrame, JDialog...
				if (option == JFileChooser.APPROVE_OPTION) {
				   File selectedFile = chooser.getSelectedFile();
				   path = selectedFile.getAbsolutePath();
				}
				dt = new DataGenerator(0, 0, 0, 0);
				dt.readFromFile(path);
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
			else if(e.getActionCommand().compareTo("add_test") == 0)
			{
				String temp = testNameTextField.getText();
				
				if(!temp.equals(""))
				{
					
					DefaultListModel<String> model2 = (DefaultListModel<String>) listModel;
					if(!model2.contains(temp))
					{
						listModel.addElement(temp);
						t.setTestName(temp);
						sm.addStats(t);
					}
					else
					{
						consoleScreen.setText(null);
						consoleScreen.setText("Use a different name!");
					}
					
				}
			}
			else if(e.getActionCommand().compareTo("remove_test") == 0)
			{
				System.out.println(testsList.getSelectedIndex());
				listModel.remove(testsList.getSelectedIndex());			
				String name = testsList.getSelectedValue();
				sm.removeTest(name);
			}
			else if(e.getActionCommand().compareTo("multi_checked") == 0)
			{
				if(multiRunsCheckBox.isSelected())
				{
					startSpinner.setVisible(true);
					rateSpinner.setVisible(true);
					lblStart.setVisible(true);
					lblRate.setVisible(true);
				}
				else
				{
					startSpinner.setVisible(false);
					rateSpinner.setVisible(false);
					lblStart.setVisible(false);
					lblRate.setVisible(false);
				}
			}
			else if(e.getActionCommand().compareTo("CplexOffline") == 0)
			{
				chckbxmntmCustomOnline.setSelected(false);
				chckbxmntmCplexOffline.setSelected(true);
				chckbxmntmCplexOnline.setSelected(false);
				algorithm = 0;
				
			}
			else if(e.getActionCommand().compareTo("CplexOnline") == 0)
			{
				chckbxmntmCustomOnline.setSelected(false);
				chckbxmntmCplexOffline.setSelected(false);
				chckbxmntmCplexOnline.setSelected(true);
				algorithm = 2;
			}
			else if(e.getActionCommand().compareTo("CustomOnline") == 0)
			{
				chckbxmntmCustomOnline.setSelected(true);
				chckbxmntmCplexOffline.setSelected(false);
				chckbxmntmCplexOnline.setSelected(false);
				algorithm = 1;
			}
			else if(e.getActionCommand().compareTo("ByLoad") == 0)
			{
				if(rdbtnLoad.isSelected())
				{
					dw1 = 1.0;
				}
				else
				{
					dw1 = 0.0;
				}
			}
			else if(e.getActionCommand().compareTo("ByEnergy") == 0)
			{
				if(rdbtnEnergy.isSelected())
				{
					dw2 = 1.0;
				}
				else
				{
					dw2 = 0.0;
				}
			}
			else if(e.getActionCommand().compareTo("Consecutive") == 0)
			{
				if(rdbtnConsecutive.isSelected())
				{
					dw3 = 1.0;
				}
				else
				{
					dw3 = 0.0;
				}
			}
			else if(e.getActionCommand().compareTo("plot_ren") == 0)
			{
				sm.showGraph("Renewables");
			}
			else if(e.getActionCommand().compareTo("energy_plot") == 0)
			{
				sm.showGraph("Energy");
			}
			else if(e.getActionCommand().compareTo("plot_non_ren") == 0)
			{
				sm.showGraph("Non Renewables");
			}
			else if(e.getActionCommand().compareTo("plot_ren_total") == 0)
			{
				sm.showGraph("Renewables/Total");
			}
			else if(e.getActionCommand().compareTo("plot_charged") == 0)
			{
				sm.showGraph("Cars Charged");
			}
			else if(e.getActionCommand().compareTo("plot_slots_used") == 0)
			{
				sm.showGraph("Slots Used");
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
        	   data[3] = source.getValue();
        	   energyTextPane.setText("" + energy_range);
        	   //System.out.println("energy: " + energy_range);
           }
           else if (source.getName().equals("car"))
           {
        	   evs = source.getValue();
        	   data[0] = source.getValue();
        	   carTextPane.setText("" + evs);
        	   //System.out.println("evs: " + evs);
        	   energySlider.setMaximum(evs);
           }
           else if (source.getName().equals("slot"))
           {
        	   time_slots = source.getValue();
        	   data[1] = source.getValue();
        	   slotTextPane.setText("" + time_slots);
        	  //System.out.println("time_slots: " + time_slots);
           }
           else if(source.getName().equals("charger"))
           {
        	   chargers = source.getValue();
        	   data[2] = source.getValue();
        	   chargerTextPane.setText("" + chargers);
        	   System.out.println("chargers: " + chargers);
           }
           else if(source.getName().equals("ren_w")) // more charged
           {
				w2 = (source.getValue() / 100.0);

				w1 = ((100.0 - source.getValue())) / 100.0;

				
				//System.out.println("W2: " + w2 + " W3: " + w3);
				//w2 = (1.0 - w1) / 2;
				//System.out.println(w2);
				DecimalFormat df = new DecimalFormat("0.00"); 
				String temp = df.format(w1).replace(",", ".");
				slotsWeightPane.setText(temp);
				
				
				temp = df.format(w2).replace(",", ".");
				renWeightPane.setText(temp + "");
				
				moreSlotsSlider.setValue((int)(w1*100));
				

				System.out.println("w1: " + w1 + " w2: " + w2 + " w3: " + w3);
           }
           else if(source.getName().equals("slots_w")) // edw to varos tha kataligei panta se 0 h' 5, den kserw an einai kako, emena kalo mou fainetai... dld de tha ginei pote 0.71 px
           {
        	   
				w1 = (source.getValue() / 100.0);

				w2 = ((100.0 - source.getValue())) / 100.0;

				
				//System.out.println("W2: " + w2 + " W3: " + w3);
				//w2 = (1.0 - w1) / 2;
				//System.out.println(w2);
				DecimalFormat df = new DecimalFormat("0.00"); 
				String temp = df.format(w1).replace(",", ".");
				slotsWeightPane.setText(temp);
				
				
				temp = df.format(w2).replace(",", ".");
				renWeightPane.setText(temp + "");
				
				moreChargedSlider.setValue((int)(w2*100));
				
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
	private JButton plusBtnEnergy;
	private JButton plusBtnSlots;
	private JButton plusBtnChargers;
	private JButton minusBtnCars;
	private JButton minusBtnEnergy;
	private JButton minusBtnSlots;
	private JButton minusBtnChargers;
	private JSlider moreSlotsSlider;	
	private JSlider moreChargedSlider;
	private JTextPane slotsWeightPane;
	private JTextPane renWeightPane;
	private JCheckBox multiRunsCheckBox;
	private JMenuBar menuBar;	
	private JMenu fileMenu;	
	private JMenuItem mntmOpen;	
	private JMenuItem mntmSaveAs;	
	private JMenu plotMenu;
	private JButton btnAddTest;
	private JScrollPane testListPane;
	private JList<String> testsList;
	private JTextField testNameTextField;
	private DefaultListModel<String> listModel;
	private JButton btnRemoveTest;
	private JMenuItem mntmEnergyUsed;
	private JMenuItem mntmRenewablesUsed;
	private JMenuItem mntmNonRenewablesUsed;
	private JMenuItem mntmRenewablestotalUsed;
	private JMenuItem mntmCarsCharged;
	private JMenuItem mntmSlotsUsed;
	private JMenu mnPlotMultiTests;
	private JMenu mnPlotSingleTest;
	private JCheckBoxMenuItem nonRenPlotCheck;
	private JCheckBoxMenuItem energyPlotCheck;
	private JCheckBoxMenuItem energyTotalPlotCheck;
	private JCheckBoxMenuItem chargedPlotCheck;
	private JCheckBoxMenuItem slotsPlotCheck;
	private JMenuItem mntmPlot;
	private JCheckBoxMenuItem renewablesPlotCheck;
	private JSpinner startSpinner = new JSpinner();
	private JSpinner rateSpinner;
	private JLabel lblStart;
	private JLabel lblRate;
	private JMenu mnAlgorithm;
	private JCheckBoxMenuItem chckbxmntmCplexOffline;
	private JCheckBoxMenuItem chckbxmntmCplexOnline;
	private JCheckBoxMenuItem chckbxmntmCustomOnline;
	private JButton btnGenerateData;
	private JRadioButton rdbtnLoad;
	private JRadioButton rdbtnEnergy;
	private JRadioButton rdbtnConsecutive;
	private JLabel energyLabel;	
	private JLabel renLabel;
	private JLabel nonRenLabel;
	private JLabel renAllLabel;
	private JLabel chargedLabel;
	private JLabel slotsLabel;
	private JPanel customPanel;
	private JLabel lblCustomDynamic;
	private JPanel cplexPanel;
	
	
// ---------------------- MAIN ---------------------------
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
		frmElectricVehicleAgent.setBounds(100, 100, 756, 616);
		frmElectricVehicleAgent.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmElectricVehicleAgent.getContentPane().setLayout(null);
		
		settingsPanel = new JPanel();
		settingsPanel.setBounds(10, 31, 292, 545);
		frmElectricVehicleAgent.getContentPane().add(settingsPanel);
		settingsPanel.setLayout(null);
		
		lblEnergy = new JLabel("Energy Range");
		lblEnergy.setBounds(10, 70, 89, 14);
		settingsPanel.add(lblEnergy);
		
		energySlider = new JSlider();
		energySlider.setMajorTickSpacing(5);
		energySlider.setToolTipText("");
		energySlider.setPaintTicks(true);
		energySlider.setMinimum(1);
		energySlider.setValue(1);
		energySlider.setBounds(10, 95, 163, 23);
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
		lblTimeSlots.setBounds(10, 129, 71, 14);
		settingsPanel.add(lblTimeSlots);
		
		slotSlider = new JSlider();
		slotSlider.setMaximum(287);
		slotSlider.setMinorTickSpacing(5);
		slotSlider.setPaintTicks(true);
		slotSlider.setValue(2);
		slotSlider.setMinimum(2);
		slotSlider.setBounds(10, 154, 163, 23);
		slotSlider.setName("slot");
		slotSlider.addChangeListener(change);
		settingsPanel.add(slotSlider);
		
		lblChargers = new JLabel("Chargers");
		lblChargers.setBounds(10, 188, 61, 14);
		settingsPanel.add(lblChargers);
		
		chargerSlider = new JSlider();
		chargerSlider.setMinorTickSpacing(5);
		chargerSlider.setValue(1);
		chargerSlider.setMinimum(1);
		chargerSlider.setPaintTicks(true);
		chargerSlider.setBounds(10, 213, 163, 23);
		chargerSlider.setName("charger");
		chargerSlider.addChangeListener(change);
		settingsPanel.add(chargerSlider);
		
		btnRandom = new JButton("Randomize");
		btnRandom.setActionCommand("random");
		btnRandom.addActionListener(action);
		btnRandom.setBounds(10, 270, 101, 23);
		settingsPanel.add(btnRandom);
		
		energyTextPane = new JTextPane();
		energyTextPane.setText("1");
		energyTextPane.setBounds(240, 98, 34, 20);
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
		slotTextPane.setBounds(240, 157, 34, 20);
		slotTextPane.setBorder(blackline);
		slotTextPane.setEditable(false);
		settingsPanel.add(slotTextPane);
		
		chargerTextPane = new JTextPane();
		chargerTextPane.setText("1");
		chargerTextPane.setBounds(240, 216, 34, 20);
		chargerTextPane.setBorder(blackline);
		chargerTextPane.setEditable(false);
		settingsPanel.add(chargerTextPane);
		
		JButton plusBtnCars = new JButton("+");
		plusBtnCars.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				carSlider.setValue(carSlider.getValue() + 1);
			}
		});
		plusBtnCars.setMargin(new Insets(0,0,0,0));
		plusBtnCars.setBounds(206, 36, 24, 23);
		settingsPanel.add(plusBtnCars);
		
		plusBtnEnergy = new JButton("+");
		plusBtnEnergy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				energySlider.setValue(energySlider.getValue() + 1);
			}
		});
		plusBtnEnergy.setMargin(new Insets(0, 0, 0, 0));
		plusBtnEnergy.setBounds(206, 95, 24, 23);
		settingsPanel.add(plusBtnEnergy);
		
		plusBtnSlots = new JButton("+");
		plusBtnSlots.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				slotSlider.setValue(slotSlider.getValue() + 1);
			}
		});
		plusBtnSlots.setMargin(new Insets(0, 0, 0, 0));
		plusBtnSlots.setBounds(206, 154, 24, 23);
		settingsPanel.add(plusBtnSlots);
		
		plusBtnChargers = new JButton("+");
		plusBtnChargers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chargerSlider.setValue(chargerSlider.getValue() + 1);
			}
		});
		plusBtnChargers.setMargin(new Insets(0, 0, 0, 0));
		plusBtnChargers.setBounds(206, 213, 24, 23);
		settingsPanel.add(plusBtnChargers);
		
		minusBtnCars = new JButton("-");
		minusBtnCars.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				carSlider.setValue(carSlider.getValue() - 1);
			}
		});
		minusBtnCars.setMargin(new Insets(0, 0, 0, 0));
		minusBtnCars.setBounds(183, 36, 24, 23);
		settingsPanel.add(minusBtnCars);
		
		minusBtnEnergy = new JButton("-");
		minusBtnEnergy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				energySlider.setValue(energySlider.getValue() - 1);
			}
		});
		minusBtnEnergy.setMargin(new Insets(0, 0, 0, 0));
		minusBtnEnergy.setBounds(183, 95, 24, 23);
		settingsPanel.add(minusBtnEnergy);
		
		minusBtnSlots = new JButton("-");
		minusBtnSlots.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				slotSlider.setValue(slotSlider.getValue() - 1);
				
			}
		});
		minusBtnSlots.setMargin(new Insets(0, 0, 0, 0));
		minusBtnSlots.setBounds(183, 154, 24, 23);
		settingsPanel.add(minusBtnSlots);
		
		minusBtnChargers = new JButton("-");
		minusBtnChargers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chargerSlider.setValue(chargerSlider.getValue() - 1);
			}
		});
		minusBtnChargers.setMargin(new Insets(0, 0, 0, 0));
		minusBtnChargers.setBounds(183, 213, 24, 23);
		settingsPanel.add(minusBtnChargers);
		
		
		btnGenerateData = new JButton("Generate Data");
		btnGenerateData.addActionListener(action);
		btnGenerateData.setActionCommand("GenerateData");
		btnGenerateData.setBounds(173, 270, 101, 23);
		btnGenerateData.setMargin(new Insets(0, 0, 0, 0));
		settingsPanel.add(btnGenerateData);
		
		customPanel = new JPanel();
		customPanel.setBounds(164, 335, 118, 199);
		settingsPanel.add(customPanel);
		customPanel.setLayout(null);
		customPanel.setBorder(blackline);
		
		rdbtnLoad = new JRadioButton("Load");
		rdbtnLoad.setHorizontalAlignment(SwingConstants.LEFT);
		rdbtnLoad.setBounds(20, 48, 62, 23);
		customPanel.add(rdbtnLoad);
		rdbtnLoad.setSelected(true);
		rdbtnLoad.addActionListener(action);
		rdbtnLoad.setActionCommand("ByLoad");

		
		lblCustomDynamic = new JLabel("Custom Dynamic");
		lblCustomDynamic.setHorizontalAlignment(SwingConstants.CENTER);
		Border redBorder = BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLUE);
		lblCustomDynamic.setBorder(redBorder);
		lblCustomDynamic.setBounds(10, 11, 98, 14);
		customPanel.add(lblCustomDynamic);
		
		rdbtnEnergy = new JRadioButton("Energy");
		rdbtnEnergy.setHorizontalAlignment(SwingConstants.LEFT);
		rdbtnEnergy.setBounds(20, 97, 62, 23);
		customPanel.add(rdbtnEnergy);
		rdbtnEnergy.addActionListener(action);
		rdbtnEnergy.setActionCommand("ByEnergy");
		
		rdbtnConsecutive = new JRadioButton("Consecutive");
		rdbtnConsecutive.setBounds(20, 149, 85, 23);
		customPanel.add(rdbtnConsecutive);
		rdbtnConsecutive.addActionListener(action);
		rdbtnConsecutive.setActionCommand("Consecutive");
		
		cplexPanel = new JPanel();
		cplexPanel.setBounds(10, 335, 152, 199);
		settingsPanel.add(cplexPanel);
		redBorder = BorderFactory.createMatteBorder(1, 1, 1, 0, Color.BLACK);
		cplexPanel.setBorder(redBorder);
		cplexPanel.setLayout(null);
		
		
		JLabel lblObjectiveFunctionWeight = new JLabel("Objective Function Weight");
		lblObjectiveFunctionWeight.setBounds(9, 11, 136, 14);
		cplexPanel.add(lblObjectiveFunctionWeight);
		redBorder = BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLUE);
		lblObjectiveFunctionWeight.setBorder(redBorder);
		lblObjectiveFunctionWeight.setHorizontalAlignment(SwingConstants.CENTER);
		
		
		
		moreSlotsSlider = new JSlider();
		moreSlotsSlider.setBounds(19, 36, 34, 96);
		cplexPanel.add(moreSlotsSlider);
		moreSlotsSlider.setOrientation(SwingConstants.VERTICAL);
		moreSlotsSlider.setName("slots_w");
		moreSlotsSlider.addChangeListener(change);
		moreSlotsSlider.setMajorTickSpacing(5);
		moreSlotsSlider.setSnapToTicks(true);
		moreSlotsSlider.setPaintTicks(true);
		
		
		
		moreChargedSlider = new JSlider();
		moreChargedSlider.setBounds(93, 36, 34, 96);
		cplexPanel.add(moreChargedSlider);
		moreChargedSlider.setName("ren_w");
		moreChargedSlider.addChangeListener(change);
		moreChargedSlider.setSnapToTicks(true);
		moreChargedSlider.setPaintTicks(true);
		moreChargedSlider.setOrientation(SwingConstants.VERTICAL);
		moreChargedSlider.setMajorTickSpacing(5);
		
		
		slotsWeightPane = new JTextPane();
		slotsWeightPane.setBounds(19, 137, 34, 20);
		cplexPanel.add(slotsWeightPane);
		slotsWeightPane.setText("0.50");
		slotsWeightPane.setEditable(false);
		slotsWeightPane.setBorder(blackline);
		
		renWeightPane = new JTextPane();
		renWeightPane.setBounds(93, 137, 34, 20);
		cplexPanel.add(renWeightPane);
		renWeightPane.setText("0.50");
		renWeightPane.setEditable(false);
		renWeightPane.setBorder(blackline);
		

		
		JLabel lblMoreSlots = new JLabel("More Slots");
		lblMoreSlots.setBounds(10, 165, 61, 14);
		cplexPanel.add(lblMoreSlots);
		
		
		JLabel lblMoreRenewables = new JLabel("More Charged");
		lblMoreRenewables.setHorizontalAlignment(SwingConstants.CENTER);
		lblMoreRenewables.setBounds(73, 165, 79, 14);
		cplexPanel.add(lblMoreRenewables);
		
		
		
		
		
		controlPanel = new JPanel();
		controlPanel.setBounds(312, 462, 411, 114);
		frmElectricVehicleAgent.getContentPane().add(controlPanel);
		controlPanel.setLayout(null);
		
		multiRunsCheckBox = new JCheckBox("Multi Runs");
		multiRunsCheckBox.setBounds(6, 11, 89, 23);
		multiRunsCheckBox.addActionListener(action);
		multiRunsCheckBox.setActionCommand("multi_checked");
		controlPanel.add(multiRunsCheckBox);
		

		
		testListPane = new JScrollPane();
		testListPane.setBounds(312, 11, 89, 73);
		controlPanel.add(testListPane);
		listModel = new DefaultListModel<String>();
		testsList = new JList<String>(listModel);
		testListPane.setViewportView(testsList);
		
		testsList.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		btnAddTest = new JButton("Add Test");
		btnAddTest.setEnabled(false);
		btnAddTest.addActionListener(action);
		btnAddTest.setActionCommand("add_test");
		btnAddTest.setBounds(213, 11, 89, 23);
		controlPanel.add(btnAddTest);
		
		testNameTextField = new JTextField();
		testNameTextField.setBounds(213, 64, 89, 20);
		controlPanel.add(testNameTextField);
		testNameTextField.setColumns(10);
		
		btnRemoveTest = new JButton("Remove Test");
		btnRemoveTest.addActionListener(action);
		btnRemoveTest.setActionCommand("remove_test");
		btnRemoveTest.setBounds(213, 37, 89, 23);
		btnRemoveTest.setMargin(new Insets(0,0,0,0));
		controlPanel.add(btnRemoveTest);
		
		btnCompute = new JButton("Compute");
		btnCompute.setBounds(10, 80, 89, 23);
		controlPanel.add(btnCompute);
		btnCompute.setActionCommand("compute");
		
		startSpinner = new JSpinner();
		startSpinner.setBounds(100, 12, 44, 20);
		startSpinner.setVisible(false);
		controlPanel.add(startSpinner);
		
		rateSpinner = new JSpinner();
		rateSpinner.setBounds(159, 12, 44, 20);
		rateSpinner.setVisible(false);
		controlPanel.add(rateSpinner);
		
		lblStart = new JLabel("Start");
		lblStart.setBounds(100, 41, 29, 14);
		lblStart.setVisible(false);
		controlPanel.add(lblStart);
		
		lblRate = new JLabel("Rate");
		lblRate.setBounds(159, 41, 29, 14);
		lblRate.setVisible(false);
		controlPanel.add(lblRate);
		

		btnCompute.addActionListener(action);
		
		statsPanel = new JPanel();
		statsPanel.setBounds(312, 257, 411, 194);
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
		
		energyLabel = new JLabel("");
		energyLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		energyLabel.setBounds(141, 11, 104, 14);
		statsPanel.add(energyLabel);
		
		renLabel = new JLabel("");
		renLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		renLabel.setBounds(136, 36, 109, 14);
		statsPanel.add(renLabel);
		
		nonRenLabel = new JLabel("");
		nonRenLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		nonRenLabel.setBounds(141, 61, 104, 14);
		statsPanel.add(nonRenLabel);
		
	    renAllLabel = new JLabel("");
	    renAllLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		renAllLabel.setBounds(165, 84, 80, 14);
		statsPanel.add(renAllLabel);
		
		chargedLabel = new JLabel("");
		chargedLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		chargedLabel.setBounds(136, 111, 109, 14);
		statsPanel.add(chargedLabel);
		
		slotsLabel = new JLabel("");
		slotsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		slotsLabel.setBounds(141, 136, 104, 14);
		statsPanel.add(slotsLabel);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(312, 31, 411, 215);
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
		            consoleScreen.setText(r.getCarInfo(position - 1));
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
		
		menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 750, 20);
		frmElectricVehicleAgent.getContentPane().add(menuBar);
		
		fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		
		mntmOpen = new JMenuItem("Open");
		mntmOpen.addActionListener(action);
		mntmOpen.setActionCommand("open");
		fileMenu.add(mntmOpen);
		
		mntmSaveAs = new JMenuItem("Save As");
		mntmSaveAs.addActionListener(action);
		mntmSaveAs.setActionCommand("save_as");
		mntmSaveAs.setEnabled(false);
		fileMenu.add(mntmSaveAs);
		
		mnAlgorithm = new JMenu("Algorithm");
		menuBar.add(mnAlgorithm);
		
		chckbxmntmCplexOffline = new JCheckBoxMenuItem("Cplex Offline");
		chckbxmntmCplexOffline.setSelected(true);
		chckbxmntmCplexOffline.addActionListener(action);
		chckbxmntmCplexOffline.setActionCommand("CplexOffline");
		mnAlgorithm.add(chckbxmntmCplexOffline);
		
		chckbxmntmCplexOnline = new JCheckBoxMenuItem("Cplex Online");
		chckbxmntmCplexOnline.setSelected(false);
		chckbxmntmCplexOnline.addActionListener(action);
		chckbxmntmCplexOnline.setActionCommand("CplexOnline");
		mnAlgorithm.add(chckbxmntmCplexOnline);
		
		chckbxmntmCustomOnline = new JCheckBoxMenuItem("Custom Online");
		chckbxmntmCustomOnline.setSelected(false);
		chckbxmntmCustomOnline.addActionListener(action);
		chckbxmntmCustomOnline.setActionCommand("CustomOnline");
		mnAlgorithm.add(chckbxmntmCustomOnline);
		
		plotMenu = new JMenu("Plot");
		menuBar.add(plotMenu);
		
		mnPlotMultiTests = new JMenu("Plot Multi Tests");
		plotMenu.add(mnPlotMultiTests);
		
		mntmEnergyUsed = new JMenuItem("Energy Used");
		mnPlotMultiTests.add(mntmEnergyUsed);
		mntmEnergyUsed.addActionListener(action);
		mntmEnergyUsed.setActionCommand("energy_plot");
		
		mntmRenewablesUsed = new JMenuItem("Renewables Used");
		mnPlotMultiTests.add(mntmRenewablesUsed);
		mntmRenewablesUsed.setActionCommand("plot_ren");
		
		mntmNonRenewablesUsed = new JMenuItem("Non Renewables Used");
		mnPlotMultiTests.add(mntmNonRenewablesUsed);
		mntmNonRenewablesUsed.setActionCommand("plot_non_ren");
		
		mntmRenewablestotalUsed = new JMenuItem("Renewables/Total Used");
		mnPlotMultiTests.add(mntmRenewablestotalUsed);
		mntmRenewablestotalUsed.setActionCommand("plot_ren_total");
		
		mntmCarsCharged = new JMenuItem("Cars Charged");
		mnPlotMultiTests.add(mntmCarsCharged);
		mntmCarsCharged.addActionListener(action);
		mntmCarsCharged.setActionCommand("plot_charged");
		
		mntmSlotsUsed = new JMenuItem("Slots Used");
		mnPlotMultiTests.add(mntmSlotsUsed);
		mntmSlotsUsed.setActionCommand("plot_slots_used");
		
		mnPlotSingleTest = new JMenu("Plot Single Test");
		plotMenu.add(mnPlotSingleTest);
		
		JMenu mnMultiRuns = new JMenu("Multi Runs");
		mnPlotSingleTest.add(mnMultiRuns);
		
		JMenuItem mntmChooseStats = new JMenuItem("Choose Stats");
		mnMultiRuns.add(mntmChooseStats);
		
		JMenu mnSingleRun = new JMenu("Single Run");
		mnPlotSingleTest.add(mnSingleRun);
		
		energyPlotCheck = new JCheckBoxMenuItem("Energy");
		energyPlotCheck.setUI(new StayOpenCheckBoxMenuItemUI());
		mnSingleRun.add(energyPlotCheck);
		
		renewablesPlotCheck = new JCheckBoxMenuItem("Renewables");
		renewablesPlotCheck.setUI(new StayOpenCheckBoxMenuItemUI());
		mnSingleRun.add(renewablesPlotCheck);
		
		nonRenPlotCheck = new JCheckBoxMenuItem("Non Renewables");
		nonRenPlotCheck.setUI(new StayOpenCheckBoxMenuItemUI());
		mnSingleRun.add(nonRenPlotCheck);
		
		energyTotalPlotCheck = new JCheckBoxMenuItem("Energy/Total");
		energyTotalPlotCheck.setUI(new StayOpenCheckBoxMenuItemUI());
		mnSingleRun.add(energyTotalPlotCheck);
		
		chargedPlotCheck = new JCheckBoxMenuItem("Cars Charged");
		chargedPlotCheck.setUI(new StayOpenCheckBoxMenuItemUI());
		mnSingleRun.add(chargedPlotCheck);
		
		slotsPlotCheck = new JCheckBoxMenuItem("Slots Used");
		slotsPlotCheck.setUI(new StayOpenCheckBoxMenuItemUI());
		mnSingleRun.add(slotsPlotCheck);
		
		mntmPlot = new JMenuItem("Plot!");
		mnSingleRun.add(mntmPlot);
		
		
		mntmSlotsUsed.addActionListener(action);
		mntmRenewablestotalUsed.addActionListener(action);
		mntmNonRenewablesUsed.addActionListener(action);
		mntmRenewablesUsed.addActionListener(action);

		
		//model = new Model();
		
		data[0] = 1;
		data[1] = 2;
		data[2] = 1;
		data[3] = 1;
		
		
	}
	
	
	
	
	public void staticRun()
	{
		
	}
	
	
	public void computeResults(int cars_num)
	{
		

		
		if(algorithm == 0) // offline
		{
			model = new Model(dt, w1, w2, w3);
			r = model.staticRun(-1);
		}
		else if(algorithm == 1)//custom online
		{
			Dynamic d = new Dynamic(dt, dw1, dw2, dw3);
			r = d.run(-1);
		}
		else // cplex offline
		{
			model = new Model(dt, w1, w2, w3);
			r = model.realTimeRun(-1);
		}
		r.printMap();
		chargedProgressBar.setValue((int)r.getCarChargedPercentage());
		energyAllProgressBar.setValue((int) r.getRenewablesPerAllPercentage()); 
		energyProgressBar.setValue((int) r.getEnergyUsedPercentage());
		nonRenProgressBar.setValue((int) r.getNonRenewablesUsedPercentage());
		renProgressBar.setValue((int) r.getRenewablesUsedPercentage());
		slotProgressBar.setValue((int) r.getSlotsUsedPercentage());
		
		energyLabel.setText(r.energyUsedString());
		renLabel.setText(r.renEnergyUsedString());
		chargedLabel.setText(r.carsChargedString());
		nonRenLabel.setText(r.nonRenEnergyUsedString());
		renAllLabel.setText(r.renPerAllEnergyString());
		slotsLabel.setText(r.slotsUsedString());
		
		car_to_slot = r.getCarToSlot();

		
		int counter = 1;
    	for(String c : car_to_slot)
    	{
	    	table_model_car.addRow(new Object[]{counter, c});	
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
		
		mntmSaveAs.setEnabled(true);
	}
}

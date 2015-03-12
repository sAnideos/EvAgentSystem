package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
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
import source.Model;

import javax.swing.JTextPane;
import javax.swing.JScrollPane;

public class Window {

	// model
	private Model model;
	private DataGenerator dt;
	private int energy_range = 1;
	private int evs = 1;
	private int time_slots = 2;
	private int chargers = 1;
	private ArrayList<Car> car_to_slot;
	private int read_file = 0; // 1 - button "File" was pressed, 0 - user input
	
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
				consoleScreen.setText(null);
				//System.out.println("time_slots: " + time_slots);
				if(!(read_file == 1))
				{
					dt = new DataGenerator(evs, time_slots, chargers, energy_range);
					dt.generateCarData();
					dt.generateEnergyData();
					dt.generateDiverseEnergy();					
				}
				read_file = 0;
				//consoleScreen.setText("" + read_file);
				model.createAndRunModel(dt.getCars(), dt.getTime_slots(),
						dt.getEnergy(), dt.getChargers(), dt.getRenewable_energy(), dt.getNon_renewable_energy());
				
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
           else
           {
        	   chargers = source.getValue();
        	   chargerTextPane.setText("" + chargers);
        	   //System.out.println("chargers: " + chargers);
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
		
		
		ChangeHandler change = new ChangeHandler();
		ActionHandler action = new ActionHandler();
		
		
		frmElectricVehicleAgent = new JFrame();
		frmElectricVehicleAgent.setTitle("Electric Vehicle Agent System");
		frmElectricVehicleAgent.setBounds(100, 100, 710, 492);
		frmElectricVehicleAgent.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmElectricVehicleAgent.getContentPane().setLayout(null);
		
		settingsPanel = new JPanel();
		settingsPanel.setBounds(10, 11, 250, 362);
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
		btnRandom.setBounds(72, 294, 101, 23);
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
				}
			}
		});
		btnFile.setBounds(72, 328, 101, 23);
		settingsPanel.add(btnFile);
		
		energyTextPane = new JTextPane();
		energyTextPane.setBounds(183, 83, 34, 20);
		energyTextPane.setBorder(blackline);
		energyTextPane.setEditable(false);
		settingsPanel.add(energyTextPane);
		
		carTextPane = new JTextPane();
		carTextPane.setBounds(183, 11, 34, 20);
		carTextPane.setBorder(blackline);
		carTextPane.setEditable(false);
		settingsPanel.add(carTextPane);
		
		slotTextPane = new JTextPane();
		slotTextPane.setBounds(183, 142, 34, 20);
		slotTextPane.setBorder(blackline);
		slotTextPane.setEditable(false);
		settingsPanel.add(slotTextPane);
		
		chargerTextPane = new JTextPane();
		chargerTextPane.setBounds(183, 212, 34, 20);
		chargerTextPane.setBorder(blackline);
		chargerTextPane.setEditable(false);
		settingsPanel.add(chargerTextPane);
		
		controlPanel = new JPanel();
		controlPanel.setBounds(270, 427, 411, 26);
		frmElectricVehicleAgent.getContentPane().add(controlPanel);
		controlPanel.setLayout(null);
		
		btnCompute = new JButton("Compute");
		btnCompute.setActionCommand("compute");
		btnCompute.addActionListener(action);
		btnCompute.setBounds(169, 0, 89, 23);
		controlPanel.add(btnCompute);
		
		statsPanel = new JPanel();
		statsPanel.setBounds(270, 234, 411, 194);
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
		energyAllProgressBar.setForeground(new Color(50, 205, 50));
		energyAllProgressBar.setStringPainted(true);
		energyAllProgressBar.setBounds(255, 86, 146, 14);
		statsPanel.add(energyAllProgressBar);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 161, 391, 29);
		statsPanel.add(scrollPane);
		
		consoleScreen = new JTextPane();
		scrollPane.setViewportView(consoleScreen);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(270, 11, 411, 215);
		frmElectricVehicleAgent.getContentPane().add(tabbedPane);
		
		bySlotPane = new JScrollPane();
		bySlotPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		tabbedPane.addTab("By Slot", null, bySlotPane, null);
		

		bySlotTable = new JTable();
		bySlotPane.setViewportView(bySlotTable);

		
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

		table_model_slot.addColumn("Time Slot");
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
		            		+ car_to_slot.get(position - 1).getEndTime());
		        }
		    }
		});

		//byCarTable.getColumnModel().getColumn(1).setMaxWidth(800);
		//byCarTable.getColumnModel().getColumn(1).setMinWidth(800);
		
		
		byCarPane = new JScrollPane(byCarTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		byCarPane.setVerticalScrollBar(byCarPane.createVerticalScrollBar());
		tabbedPane.addTab("By Car", null, byCarPane, null);
		

		

		
		model = new Model();
		
	}
}

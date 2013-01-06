
package fi.helsinki.cs.turridevelop.gui;

import fi.helsinki.cs.turridevelop.exceptions.SimulationException;
import fi.helsinki.cs.turridevelop.logic.Machine;
import fi.helsinki.cs.turridevelop.logic.Project;
import fi.helsinki.cs.turridevelop.logic.Simulation;
import fi.helsinki.cs.turridevelop.logic.Tape;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.TreeSet;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Panel for simulating Turing machines.
 */
public class RunPanel extends JPanel {
    /**
     * The project being simulated.
     */
    private Project project;
    
    /**
     * Handler to call when the panel should be closed.
     */
    private RunPanelCloseHandler close_handler;
    
    /**
     * Combo box for choosing the machine to run.
     */
    private JComboBox machine_combo;
    
    /**
     * The running simulation, null if none.
     */
    private Simulation simulation;
    
    /**
     * Buttons that should be disabled if there is no simulation running.
     */
    private ArrayList<AbstractButton> simulation_buttons;
    
    /**
     * The tape to use in simulations.
     */
    private Tape tape;
    
    /**
     * The label showing the status of the simulation.
     */
    private JLabel status;
    
    /**
     * Constructs a run panel for a project.
     * 
     * @param project The project to run.
     * @param close_handler Handler to call when the panel should be closed.
     */
    public RunPanel(Project project, RunPanelCloseHandler close_handler) {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(3, 3, 3, 3);
        setBorder(BorderFactory.createTitledBorder("Run project"));
        
        this.project = project;
        this.close_handler = close_handler;
        tape = new Tape();
        simulation_buttons = new ArrayList<AbstractButton>();
        
        JButton button;
        
        c.gridy = 0;
        
        button = new JButton("Close");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeClicked();
            }
        });
        c.gridx = 0;
        add(button, c);
        
        c.gridx++;
        add(new JLabel("Machine to run: "), c);
        
        machine_combo = new JComboBox();
        c.gridx++;
        add(machine_combo, c);
        
        button = new JButton("Start");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startClicked();
            }
        });
        c.gridx++;
        add(button, c);
        
        button = new JButton("Step");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stepClicked();
            }
        });
        simulation_buttons.add(button);
        c.gridx++;
        add(button, c);
        
        status = new JLabel();
        c.gridx++;
        c.weightx = 1.0;
        add(status, c);
        c.weightx = 0.0;
        
        machinesChanged();
        updateButtons();
        updateStatus();
    }
    
    /**
     * Function for notifying the panel that the list of machines has changed.
     */
    public void machinesChanged() {
        Object original_selection = machine_combo.getSelectedItem();
        
        // Update the list.
        TreeSet<String> machine_names =
            new TreeSet<String>(project.getMachineNames());
        Object[] items = new Object[machine_names.size()];
        int i = 0;
        for(String name : machine_names) {
            items[i] = new MachineName(name);
            i++;
        }
        machine_combo.setModel(new DefaultComboBoxModel(items));
        
        // Try to select the same machine as it was before.
        if(original_selection != null) {
            machine_combo.setSelectedItem(original_selection);
        }
    }
    
    private void closeClicked() {
        close_handler.runPanelClosed(this);
    }
    
    private void startClicked() {
        MachineName selection = (MachineName) machine_combo.getSelectedItem();
        Machine machine = null;
        simulation = null;
        if(selection != null) {
            machine = project.getMachine(selection.getName());
        }
        if(machine != null) {
            try {
                simulation = new Simulation(project, machine.getName(), tape);
            } catch(SimulationException e) {
                fail(e);
                return;
            }
        }
        updateButtons();
        updateStatus();
    }
    
    private void stepClicked() {
        if(simulation != null) {
            try {
                simulation.step();
            } catch(SimulationException e) {
                fail(e);
            }
        }
        updateStatus();
    }
    
    /**
     * Enable/disable buttons based on whether the simulation is active.
     */
    private void updateButtons() {
        for(AbstractButton button : simulation_buttons) {
            button.setEnabled(simulation != null);
        }
    }
    
    /**
     * Update the status button to reflect the status of the simulation.
     */
    private void updateStatus() {
        if(simulation == null) {
            status.setText("Status: NOTHING");
        } else {
            StringBuilder text = new StringBuilder();
            switch(simulation.getStatus()) {
                case RUNNING:
                    text.append("Status: RUNNING");
                    break;
                case REJECTED:
                    text.append("Status: REJECTED");
                    break;
                case ACCEPTED:
                    text.append("Status: ACCEPTED");
                    break;
            }
            
            text.append(", Machine: '");
            text.append(simulation.getMachine().getName());
            text.append("', State: '");
            text.append(simulation.getState().getName());
            text.append("'");
                    
            status.setText(text.toString());
        }
        revalidate();
        repaint();
    }
    
    /**
     * Shows error message based on exception and reset simulation.
     * 
     * @param e The simulation exception to use for the error message.
     */
    private void fail(SimulationException e) {
        JOptionPane.showMessageDialog(
            this,
            "Simulation failed:\n" + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
        simulation = null;
        updateButtons();
        updateStatus();
    }
}

package fi.helsinki.cs.turridevelop.gui;

import fi.helsinki.cs.turridevelop.exceptions.SimulationException;
import fi.helsinki.cs.turridevelop.logic.Machine;
import fi.helsinki.cs.turridevelop.logic.Project;
import fi.helsinki.cs.turridevelop.logic.Simulation;
import fi.helsinki.cs.turridevelop.logic.Tape;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

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
     * The text area for the tape view.
     */
    private JTextArea tape_textarea;
    
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
        
        c.gridx = 0;
        c.gridy = 0;
        
        button = new JButton("Close");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeClicked();
            }
        });
        add(button, c);
        c.gridx++;
        
        add(new JLabel("Machine to run: "), c);
        c.gridx++;
        
        machine_combo = new JComboBox();
        add(machine_combo, c);
        c.gridx++;
        
        button = new JButton("Start");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startClicked();
            }
        });
        add(button, c);
        c.gridx++;
        
        button = new JButton("Step");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stepClicked();
            }
        });
        simulation_buttons.add(button);
        add(button, c);
        c.gridx++;
        
        status = new JLabel();
        c.weightx = 1.0;
        add(status, c);
        c.weightx = 0.0;
        c.gridx++;
        
        c.gridy++;
        c.gridx = 0;
        
        tape_textarea = new JTextArea(2, 30);
        tape_textarea.setEditable(false);
        tape_textarea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(
            tape_textarea,
            JScrollPane.VERTICAL_SCROLLBAR_NEVER,
            JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
        ));
        c.gridwidth = 6;
        add(panel, c);
        c.gridwidth = 1;
        
        machinesChanged();
        updateButtons();
        updateStatus();
        updateTapeView();
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
        updateTapeView();
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
        updateTapeView();
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
     * Update the tape view to show the current status of the view.
     */
    private void updateTapeView() {
        StringBuilder text = new StringBuilder();
        int pos = 0;
        if(simulation != null) {
            pos = simulation.getHead().getPosition();
            for(int i = 0; i < pos; i++) {
                text.append(' ');
            }
            text.append('▼');
        }
        text.append("\n");
        String contents = tape.getContents();
        for(int i = 0; i < Math.max(contents.length(), pos) + 300; i++) {
            char c = tape.getCharacterAt(i);
            if(c == '\n') {
                c = '�';
            }
            text.append(c);
        }
        tape_textarea.setText(text.toString());
        tape_textarea.setCaretPosition(pos);
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
        updateTapeView();
    }
}

package fi.helsinki.cs.turridevelop.gui;

import fi.helsinki.cs.turridevelop.exceptions.SimulationException;
import fi.helsinki.cs.turridevelop.logic.Machine;
import fi.helsinki.cs.turridevelop.logic.Project;
import fi.helsinki.cs.turridevelop.logic.Simulation;
import fi.helsinki.cs.turridevelop.logic.SimulationStatus;
import fi.helsinki.cs.turridevelop.logic.Tape;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.TreeSet;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

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
    private RunPanelEventHandler eventhandler;
    
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
     * The toggle button used to continuously run the project.
     */
    private JToggleButton run_button;
    
    /**
     * Has the user been warned for this run that the tape is very long?
     */
    private boolean run_warned;
    
    /**
     * Constructs a run panel for a project.
     * 
     * @param project The project to run.
     * @param eventhandler Handler to call on events.
     */
    public RunPanel(Project project, RunPanelEventHandler eventhandler) {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(3, 3, 3, 3);
        setBorder(BorderFactory.createTitledBorder("Run project"));
        
        this.project = project;
        this.eventhandler = eventhandler;
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
        button.setMnemonic(KeyEvent.VK_A);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startClicked();
            }
        });
        add(button, c);
        c.gridx++;
        
        button = new JButton("Step");
        button.setMnemonic(KeyEvent.VK_S);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stepClicked();
            }
        });
        simulation_buttons.add(button);
        add(button, c);
        c.gridx++;
        
        run_button = new JToggleButton("Run");
        run_button.setMnemonic(KeyEvent.VK_R);
        run_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runToggled();
            }
        });
        simulation_buttons.add(run_button);
        add(run_button, c);
        c.gridx++;
        
        status = new JLabel();
        c.weightx = 1.0;
        add(status, c);
        c.weightx = 0.0;
        c.gridx++;
        
        c.gridy++;
        c.gridx = 0;
        
        
        c.gridwidth = 7;
        add(createTapePanel(), c);
        c.gridwidth = 1;
        
        machinesChanged();
        updateButtons();
        updateStatus();
        updateTapeView();
    }
    
    /**
     * Creates and returns a panel for showing the state of the tape.
     */
    private JPanel createTapePanel() {
        JPanel tapepanel = new JPanel(new GridBagLayout());
        tapepanel.setBorder(BorderFactory.createTitledBorder("Tape"));
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1.0;
        
        JButton button = new JButton("Clear");
        button.setMnemonic(KeyEvent.VK_C);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearTapeClicked();
            }
        });
        c.gridx = 0;
        c.gridy = 0;
        tapepanel.add(button, c);
        
        button = new JButton("Edit");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editTapeClicked();
            }
        });
        c.gridy = 1;
        tapepanel.add(button, c);
        
        tape_textarea = new JTextArea(2, 30);
        tape_textarea.setEditable(false);
        tape_textarea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        
        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 2;
        c.weightx = 1.0;
        tapepanel.add(new JScrollPane(
            tape_textarea,
            JScrollPane.VERTICAL_SCROLLBAR_NEVER,
            JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
        ), c);
        
        return tapepanel;
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
        eventhandler.runPanelClosed(this);
    }
    
    private void startClicked() {
        run_warned = false;
        
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
        eventhandler.runPanelShowState(
            simulation.getMachine().getName(),
            simulation.getState().getName()
        );
    }
    
    private void stepClicked() {
        step(1);
    }
    
    /**
     * Steps the simulation given number of times.
     * 
     * @param times How many times to step.
     */
    private void step(int times) {
        if(simulation != null) {
            if(
                !run_warned &&
                run_button.isSelected() &&
                simulation.getHead().getPosition() > 50000
            ) {
                int ret = JOptionPane.showConfirmDialog(
                    this,
                    "The tape is getting very long, are you sure you want to " +
                    "continue running?",
                    "Tape length warning",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                if(ret == JOptionPane.YES_OPTION) {
                    run_warned = true;
                } else {
                    run_button.setSelected(false);
                    return;
                }
            }
            try {
                for(int i = 0; i < times; i++) {
                    simulation.step();
                }
            } catch(SimulationException e) {
                fail(e);
            }
        }
        updateStatus();
        updateTapeView();
        if(simulation != null) {
            eventhandler.runPanelShowState(
                simulation.getMachine().getName(),
                simulation.getState().getName()
            );
        }
    }
    
    private void runToggled() {
        run_warned = false;
        if(run_button.isSelected()) {
            new Runnable() {
                @Override
                public void run() {
                    if(
                        run_button.isSelected() &&
                        simulation != null &&
                        simulation.getStatus() == SimulationStatus.RUNNING &&
                        isDisplayable()
                    ) {
                        step(10000); 
                        SwingUtilities.invokeLater(this);
                    } else {
                        run_button.setSelected(false);
                    }
                }
            }.run();
        }
    }
    
    private void clearTapeClicked() {
        tape.setContents("");
        updateTapeView();
    }
    
    private void editTapeClicked() {
        String contents = JOptionPane.showInputDialog(
            this,
            "New tape content:",
            tape.getContents() + tape.getEmptyCharacter()
        );
        if(contents != null) {
            tape.setContents(contents);
            updateTapeView();
        }
    }
    
    /**
     * Enables/disables buttons based on whether the simulation is active.
     */
    private void updateButtons() {
        for(AbstractButton button : simulation_buttons) {
            button.setEnabled(simulation != null);
        }
    }
    
    /**
     * Updates the status button to reflect the status of the simulation.
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
     * Updates the tape view to show the current status of the view.
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
        
        // Move the caret so that the position where the head is is visible.
        tape_textarea.setCaretPosition(
            Math.min(Math.max(2 * pos + 2 - 5, pos + 2), text.length() - 1)
        );
        final int pos2 = pos;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                tape_textarea.setCaretPosition(
                    Math.min(2 * pos2 + 7, tape_textarea.getText().length())
                );
            }
        });
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

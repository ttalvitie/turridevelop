
package fi.helsinki.cs.turridevelop.gui;

import fi.helsinki.cs.turridevelop.exceptions.NameInUseException;
import fi.helsinki.cs.turridevelop.logic.State;
import fi.helsinki.cs.turridevelop.logic.Transition;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Editing panel for a state.
 */
public class StateEditor extends JPanel {
    /**
     * The state being edited.
     */
    private State state;
    
    /**
     * The frame containing this editor.
     */
    private JFrame frame;
    
    /**
     * The machine view currently editing the machine in which the state is
     * contained.
     */
    private MachineView machineview;
    
    /**
     * The text field for changing the name.
     */
    private JTextField namefield;
    
    /**
     * Constructs a state editor.
     * 
     * @param state State to edit.
     * @param machineview The associated machine view.
     * @param frame The frame containing the editor.
     */
    public StateEditor(State state, MachineView machineview, JFrame frame) {
        this.state = state;
        this.machineview = machineview;
        this.frame = frame;
        
        setBorder(BorderFactory.createTitledBorder("Edit state"));
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        
        JButton button = new JButton("Remove state");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeStateClicked();
            }
        });
        c.gridx = 0;
        c.gridy = 0;
        add(button, c);
        
        button = new JButton("New transition");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newTransitionClicked();
            }
        });
        c.gridx = 1;
        c.gridy = 0;
        add(button, c);
        
        // Name panel:
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(new JLabel("Name: "));
        
        namefield = new JTextField(state.getName());
        namefield.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nameChanged();
            }
        });
        namefield.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                nameChanged();
            }
        });
        panel.add(namefield);
        
        panel.setMaximumSize(new Dimension(
            panel.getMaximumSize().width, panel.getPreferredSize().height
        ));
        
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        add(panel, c);
        
        JList transitionlist = new JList();
        JScrollPane transitionscroll = new JScrollPane(transitionlist);
        c.gridx = 0;
        c.gridy = 2;
        c.weighty = 1.0;
        add(transitionscroll, c);
    }
    
    private void nameChanged() {
        if(namefield.getText().equals(state.getName())) {
            return;
        }
        try {
            state.setName(namefield.getText());
        } catch(NameInUseException e) {
            namefield.setText(state.getName());
            JOptionPane.showMessageDialog(
                this,
                "Could not change name to '" + namefield.getText() + "':\n" +
                "Name already in use.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
        machineview.stateModified();
    }
    
    private void removeStateClicked() {
        machineview.getMachine().removeState(state.getName());
        machineview.stateModified();
    }
    
    private void newTransitionClicked() {
        machineview.startStateChoice(new StateChoiceHandler() {
            @Override
            public void stateChosen(State choice) {
                if(choice == null) {
                    return;
                }
                
                addTransitionTo(choice);
            }            
        }, "Choose the destination state for the transition.");
    }
    
    private void addTransitionTo(State destination) {
        TransitionEditor dialog = new TransitionEditor(frame, "New transition");
        dialog.setVisible(true);
        Transition transition = dialog.getTransition(destination);
        if(transition != null) {
            try {
                state.addTransition(transition);
            } catch(NameInUseException e) {
                JOptionPane.showMessageDialog(
                    this,
                    "Could not add transition:\nInput character already in use",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
        }
        machineview.stateModified();
    }
}


package fi.helsinki.cs.turridevelop.gui;

import fi.helsinki.cs.turridevelop.exceptions.NameInUseException;
import fi.helsinki.cs.turridevelop.logic.State;
import fi.helsinki.cs.turridevelop.logic.Transition;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
     * The check box for setting the state accepting.
     */
    private JCheckBox accepting;
    
    /**
     * List of transitions.
     */
    JList transitionlist;
    
    /**
     * The list of the transitions referred by transitionlist in the same order.
     */
    ArrayList<Transition> transitionlist_objs;
    
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
        
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        add(panel, c);
        c.gridwidth = 1;
        
        accepting = new JCheckBox("Accepting", state.isAccepting());
        accepting.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                acceptingToggled();
            }
        });
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        add(accepting, c);
        c.gridwidth = 1;

        JButton button = new JButton("Remove");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeStateClicked();
            }
        });
        c.gridx = 0;
        c.gridy = 2;
        add(button, c);
        
        button = new JButton("Merge");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mergeStateClicked();
            }
        });
        c.gridx = 1;
        c.gridy = 2;
        add(button, c);
                
        JPanel transitionpanel = new JPanel(new GridBagLayout());
        GridBagConstraints c2;
        
        transitionlist = new JList();
        JScrollPane transitionscroll = new JScrollPane(transitionlist);
        transitionscroll.setBorder(
            BorderFactory.createTitledBorder("Transitions")
        );
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 1.0;
        c.gridwidth = 2;
        transitionpanel.add(transitionscroll, c);
        c.weighty = 0.0;
        c.gridwidth = 1;
        
        button = new JButton("New");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newTransitionClicked();
            }
        });
        c.gridx = 0;
        c.gridy = 1;
        transitionpanel.add(button, c);

        button = new JButton("Remove");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeTransitionClicked();
            }
        });
        c.gridx = 1;
        c.gridy = 1;
        transitionpanel.add(button, c);
        
        button = new JButton("Modify");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modifyTransitionClicked();
            }
        });
        c.gridx = 0;
        c.gridy = 2;
        transitionpanel.add(button, c);
        
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        c.weighty = 1.0;
        add(transitionpanel, c);
        
        updateTransitionList();
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
                frame,
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
    
    private void mergeStateClicked() {
        
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
    
    private void removeTransitionClicked() {
        int selected = transitionlist.getSelectedIndex();
        if(selected >= 0 && selected < transitionlist_objs.size()) {
            state.removeTransition(transitionlist_objs.get(selected));
            updateTransitionList();
            machineview.stateModified();
        }
    }
    
    private void modifyTransitionClicked() {
        int selected = transitionlist.getSelectedIndex();
        if(selected >= 0 && selected < transitionlist_objs.size()) {
            Transition oldtransition = transitionlist_objs.get(selected);
            State destination = oldtransition.getDestination();
            
            TransitionEditor dialog =
                new TransitionEditor(frame, "Modify transition");
            dialog.defaultsFromTransition(oldtransition);
            dialog.setVisible(true);
            
            Transition newtransition = dialog.getTransition(destination);
            
            if(newtransition != null) {
                // First remove old transition and add new. If adding fails,
                // add the old transition back.
                boolean success = true;
                state.removeTransition(oldtransition);
                try {
                    state.addTransition(newtransition);
                } catch(NameInUseException e) {
                    success = false;
                }
                if(!success) {
                    try {
                        state.addTransition(oldtransition);
                    } catch(NameInUseException e) {
                        throw new RuntimeException();
                    }
                    JOptionPane.showMessageDialog(
                        frame,
                        "Could not modify transition:\n" +
                        "Input character already in use.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
                updateTransitionList();
                machineview.stateModified();
            }
        }
    }
    
    private void acceptingToggled() {
        state.setAccepting(accepting.isSelected());
        machineview.stateModified();
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
                    frame,
                    "Could not add transition:\n" +
                    "Input character already in use.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
        }
        updateTransitionList();
        machineview.stateModified();
    }
    
    /**
     * Update transition list to match the current transitions of the state.
     */
    private void updateTransitionList() {
        transitionlist_objs = new ArrayList<Transition>(state.getTransitions());
        
        // Sort the transitions by their text representations.
        Collections.sort(transitionlist_objs, new Comparator<Transition>() {
            @Override
            public int compare(Transition t1, Transition t2) {
                return Util.getTransitionText(t1, true).compareTo(
                    Util.getTransitionText(t2, true)
                );
            }
        });
        
        // Initialize the list element.
        String[] names = new String[transitionlist_objs.size()];
        for(int i = 0; i < transitionlist_objs.size(); i++) {
            names[i] = Util.getTransitionText(transitionlist_objs.get(i), true);
        }
        transitionlist.setListData(names);
    }
}

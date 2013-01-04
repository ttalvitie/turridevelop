
package fi.helsinki.cs.turridevelop.gui;

import fi.helsinki.cs.turridevelop.exceptions.NameInUseException;
import fi.helsinki.cs.turridevelop.logic.State;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.BoxLayout;
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
     */
    public StateEditor(State state, MachineView machineview) {
        this.state = state;
        this.machineview = machineview;
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
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
        add(panel);
        
        JList transitionlist = new JList();
        JScrollPane transitionscroll = new JScrollPane(transitionlist);
        add(transitionscroll);
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
}

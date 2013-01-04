
package fi.helsinki.cs.turridevelop.gui;

import fi.helsinki.cs.turridevelop.logic.State;
import fi.helsinki.cs.turridevelop.logic.Transition;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 * Dialog for creating/editing transitions.
 */
public class TransitionEditor extends JDialog {
    /**
     * Text field for input characters.
     */
    private JTextField input_field;
    
    /**
     * Text field for output character.
     */
    private JTextField output_field;
    
    /**
     * Radio button for 'left' movement.
     */
    private JRadioButton radio_left;
    
    /**
     * Radio button for 'stay' movement.
     */
    private JRadioButton radio_stay;
    
    /**
     * Radio button for 'right' movement.
     */
    private JRadioButton radio_right;
    
    /**
     * The button group for movement.
     */
    private ButtonGroup movement_group;
    
    /**
     * Was the editor closed with the OK button?
     */
    private boolean closed_ok;
    
    /**
     * Constructs an empty transition editor. Use setVisible(true) to show and
     * wait for closing of the dialog.
     * 
     * @param frame The parent frame that should be blocked while editing.
     * @param title The title of the dialog.
     */
    public TransitionEditor(JFrame frame, String title) {
        super(frame, title, true);
        getContentPane().setLayout(new GridBagLayout());
        
        closed_ok = false;
        
        final char emptychar = '␣';
        
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(4, 4, 4, 4);
        
        c.gridx = 0;
        c.gridy = 0;
        getContentPane().add(new JLabel("Input characters: "), c);
        
        input_field = new JTextField(10);
        c.gridx = 1;
        c.gridy = 0;
        getContentPane().add(input_field, c);
        
        JButton openbox_button = new JButton("" + emptychar);
        openbox_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                input_field.setText(input_field.getText() + emptychar);
            }
        });
        c.gridx = 2;
        c.gridy = 0;
        c.weightx = 0.0;
        getContentPane().add(openbox_button, c);
        c.weightx = 1.0;
        
        c.gridx = 0;
        c.gridy = 1;
        getContentPane().add(new JLabel("Output character: "), c);
        
        output_field = new JTextField();
        c.gridx = 1;
        c.gridy = 1;
        getContentPane().add(output_field, c);
        
        openbox_button = new JButton("" + emptychar);
        openbox_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                output_field.setText("" + emptychar);
            }
        });
        c.gridx = 2;
        c.gridy = 1;
        c.weightx = 0.0;
        getContentPane().add(openbox_button, c);
        c.weightx = 1.0;
        
        c.gridx = 0;
        c.gridy = 2;
        getContentPane().add(new JLabel("Movement: "), c);
        
        JPanel radiobuttonpanel = new JPanel(new FlowLayout());
        radio_left = new JRadioButton("Left");
        radio_stay = new JRadioButton("Stay");
        radio_right = new JRadioButton("Right");
        radiobuttonpanel.add(radio_left);
        radiobuttonpanel.add(radio_stay);
        radiobuttonpanel.add(radio_right);
        c.gridx = 1;
        c.gridy = 2;
        c.gridwidth = 2;
        getContentPane().add(radiobuttonpanel, c);
        
        movement_group = new ButtonGroup();
        movement_group.add(radio_left);
        movement_group.add(radio_stay);
        movement_group.add(radio_right);
        movement_group.setSelected(radio_stay.getModel(), true);
        
        JPanel buttonpanel = new JPanel(new FlowLayout());
        JButton cancel = new JButton("Cancel");
        JButton ok = new JButton("OK");
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                okClicked();
            }
        });
        
        buttonpanel.add(cancel);
        buttonpanel.add(ok);
        c.gridx = 0;
        c.gridwidth = 3;
        c.gridy = 3;
        getContentPane().add(buttonpanel, c);
        
        pack();
    }
    
    private void okClicked() {
        String error = null;
        if(input_field.getText().length() == 0) {
            error = "The transition does not have any input characters.";
        }
        if(output_field.getText().length() > 1) {
            error = "Output should be empty or one character.";
        }
        
        if(error != null) {
            JOptionPane.showMessageDialog(
                this,
                "Could not save transition:\n" + error,
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        
        closed_ok = true;
        dispose();
    }
    
    /**
     * Get the default values of the fields from a transition.
     * 
     * @param transition The transition from which to get the default values.
     */
    public void defaultsFromTransition(Transition transition) {
        input_field.setText(transition.getInputCharacters());
        if(transition.getOutputCharacter() == null) {
            output_field.setText("");
        } else {
            output_field.setText("" + transition.getOutputCharacter());
        }
        switch(transition.getMovement()) {
            case -1:
                movement_group.setSelected(radio_left.getModel(), true);
                break;
            case 0:
                movement_group.setSelected(radio_stay.getModel(), true);
                break;
            case 1:
                movement_group.setSelected(radio_right.getModel(), true);
                break;
        }
    }
    
    /**
     * Construct a transition from the settings specified in the editor.
     * 
     * @param destination The destination state.
     * @return The transition or null if user didn't close the window with OK
     * button.
     */
    public Transition getTransition(State destination) {
        if(!closed_ok) {
            return null;
        }
        
        Character output = null;
        if(output_field.getText().length() != 0) {
            output = output_field.getText().charAt(0);
        }
        
        int movement = -1;
        if(radio_stay.isSelected()) {
            movement = 0;
        }
        if(radio_right.isSelected()) {
            movement = 1;
        }
        
        return new Transition(
            destination,
            input_field.getText(),
            output,
            movement
        );
    }
}

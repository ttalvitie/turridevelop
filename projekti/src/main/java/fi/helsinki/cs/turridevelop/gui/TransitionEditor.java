
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
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
    private JRadioButton left;
    
    /**
     * Radio button for 'stay' movement.
     */
    private JRadioButton stay;
    
    /**
     * Radio button for 'right' movement.
     */
    private JRadioButton right;
    
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
        
        c.gridx = 0;
        c.gridy = 1;
        getContentPane().add(new JLabel("Output character: "), c);
        
        output_field = new JTextField();
        c.gridx = 1;
        c.gridy = 1;
        getContentPane().add(output_field, c);
        
        c.gridx = 0;
        c.gridy = 2;
        getContentPane().add(new JLabel("Movement: "), c);
        
        JPanel radiobuttonpanel = new JPanel(new FlowLayout());
        left = new JRadioButton("Left");
        stay = new JRadioButton("Stay");
        right = new JRadioButton("Right");
        radiobuttonpanel.add(left);
        radiobuttonpanel.add(stay, true);
        radiobuttonpanel.add(right);
        c.gridx = 1;
        c.gridy = 2;
        getContentPane().add(radiobuttonpanel, c);
        
        ButtonGroup group = new ButtonGroup();
        group.add(left);
        group.add(stay);
        group.add(right);
        
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
                closed_ok = true;
                dispose();
            }
        });
        
        buttonpanel.add(cancel);
        buttonpanel.add(ok);
        c.gridx = 0;
        c.gridwidth = 2;
        c.gridy = 3;
        getContentPane().add(buttonpanel, c);
        
        pack();
    }
    
    /**
     * Construct a transition from the settings specified in the editor.
     * 
     * @param destination The destination state.
     * @return The transition or null if user didn't close the window with OK
     * button.
     */
    Transition getTransition(State destination) {
        if(!closed_ok) {
            return null;
        }
        
        Character output = null;
        if(output_field.getText().length() != 0) {
            output = output_field.getText().charAt(0);
        }
        
        int movement = -1;
        if(stay.isSelected()) {
            movement = 0;
        }
        if(right.isSelected()) {
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

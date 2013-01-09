package fi.helsinki.cs.turridevelop.gui;

import fi.helsinki.cs.turridevelop.exceptions.FilesystemException;
import fi.helsinki.cs.turridevelop.exceptions.MalformedFileException;
import fi.helsinki.cs.turridevelop.exceptions.NameInUseException;
import fi.helsinki.cs.turridevelop.file.TurrInput;
import fi.helsinki.cs.turridevelop.file.TurrOutput;
import fi.helsinki.cs.turridevelop.logic.Machine;
import fi.helsinki.cs.turridevelop.logic.Project;
import fi.helsinki.cs.turridevelop.logic.State;
import fi.helsinki.cs.turridevelop.util.Vec2;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Window where a single project is modified.
 */
public class ProjectWindow implements RunPanelEventHandler {
    /**
     * The frame of the window.
     */
    private JFrame frame;
    
    /**
     * Project being manipulated, null if none. Change through changeProject.
     */
    private Project project;
    
    /**
     * Buttons that should only be enabled when a project is open.
     */
    private ArrayList<AbstractButton> project_buttons;
    
    /**
     * Buttons that should only be enabled when a machine is being edited.
     */
    private ArrayList<AbstractButton> machine_buttons;
    
    /**
     * The list of machines. Valid if project != null.
     */
    private JList machinelist;
    
    /**
     * The machine editing area. Valid if project != null.
     */
    private JPanel machinepanel;
    
    /**
     * The currently visible machineview, null if none.
     */
    private MachineView machineview;
    
    /**
     * The editing panel area. Valid if project != null.
     */
    private JPanel editpanel;
    
    /**
     * The run panel area. Valid if project != null.
     */
    private JPanel runpanel;
    
    /**
     * The main splitter of the window.
     */
    private JSplitPane split;
    
    public ProjectWindow() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(300, 200));
        
        // Add menus.
        JMenuBar menubar = new JMenuBar();
        JMenu menu;
        JMenuItem item;
        project_buttons = new ArrayList<AbstractButton>();
        machine_buttons = new ArrayList<AbstractButton>();
        
        // Project-menu:
        menu = new JMenu("Project");
        
        item = new JMenuItem("New project");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newProjectClicked();
            }
        });
        menu.add(item);
        
        item = new JMenuItem("Open project");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openProjectClicked();
            }
        });
        menu.add(item);
        
        item = new JMenuItem("Close project");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeProjectClicked();
            }
        });
        project_buttons.add(item);
        menu.add(item);
        
        item = new JMenuItem("Save project as");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveProjectAsClicked();
            }
        });
        project_buttons.add(item);
        menu.add(item);
        menu.addSeparator();
        
        item = new JMenuItem("Run");
        item.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK)
        );
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runProjectClicked();
            }
        });
        project_buttons.add(item);
        menu.add(item);
        menu.addSeparator();
        
        item = new JMenuItem("Quit");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                quitClicked();
            }
        });
        menu.add(item);
        
        menubar.add(menu);
        
        frame.setJMenuBar(menubar);
        
        changeProject(null);
        
        frame.getContentPane().setLayout(new BoxLayout(
            frame.getContentPane(), BoxLayout.X_AXIS
        ));
        
        frame.pack();
        frame.setVisible(true);
    }
    
    @Override
    public void runPanelShowState(String machine, String state) {
        if(project != null) {
            updateMachineList(machine);
            if(machineview != null) {
                machineview.setActiveState(state);
            }
        }
    }
    
    @Override
    public void runPanelClosed(RunPanel panel) {
        runpanel.removeAll();
        split.revalidate();
        split.repaint();
    }
    
    private void newProjectClicked() {
        changeProject(new Project());
    }
    
    private void openProjectClicked() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if(chooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        
        // A directory was chosen, try to open it.
        File dir = chooser.getSelectedFile();
        try {
            changeProject(TurrInput.readProjectDirectory(dir));
        } catch(MalformedFileException e) {
            JOptionPane.showMessageDialog(
                frame,
                "Opening project '" + dir + "' failed:\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    private void closeProjectClicked() {
        changeProject(null);
   }
    
    private void saveProjectAsClicked() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if(chooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        
        // A directory was chosen, create it if it doesn't exist already.
        File dir = chooser.getSelectedFile();
        if(!dir.isDirectory()) {
            JOptionPane.showMessageDialog(
                frame,
                "Saving project to '" + dir + "' failed:\n" +
                "The path is not a directory.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        
        // Try to save.
        try {
            TurrOutput.writeProject(project, dir);
        } catch(FilesystemException e) {
            JOptionPane.showMessageDialog(
                frame,
                "Saving project to '" + dir + "' failed:\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    private void quitClicked() {
        System.exit(0);
    }
    
    private void newMachineClicked() {
        String name = JOptionPane.showInputDialog(frame, "Machine name:");
        if(name == null) {
            return;
        }
        
        try {
            Machine machine = project.addMachine(name);
            
            // Update machine list and set the new machine as current.
            updateMachineList(name);
            
            // Add start and accept states.
            try {
                State start = machine.addState("start");
                start.setPosition(new Vec2(-100, 0));
                State accept = machine.addState("accept");
                accept.setPosition(new Vec2(100, 0));
                accept.setAccepting(true);
            } catch(NameInUseException e) {
                throw new RuntimeException();
            }
        } catch(NameInUseException e) {
            JOptionPane.showMessageDialog(
                frame,
                "Could not create machine with name '" + name + "':\n" +
                "Name already in use.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    private void renameMachineClicked() {
        Machine machine = machineview.getMachine();
        String name = JOptionPane.showInputDialog(
            frame,
            "New name for machine '" + machine.getName() + "':",
            machine.getName()
        );
        if(name == null) {
            return;
        }
        
        if(name.equals(machine.getName())) {
            // Nothing needs to be done.
            return;
        }
        try {
            machine.setName(name);
        } catch(NameInUseException e) {
            JOptionPane.showMessageDialog(
                frame,
                "Could rename machine to '" + name + "':\n" +
                "Name already in use.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        
        // Renaming the machine succeeded, update machine list and set the same
        // machine as current.
        updateMachineList(name);
    }
    
    private void removeMachineClicked() {
        String name = machineview.getMachine().getName();
        int ret = JOptionPane.showConfirmDialog(
            frame,
            "Are you sure you want to remove machine '" + name + "'?",
            "Confirm machine removal",
            JOptionPane.YES_NO_OPTION
        );
        
        if(ret == JOptionPane.YES_OPTION) {
            project.removeMachine(name);
            updateMachineList(null);
        }
    }
    
    private void runProjectClicked() {
        runpanel.removeAll();
        runpanel.add(new RunPanel(project, this));
        split.revalidate();
        split.repaint();
    }
    
    void newStateClicked() {
        machineview.addState();
    }
    
    private void machineSelected() {
        MachineName machinename = (MachineName) machinelist.getSelectedValue();
        Machine machine;
        if(machinename == null) {
            machine = null;
        } else {
            machine = project.getMachine(machinename.getName());
        }
        machinepanel.removeAll();
        editpanel.removeAll();
        machineview = null;
        if(machine != null) {
            machineview = new MachineView(project, machine, editpanel, frame);
            machinepanel.add(machineview);
        }
        
        for(AbstractButton item : machine_buttons) {
            item.setEnabled(machine != null);
        }
        
        machinepanel.revalidate();
        machinepanel.repaint();
        
        editpanel.revalidate();
        editpanel.repaint();
    }
    
    /**
     * Set the project of the class to project, update GUI accordingly.
     * 
     * @param newproject The new project.
     */
    private void changeProject(Project newproject) {
        project = newproject;
        
        // Update the buttons.
        for(AbstractButton item : project_buttons) {
            item.setEnabled(project != null);
        }
        
        if(project == null) {
            for(AbstractButton item : machine_buttons) {
                item.setEnabled(false);
            }
        }
        
        // Update the frame layout.
        frame.getContentPane().removeAll();
        if(project != null) {
            // Machine list on the left, the machine panel on the right.
            JPanel machineeditor = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.BOTH;
            c.weightx = 1.0;
            c.gridx = 0;
            c.gridy = 0;
            
            machinelist = new JList();
            machinelist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            machinelist.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    machineSelected();
                }
            });
            
            JScrollPane machinelist_scroll = new JScrollPane(machinelist);
            machinelist_scroll.setMinimumSize(new Dimension(100, 100));
            machinelist_scroll.setPreferredSize(new Dimension(200, 200));
            Border border = BorderFactory.createTitledBorder("Machines");
            machinelist_scroll.setBorder(border);
            c.weighty = 1.0;
            c.gridwidth = 2;
            machineeditor.add(machinelist_scroll, c);
            c.weighty = 0.0;
            c.gridwidth = 1;
            c.gridy++;
            
            JButton button = new JButton("New");
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    newMachineClicked();
                }
            });
            machineeditor.add(button, c);
            c.gridx++;
            
            button = new JButton("Remove");
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    removeMachineClicked();
                }
            });
            button.setEnabled(false);
            machine_buttons.add(button);
            machineeditor.add(button, c);
            
            c.gridy++;
            c.gridx = 0;
            
            button = new JButton("Rename");
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    renameMachineClicked();
                }
            });
            button.setEnabled(false);
            machine_buttons.add(button);
            machineeditor.add(button, c);
            c.gridx++;
            
            button = new JButton("New state");
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    newStateClicked();
                }
            });
            button.setMnemonic(KeyEvent.VK_N);
            button.setEnabled(false);
            machine_buttons.add(button);
            machineeditor.add(button, c);
            
            editpanel = new JPanel();
            editpanel.setLayout(new BorderLayout());
            editpanel.setMinimumSize(new Dimension(200, 200));
            editpanel.setPreferredSize(new Dimension(300, 300));
            
            JSplitPane leftsplit = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT, machineeditor, editpanel
            );
            
            JPanel rightpanel = new JPanel();
            rightpanel.setLayout(new BorderLayout());
            
            machinepanel = new JPanel();
            machinepanel.setLayout(new BorderLayout());
            machinepanel.setMinimumSize(new Dimension(300, 300));
            machinepanel.setPreferredSize(new Dimension(600, 600));
            rightpanel.add(machinepanel);
            
            runpanel = new JPanel();
            runpanel.setLayout(new BorderLayout());
            rightpanel.add(runpanel, BorderLayout.SOUTH);
            
            split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, leftsplit, rightpanel
            );
            frame.getContentPane().add(split);
            updateMachineList(null);
        }
        frame.pack();
    }
    
    /**
     * Updates machine list to match the list of machines in the current
     * project.
     * 
     * @param selection After the update, the name of the machine that should
     * be selected. If null, machines will be unselected.
     */
    private void updateMachineList(String selection) {
        Set<String> machinenames = project.getMachineNames();
        SortedSet<String> machinenames_sort = new TreeSet<String>(machinenames);
        Object[] data = new Object[machinenames_sort.size()];
        int index = 0;
        for(String name : machinenames_sort) {
            data[index] = new MachineName(name);
            index++;
        }
        machinelist.setListData(data);
        
        if(selection == null) {
            machinelist.clearSelection();
        } else {
            machinelist.setSelectedValue(new MachineName(selection), true);
        }
        
        // If there is a run panel, notify it.
        if(runpanel.getComponentCount() == 1) {
            ((RunPanel) runpanel.getComponent(0)).machinesChanged();
        }
    }
}

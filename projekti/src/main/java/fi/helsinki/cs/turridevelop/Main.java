package fi.helsinki.cs.turridevelop;

import fi.helsinki.cs.turridevelop.gui.ProjectWindow;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ProjectWindow window = new ProjectWindow();
            }
        });
    }
}

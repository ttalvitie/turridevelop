package fi.helsinki.cs.turridevelop;

import fi.helsinki.cs.turridevelop.exceptions.NameInUseException;
import fi.helsinki.cs.turridevelop.file.TurrOutput;
import fi.helsinki.cs.turridevelop.logic.Machine;
import fi.helsinki.cs.turridevelop.logic.Project;
import fi.helsinki.cs.turridevelop.logic.State;
import fi.helsinki.cs.turridevelop.logic.Transition;

public class Main {
    public static void main(String[] args) throws NameInUseException {
        // JSON test:
        Project p = new Project();
        Machine m = p.addMachine("m");
        
        State a = m.addState("a");
        State b = m.addState("b");
        b.setAccepting(true);
        a.addTransition(new Transition(b, "xyz", 'w', 5));
        
        System.out.println(TurrOutput.machineToJSON(m));
    }
}

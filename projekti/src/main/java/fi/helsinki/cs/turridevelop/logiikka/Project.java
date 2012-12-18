package fi.helsinki.cs.turridevelop.logiikka;

import java.util.HashMap;

/**
 * Turing machine projects consisting of multiple Machines.
 */
public class Project implements MachineNameStorage {
    private HashMap<String, Machine> machines;
    
    public Project() {
        machines = new HashMap<String, Machine>();
    }
    
    public void onMachineNameChange(String oldname) {
        Machine machine = machines.get(oldname);
        machines.remove(oldname);
        machines.put(machine.getName(), machine);
    }
}

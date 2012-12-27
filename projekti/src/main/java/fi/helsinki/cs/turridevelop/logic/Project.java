package fi.helsinki.cs.turridevelop.logic;

import fi.helsinki.cs.turridevelop.exceptions.NameInUseException;
import java.util.HashMap;

/**
 * Turing machine projects consisting of multiple Machines.
 */
public class Project implements MachineObserver {
    private HashMap<String, Machine> machines;
    
    /**
     * Constructs empty Project.
     */
    public Project() {
        machines = new HashMap<String, Machine>();
    }
    
    /**
     * Gets machine by name.
     * 
     * @param name Name of the machine.
     * @return The machine or null if not found.
     */
    public Machine getMachine(String name) {
        return machines.get(name);
    }
    
    /**
     * Adds a machine to the project.
     * 
     * @param name Name of the new machine.
     * @throws NameInUseException if the name is already in use.
     * @return The created machine.
     */
    public Machine addMachine(String name) throws NameInUseException {
        if(machines.containsKey(name)) {
            throw new NameInUseException();
        }
        Machine machine = new Machine(name, this);
        machines.put(name, machine);
        return machine;
    }
    
    @Override
    public boolean onMachineNameChange(String oldname) {
        Machine machine = machines.get(oldname);
        if(machines.containsKey(machine.getName())) {
            return false;
        }
        machines.remove(oldname);
        machines.put(machine.getName(), machine);
        return true;
    }
}

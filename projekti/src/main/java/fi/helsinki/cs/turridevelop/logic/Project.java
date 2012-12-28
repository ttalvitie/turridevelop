package fi.helsinki.cs.turridevelop.logic;

import fi.helsinki.cs.turridevelop.exceptions.NameInUseException;
import fi.helsinki.cs.turridevelop.util.ByNameContainer;
import java.util.Set;

/**
 * Turing machine projects consisting of multiple Machines.
 */
public class Project {
    /**
     * The machines in the project.
     */
    private ByNameContainer<Machine> machines;
    
    /**
     * Constructs empty Project.
     */
    public Project() {
        machines = new ByNameContainer<Machine>();
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
     * Gets the set of machine names.
     * 
     * @return The set of machine names.
     */
    public Set<String> getMachineNames() {
        return machines.getNames();
    }
    
    /**
     * Adds a machine to the project.
     * 
     * @param name Name of the new machine.
     * @throws NameInUseException if the name is already in use.
     * @return The created machine.
     */
    public Machine addMachine(String name) throws NameInUseException {
        Machine machine = new Machine(name, machines);
        machines.add(machine);
        return machine;
    }
    
    /**
     * Removes a machine from the project, if exists.
     * 
     * @param name Name of the machine.
     */
    public void removeMachine(String name) {
        machines.remove(name);
    }
}

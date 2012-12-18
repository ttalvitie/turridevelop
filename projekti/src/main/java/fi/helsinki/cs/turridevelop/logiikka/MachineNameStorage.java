package fi.helsinki.cs.turridevelop.logiikka;

/**
 * Observer for tracking changes of Machine names.
 */
public interface MachineNameStorage {
    /**
     * Function called after the name of a machine changes.
     * 
     * @param oldname The old name of the machine.
     * @return If false, the new name is already in use and the old name should
     * be restored.
     */
    boolean onMachineNameChange(String oldname);
}

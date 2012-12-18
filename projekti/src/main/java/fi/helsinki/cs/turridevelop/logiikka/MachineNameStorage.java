package fi.helsinki.cs.turridevelop.logiikka;

/**
 * Observer for tracking changes of Machine names.
 */
public interface MachineNameStorage {
    /**
     * Function called after the name of a machine changes.
     * 
     * @param oldname The old name of the machine.
     */
    void onMachineNameChange(String oldname);
}

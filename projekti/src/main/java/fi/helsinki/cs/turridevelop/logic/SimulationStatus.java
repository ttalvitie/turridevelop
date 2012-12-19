package fi.helsinki.cs.turridevelop.logic;

/**
 * Status of a simulation.
 *   RUNNING: the simulation is still running.
 *   ACCEPTED: the simulation reached an accepting state.
 *   REJECTED: the simulation could not continue and thus rejected the input.
 */
public enum SimulationStatus {
    RUNNING,
    ACCEPTED,
    REJECTED
}

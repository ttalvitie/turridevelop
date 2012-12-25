package fi.helsinki.cs.turridevelop.logic;

/**
 * Simulation context for Turing machines.
 */
public class Simulation {
    /**
     * The head of the Turing machine.
     */
    private Head head;
    
    /**
     * The state in which the simulation is now.
     */
    private State state;
    
    /**
     * The status of the simulation.
     */
    private SimulationStatus status; 
    
    /**
     * Constructs a simulation.
     * 
     * @param tape The input tape to simulate on.
     * @param start_state The state to start from.
     */
    public Simulation(Tape tape, State start_state) {
        head = new Head(tape);
        state = start_state;
        if(state.isAccepting()) {
            status = SimulationStatus.ACCEPTED;
        } else {
            status = SimulationStatus.RUNNING;
        }
    }
    
    /**
     * Gets the current status of the simulation.
     * 
     * @return The status of the simulation. If ACCEPTED, the simulation has met
     * an accepting state. If REJECTED, the simulation has met a state from
     * which it could not continue from. Otherwise, it is still RUNNING.
     */
    public SimulationStatus getStatus() {
        return status;
    }
    
    /**
     * Get the state the simulation is currently/was last.
     * 
     * @return If the simulation status is REJECTED, the state from which the
     * simulation could not continue. If the simulation status is ACCEPTED, the
     * accepting state of the simulation. Otherwise the current state.
     */
    public State getState() {
        return state;
    }
    
    /**
     * If the simulation is still running, run it one transition forward.
     */
    public void step() {
        if(status == SimulationStatus.RUNNING) {
            char readc = head.read();
            Transition transition = state.getTransitionByInput(readc);
            if(transition == null) {
                // Cannot continue, reject.
                status = SimulationStatus.REJECTED;
                return;
            }
            
            Character writec = transition.getOutputCharacter();
            if(writec != null) {
                head.write(writec);
            }
            
            head.move(transition.getMovement());
            
            state = transition.getDestination();
            if(state.isAccepting()) {
                status = SimulationStatus.ACCEPTED;
            }
        }
    }
    
    /**
     * Step the simulation until the status is no longer RUNNING.
     * 
     * Note that if the simulation might not terminate.
     */
    public void run() {
        while(getStatus() == SimulationStatus.RUNNING) {
            step();
        }
    }
}

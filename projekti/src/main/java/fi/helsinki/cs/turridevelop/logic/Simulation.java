package fi.helsinki.cs.turridevelop.logic;

import fi.helsinki.cs.turridevelop.exceptions.SimulationException;
import java.util.Stack;

/**
 * Simulation context for Turing machines.
 */
public class Simulation {
    /**
     * The head of the Turing machine.
     */
    private Head head;
    
    /**
     * The stack of states in which the simulation is now. When a submachine is
     * entered, the state of the submachine is put on the top.
     */
    private Stack<State> states;
    
    /**
     * The machines of the states in the states-stack.
     */
    private Stack<Machine> machines;
    
    /**
     * The project in which the submachines will be searched for.
     */
    private Project project;
    
    /**
     * The status of the simulation.
     */
    private SimulationStatus status; 
    
    /**
     * Constructs a simulation. The simulation starts from the state named
     * "start" in the specified machine.
     * 
     * @param project The project to simulate.
     * @param machine_name The name of the machine in the project to simulate.
     * @param tape The input tape to simulate on.
     * 
     * @throws SimulationException if the project doesn't have the machine or
     * the machine does not have a start state.
     */
    public Simulation(
        Project project,
        String machine_name,
        Tape tape
    ) throws SimulationException {
        head = new Head(tape);
        this.project = project;
        status = SimulationStatus.RUNNING;
        
        Machine machine = project.getMachine(machine_name);
        if(machine == null) {
            throw new SimulationException(
                "The project does not have machine '" + machine_name + "'."
            );
        }
        State state = machine.getState("start");
        if(state == null) {
            throw new SimulationException(
                "The machine does not have a state named 'start'."
            );
        }
        
        states = new Stack<State>();
        machines = new Stack<Machine>();
        states.push(state);
        machines.push(machine);
        intoState();
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
     * Gets the state the simulation is currently/was last.
     * 
     * @return If the simulation status is REJECTED, the state from which the
     * simulation could not continue. If the simulation status is ACCEPTED, the
     * accepting state of the simulation. Otherwise the current state.
     */
    public State getState() {
        return states.peek();
    }
    
    /**
     * Gets the machine the simulation is currently/was last.
     * 
     * @return The machine getState() is in.
     */
    public Machine getMachine() {
        return machines.peek();
    }
    
    /**
     * Gets the head of the simulation.
     * 
     * @return The head that is used throughout the simulation.
     */
    public Head getHead() {
        return head;
    }
    
    /**
     * Gets the tape the simulation operates on.
     * 
     * @return The tape that the simulation operates on.
     */
    public Tape getTape() {
        return head.getTape();
    }
    
    /**
     * If the simulation is still running, runs it one transition forward. After
     * that, steps into and out of submachines such that next step starts again
     * with a transition.
     * 
     * @throws SimulationException if a submachine was not found, a submachine
     * did not have a start state or an infinite submachine recursion was found.
     * Leaves the simulation in an inconsistent state, should not be used
     * afterwards.
     */
    public void step() throws SimulationException {
        if(status == SimulationStatus.RUNNING) {
            State state = states.peek();
            
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
            
            states.pop();
            states.push(transition.getDestination());
            intoState();
        }
    }
    
    /**
     * Handle everything that must be done after entering the state on the top
     * of the stack, i.e. calling submachines and checking for accepted state.
     * 
     * @throws SimulationException if a submachine was not found, a submachine
     * did not have a start state or an infinite submachine recursion was found.
     * Leaves the simulation in an inconsistent state, should not be used
     * afterwards.
     */
    private void intoState() throws SimulationException {
        // While the state on the top has a submachine, go into it.
        int depth = 0;
        while(states.peek().getSubmachine() != null) {
            depth++;
            // If we have gone deeper than the number of machines in the
            // project, we are stuck in an infinite loop.
            if(depth > project.getMachineNames().size()) {
                throw new SimulationException(
                    "The project has an infinite submachine loop containing " +
                    "the start state of machine '" + machines.peek().getName() +
                    "'"
                );
            }
            
            String submachine_name = states.peek().getSubmachine();
            Machine submachine = project.getMachine(submachine_name);
            if(submachine == null) {
                throw new SimulationException(
                    "The project does not have machine '" + submachine_name +
                    "':\nReferred by state '" + getState().getName() + "' of " +
                    "machine '" + getMachine().getName() + "'."
                );
            }
            State state = submachine.getState("start");
            if(state == null) {
                throw new SimulationException(
                    "Machine '" + submachine_name + "' does not have a state " +
                    "named 'start'."
                );
            }
            
            states.push(state);
            machines.push(submachine);
        }
        
        // While the state on the top is accepting, return from it.
        while(states.peek().isAccepting()) {
            if(states.size() == 1) {
                status = SimulationStatus.ACCEPTED;
                return;
            } else {
                states.pop();
                machines.pop();
            }
        }
    }
    
    /**
     * Step the simulation until the status is no longer RUNNING.
     * 
     * Note that if the simulation might not terminate.
     * 
     * @throws SimulationException if a submachine was not found, a submachine
     * did not have a start state or an infinite submachine recursion was found.
     * Leaves the simulation in an inconsistent state, should not be used
     * afterwards.
     */
    public void run() throws SimulationException {
        while(getStatus() == SimulationStatus.RUNNING) {
            step();
        }
    }
}

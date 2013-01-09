package fi.helsinki.cs.turridevelop.file;

import fi.helsinki.cs.turridevelop.exceptions.FilesystemException;
import fi.helsinki.cs.turridevelop.exceptions.MalformedFileException;
import fi.helsinki.cs.turridevelop.exceptions.NameInUseException;
import fi.helsinki.cs.turridevelop.logic.Machine;
import fi.helsinki.cs.turridevelop.logic.Project;
import fi.helsinki.cs.turridevelop.logic.State;
import fi.helsinki.cs.turridevelop.logic.Transition;
import fi.helsinki.cs.turridevelop.util.Vec2;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Functions for reading Turr files.
 */
public class TurrInput {
    /**
     * Gets the machine names of all the Turr files in a directory. The file
     * names are the machine names with ".turr" in the end.
     * 
     * @param dir The directory to read.
     * @return Set of the machine names.
     * @throws FilesystemExcption if the directory cannot be opened.
     */
    public static Set<String> getMachineNamesInDirectory(
        File dir
    ) throws FilesystemException {
        // Get the list of files in the directory.
        File[] files = dir.listFiles();
        
        if(files == null) {
            throw new FilesystemException(
                "Could not open project directory '" + dir + "'"
            );
        }
        
        HashSet<String> names = new HashSet<String>();
        
        String extension = ".turr";
        for(File file : files) {
            String filename = file.getName();
            int extension_start = filename.length() - extension.length();
            if(
                extension_start < 0 ||
                !filename.substring(extension_start).equals(extension)
            ) {
                break;
            }
            String name = filename.substring(0, extension_start);
            names.add(name);
        }
        
        return names;
    }
    
    /**
     * Reads a project directory.
     * 
     * @param dir The project directory to read.
     * @return The project read from all .turr-machine files from the directory.
     * @throws FilesystemException if the project directory or the machine files
     * cannot be read.
     * @throws MalformedFileException if some machine files are malformed.
     */
    public static Project readProjectDirectory(
        File dir
    ) throws MalformedFileException, FilesystemException {
        // Get the list of machines.
        Set<String> names = getMachineNamesInDirectory(dir);
        
        HashMap<String, JSONObject> json = new HashMap<String, JSONObject>();
        for(String name : names) {
            // Read the source file.
            File file = new File(dir, name + ".turr");
            
            StringBuilder source = new StringBuilder();
            try {
                FileInputStream in = new FileInputStream(file);
                InputStreamReader reader = new InputStreamReader(in, "UTF-8");
                
                char[] buffer = new char[1024];
                int read;
                while((read = reader.read(buffer, 0, 1024)) != -1) {
                    source.append(buffer, 0, read);
                }
            } catch(Exception e) {
                throw new FilesystemException(
                    "Could not read machine file '" + file + "'."
                );
            }
            
            JSONObject machine_json;
            try {
                machine_json = new JSONObject(source.toString());
            } catch(JSONException e) {
                throw new MalformedFileException(
                    "Could not parse JSON from machine file '" + file + "'."
                );
            }
            json.put(name, machine_json);
        }
        
        Project project;
        try {
            project = JSONToProject(json);
        } catch(MalformedFileException e) {
            throw new MalformedFileException("Malformed machine files.");
        }
        
        return project;
    }
    
    /**
     * Reads a directory of JSON object representations of machines in Turr
     * format, and creates a Project from the representations.
     * 
     * @param json Map from the machine names to their JSON object
     * representations.
     * @return The project read from json.
     * @throws MalformedFileException if the file cannot be parsed.
     */
    public static Project JSONToProject(
        Map<String, JSONObject> json
    ) throws MalformedFileException {
        Project ret = new Project();
        try {
            // Read the states.
            for(String name : json.keySet()) {
                Machine machine = ret.addMachine(name);
                readStates(machine, json.get(name));
            }
        } catch(NameInUseException e) {
            throw new MalformedFileException();
        } catch(JSONException e) {
            throw new MalformedFileException();
        }
        
        return ret;
    }
    
    /**
     * Reads states from a Turr JSON object representation of a Machine.
     * 
     * @param machine The Machine to add the states to.
     * @param json The JSON object of the machine to read the states from.
     */
    private static void readStates(
        Machine machine,
        JSONObject json
    ) throws JSONException, NameInUseException, MalformedFileException {
        JSONObject states_json = json.getJSONObject("states");
        
        // First add states without transitions.
        Iterator iter = states_json.keys();
        while(iter.hasNext()) {
            String statename = (String) iter.next();
            State state = machine.addState(statename);
            JSONObject state_json = states_json.getJSONObject(statename);
            
            readState(state, state_json);
        }
        
        // Then add transitions, because now all destination states should
        // exist.
        iter = states_json.keys();
        while(iter.hasNext()) {
            String statename = (String) iter.next();
            State state = machine.getState(statename);
            JSONObject state_json = states_json.getJSONObject(statename);
            
            readTransitions(machine, state, state_json);
        }
    }
    
    /**
     * Reads all other data except transitions from a JSON object into a state.
     * 
     * @param state The state to read the data into.
     * @param json The JSON object describing the state.
     * @throws JSONException if some fields are missing in the JSON object.
     */
    private static void readState(
        State state, JSONObject json
    ) throws JSONException {
        if(json.get("accepting").equals(true)) {
            state.setAccepting(true);
        }
        
        Object submachine_json = json.get("submachine");
        if(submachine_json != JSONObject.NULL) {
            state.setSubmachine((String) submachine_json);
        }
        
        double x = json.getDouble("x");
        double y = json.getDouble("y");
        state.setPosition(new Vec2(x, y));
    }
    
    /**
     * Reads transitions from a JSON object into a state.
     * 
     * @param machine The machine containing the state.
     * @param state The state to read the data into.
     * @param json The JSON object describing the state.
     * @throws JSONException if some fields are missing in the JSON object.
     * @throws MalformedFileException in case of invalid field contents in the
     * JSON object.
     * @throws NameInUseException if some transitions have the same input
     * character.
     */
    private static void readTransitions(
        Machine machine, State state, JSONObject json
    ) throws JSONException, MalformedFileException, NameInUseException {
        JSONArray transitions_json = json.getJSONArray("transitions");
            
        for(int i = 0; i < transitions_json.length(); i++) {
            JSONObject transition_json = transitions_json.getJSONObject(i);
            
            String destname = transition_json.getString("destination");
            State destination = machine.getState(destname);
            if(destination == null) {
                throw new MalformedFileException();
            }
            String inchar = transition_json.getString("inchar");
            
            Character outchar = null;
            Object outchar_obj = transition_json.get("outchar");
            if(outchar_obj instanceof String) {
                String outchar_str = (String) outchar_obj;
                if(outchar_str.length() != 1) {
                    throw new MalformedFileException();
                }
                outchar = outchar_str.charAt(0);
            }
            
            int move;
            String move_str = transition_json.getString("move");
            if(move_str.equals("L")) {
                move = -1;
            } else if(move_str.equals("R")) {
                move = 1;
            } else if(move_str.equals("S")) {
                move = 0;
            } else {
                throw new MalformedFileException();
            }
            
            state.addTransition(new Transition(
                destination, inchar, outchar, move
            ));
        }
    }
}

package fi.helsinki.cs.turridevelop.util;

import fi.helsinki.cs.turridevelop.exceptions.NameInUseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

/**
 * Container that stores elements of type T by their names and tracks their
 * name changes. Does not allow two elements to have the same name.
 */
public class ByNameContainer<T extends ByNameStored> {
    /**
     * Map from the element names to the corresponding elements.
     */
    private HashMap<String, T> elements;
    
    /**
     * Constructs a ByNameContainer.
     */
    public ByNameContainer() {
        elements = new HashMap<String, T>();
    }
    
    /**
     * Adds an element to the container.
     * 
     * @param element The element to add.
     * @throws NameInUseException if the name is already in use. In that case,
     * the element is not added.
     */
    public void add(T element) throws NameInUseException {
        if(elements.containsKey(element.getName())) {
            throw new NameInUseException();
        }
        elements.put(element.getName(), element);
    }
    
    /**
     * Removes an element from the container.
     * 
     * If the element is not found, nothing is done.
     * 
     * @param name The name of the element.
     */
    public void remove(String name) {
        elements.remove(name);
    }
    
    /**
     * Gets an element from the container by its name.
     * 
     * @param name The name of the element.
     * @return The element or null if not found.
     */
    public T get(String name) {
        return elements.get(name);
    }
    
    /**
     * Check if the container has element with given name.
     * 
     * @param name The name to find.
     * @return True if the container has element with the requested name, false
     * otherwise.
     */
    public boolean has(String name) {
        return elements.containsKey(name);
    }
    
    /**
     * Gets the set of all element names.
     * 
     * @return The set of element names.
     */
    public Set<String> getNames() {
        return Collections.unmodifiableSet(elements.keySet());
    }
    
    /**
     * Function used by elements to notify when their names change.
     * 
     * @param old_name The old name of the element.
     * @throws RuntimeException if there was no element with name old_name or
     * the new name is in use. This should not happen because ByNameStored
     * objects should check the availibility with hasElement before calling.
     */
    public void nameChanged(String old_name) {
        if(!elements.containsKey(old_name)) {
            throw new RuntimeException("nameChanged called with unknown name.");
        }
        
        T element = elements.get(old_name);
        
        if(elements.containsKey(element.getName())) {
            throw new RuntimeException(
                "The new name of the object passed to nameChanged is in use."
            );
        }
        
        elements.remove(old_name);
        elements.put(element.getName(), element);
    }
}
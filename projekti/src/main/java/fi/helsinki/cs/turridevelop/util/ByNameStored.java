package fi.helsinki.cs.turridevelop.util;

/**
 * A class implements the ByNameStored interface to indicate that it only
 * changes its name when it has tested that all ByNameContainers registered to
 * the instance do not already have element with the same name by calling their
 * 'has' methods and after a successful change notifies them by calling their
 * 'nameChanged' methods. The ByNameStored should NOT add itself to the
 * container.
 */
public interface ByNameStored {
    /**
     * Gets the name of the object.
     * 
     * @return The current name of the object.
     */
    String getName();
}

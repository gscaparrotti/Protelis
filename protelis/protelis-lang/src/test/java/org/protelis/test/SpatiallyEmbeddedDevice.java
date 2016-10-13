package org.protelis.test;

import org.protelis.lang.datatype.Field;
import org.protelis.vm.ExecutionContext;

/**
 * A device embedded in space.
 */
public interface SpatiallyEmbeddedDevice extends ExecutionContext {

    /**
     * Get the distance between the current device and its neighbors. Distance
     * must be positive.
     * 
     * @return field of distances
     */
    Field nbrRange();
}

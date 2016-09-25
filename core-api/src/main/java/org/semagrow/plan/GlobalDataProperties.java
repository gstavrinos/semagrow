package org.semagrow.plan;

import java.util.Optional;
import java.util.Set;

/**
 * @author acharal
 */
public class GlobalDataProperties {

    PartitioningScheme partitioningScheme;

    Set<String> partitioningFields;

    Optional<Ordering> ordering;

    public boolean isPartitionedOnVariables(Set<String> variables) {
        if (partitioningScheme.isPartitionedOnKey()) {
            variables.containsAll(partitioningFields);
        }
        return false;
    }

}

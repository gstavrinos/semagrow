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

    /**
     * Checks whether the data properties are partitioned on a key and if so,
     * if all the partitioned variables are included in the given set.
     * <p>
     * Let {C_1, C_2} be the partitioning variables. It follows that the stream
     * is also partitioned on { C_1, C_2, C_3 } but not on just C_1. It is easy to
     * see that if partitioned in {C_1, C_2} it might occur that the rows with
     * the same value of C_1 (but different C_2) may reside on different partitions.
     *
     * @param variables
     * @return True, if it is partitioned on the set of variables; false if
     *         it is partitioned in some variables that are not in {@code variables}
     */
    public boolean isPartitionedOnVariables(Set<String> variables) {
        if (partitioningScheme.isPartitionedOnKey()) {
            variables.containsAll(partitioningFields);
        }
        return false;
    }

}

package org.semagrow.plan;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

/**
 * @author acharal
 */
public class GlobalDataProperties {

    PartitioningScheme partitioningScheme;

    Set<String> partitioningVariables;

    Optional<Ordering> ordering;

    public void setHashPartitioned(Set<String> partitioningFields) {
        this.partitioningScheme    = PartitioningScheme.HASH_PARTITIONING;
        this.partitioningVariables = partitioningFields;
        this.ordering = Optional.empty();
    }

    public void setRangePartitioned(Ordering ordering) {
        this.partitioningScheme = PartitioningScheme.RANGE_PARTITIONING;
        this.partitioningVariables = ordering.getVariables();
        this.ordering = Optional.of(ordering);
    }

    public void setAnyPartitioned(Set<String> partitioningFields) {
        this.partitioningScheme    = PartitioningScheme.ANY_PARTITIONING;
        this.partitioningVariables = partitioningFields;
        this.ordering = Optional.empty();
    }

    public void setRandomPartitioned() {
        this.partitioningScheme = PartitioningScheme.RANDOM;
        this.ordering = Optional.empty();
    }

    public void setReplication() {
        this.partitioningScheme = PartitioningScheme.REPLICATE;
        this.ordering = Optional.empty();
    }

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
        // FIXME: Treat the case where this is ordered and not just hash-partitioned

        if (partitioningScheme.isPartitionedOnKey()) {
            variables.containsAll(partitioningVariables);
        }
        return false;
    }

    public boolean matchesOrderedPartitioning(Optional<Ordering> ordering) {

        if (partitioningScheme == PartitioningScheme.RANGE_PARTITIONING) {
            Ordering tord = this.ordering.get();
            Ordering oord = ordering.get();

            Iterator<Ordering.OrderedVariable> tit  = tord.getOrderedVariables();
            Iterator<Ordering.OrderedVariable> oit  = oord.getOrderedVariables();

            while (oit.hasNext()) {
                if (tit.hasNext()) {
                    Ordering.OrderedVariable oo = oit.next();
                    Ordering.OrderedVariable to = tit.next();

                    if (!oo.isCoveredBy(to)) {
                        // stop and return failure
                        return false;
                    }
                } else {
                    // we finished this.ordering without finishing other.ordering
                    // this means that the partitions are ordered with less columns than requested.
                    return true;
                }
            }
        }
        return false;
    }

}

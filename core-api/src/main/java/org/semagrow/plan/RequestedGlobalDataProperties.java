package org.semagrow.plan;

import java.util.Optional;
import java.util.Set;

/**
 *
 * @author acharal
 */
public class RequestedGlobalDataProperties {

    PartitioningScheme partitioningScheme;

    Set<String> partitioningVariables;

    Optional<Ordering> ordering;


    public RequestedGlobalDataProperties() {
        partitioningScheme = PartitioningScheme.RANDOM;
    }

    public void setHashPartitioned(Set<String> partitioningFields) {
        this.partitioningScheme    = PartitioningScheme.HASH_PARTITIONING;
        this.partitioningVariables = partitioningFields;
        this.ordering = Optional.empty();
    }

    public void setRangePartitioned(Ordering ordering) {
        this.partitioningScheme = PartitioningScheme.RANGE_PARTITIONING;
        this.ordering = Optional.of(ordering);
    }

    public void setAnyPartitioned(Set<String> partitioningFields) {
        this.partitioningScheme    = PartitioningScheme.ANY_PARTITIONING;
        this.partitioningVariables = partitioningFields;
    }

    public PartitioningScheme getPartitioningScheme() { return partitioningScheme; }

    public boolean isTrivial() {
        return this.partitioningScheme == PartitioningScheme.RANDOM;
    }

    public boolean isCoveredBy(GlobalDataProperties other) {

        if (this.partitioningScheme.isCoveredBy(other.partitioningScheme)) {

            if (other.partitioningScheme.isPartitionedOnKey()) {
                // if other is partitioned at least on the variables of this
                return other.isPartitionedOnVariables(this.partitioningVariables);
            }

            if (this.partitioningScheme == PartitioningScheme.RANGE_PARTITIONING) {
                // check ordering
            }
        }

        return false;
    }

}

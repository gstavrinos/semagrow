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

    public PartitioningScheme getPartitioningScheme() { return partitioningScheme; }

    public boolean isTrivial() {
        return this.partitioningScheme == PartitioningScheme.RANDOM;
    }

    public boolean isCoveredBy(GlobalDataProperties other) {

        if (this.partitioningScheme == PartitioningScheme.HASH_PARTITIONING &&
            other.partitioningScheme == PartitioningScheme.HASH_PARTITIONING) {
            // if other is partitioned at least on the variables of this
            return other.isPartitionedOnVariables(this.partitioningVariables);

        } else if (this.partitioningScheme == PartitioningScheme.RANGE_PARTITIONING &&
                other.partitioningScheme == PartitioningScheme.RANGE_PARTITIONING) {
            return other.matchesOrderedPartitioning(this.ordering);

        } else if (this.partitioningScheme == PartitioningScheme.ANY_PARTITIONING &&
                   other.partitioningScheme.isPartitionedOnKey())
        {
            return other.isPartitionedOnVariables(this.partitioningVariables);
        } else if (this.partitioningScheme == PartitioningScheme.RANDOM) {
            return other.partitioningScheme == PartitioningScheme.RANDOM;
        } else if (this.partitioningScheme == PartitioningScheme.REPLICATE) {
            return other.partitioningScheme == PartitioningScheme.REPLICATE;
        }

        return false;
    }

}

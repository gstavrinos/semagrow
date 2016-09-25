package org.semagrow.plan;

/**
 * @author acharal
 */
public enum PartitioningScheme {

    RANDOM,              // partitioning is random (e.g. round-robin)

    ANY_PARTITIONING,    // any partitioning scheme

    HASH_PARTITIONING,   // partitioning using a hash function

    RANGE_PARTITIONING,  // partitioning using ranges

    REPLICATE;           // each partition gets a full replication

    public boolean isPartitionedOnKey() {
        return this == ANY_PARTITIONING || this == HASH_PARTITIONING || this == RANGE_PARTITIONING;
    }


    public boolean isCoveredBy(PartitioningScheme other) {
        if (other == ANY_PARTITIONING)
            return this == ANY_PARTITIONING || this == HASH_PARTITIONING || this == RANGE_PARTITIONING;

        return this == other;
    }

}

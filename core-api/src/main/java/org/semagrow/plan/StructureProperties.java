package org.semagrow.plan;

import java.util.Optional;
import java.util.Set;

/**
 * This class is used to describe properties regarding the structure of the data
 * such as ordering and grouping.
 */
public class StructureProperties {

    /**
     * The optional ordering of the data.
     */
    Optional<Ordering> ordering;

    /**
     * The optional grouping of the data.
     * Grouping is represented by the set of the grouped fields.
     */
    Optional<Set<String>> groupedFields;

    /**
     * The set of fields that combined identify uniquely the row.
     */
    Set<Set<String>> uniqueFields;

    public boolean isCoveredBy(StructureProperties other) {

        if (ordering.isPresent()) {
            // check whether the ordering is covered by the other ordering
            return other.ordering
                    .map( o -> o.isCoveredBy(ordering.get()) )
                    .orElse(false);
        } else if (groupedFields.isPresent()) {
            if (other.groupedFields.isPresent()) {
                // is grouped at least at the same fields
                // or the rest of the fields are unique.
                return other.groupedFields.get().containsAll(groupedFields.get());
            }
        }

        return true;
    }

    public boolean isTrivial() {
        return !ordering.isPresent() && !groupedFields.isPresent();
    }

}

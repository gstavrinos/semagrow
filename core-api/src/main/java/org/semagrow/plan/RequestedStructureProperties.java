package org.semagrow.plan;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Created by angel on 22/9/2016.
 */
public class RequestedStructureProperties {

    /**
     * The optional ordering of the data.
     */
    Optional<Ordering> ordering;

    /**
     * The optional grouping of the data.
     * Grouping is represented by the set of the grouped fields.
     */
    Optional<Set<String>> groupedFields;

    public RequestedStructureProperties(){
        ordering = Optional.empty();
        groupedFields = Optional.empty();
    }

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

    public static RequestedStructureProperties forOrdering(Ordering o) {
        RequestedStructureProperties props = new RequestedStructureProperties();
        props.ordering = Optional.of(o.clone());
        return props;
    }

    public static RequestedStructureProperties forGrouping(Set<String> groupedFields) {
        RequestedStructureProperties props = new RequestedStructureProperties();
        props.groupedFields = Optional.of(new HashSet<>(groupedFields));
        return props;
    }


}

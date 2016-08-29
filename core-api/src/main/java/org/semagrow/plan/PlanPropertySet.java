package org.semagrow.plan;

import org.semagrow.selector.Site;

/**
 * A collection of attributes of a query {@link Plan}.
 * @author acharal
 */
public interface PlanPropertySet {

    /**
     * Returns the estimated cardinality of the plan
     */
    long getCardinality();

    void setCardinality(long card);

    /**
     * Returns the estimated cost of the plan
     */
    Cost getCost();

    void setCost(Cost cost);

    /**
     * Returns the site where the plan will execute.
     */
    Site getSite();

    void setSite(Site site);

    /**
     * Returns the ordering of the result set that will be yielded
     * w  hen the plan will be evaluated.
     */
    StructureProperties getStructureProperties();

    void setStructureProperties(StructureProperties ordering);

    /**
     * Checks if this property set is comparable with another property set.
     * A property set is comparable if every property defined in the set
     * is comparable with the respective property on the other property set.
     * @param properties a property set to be compared
     * @return {@code true} if the property sets are comparable, {@code false} otherwise.
     */
    boolean isComparable(PlanPropertySet properties);


    /**
     * Clones a property set to a new object
     * @return a newly allocated {@link PlanPropertySet} object
     */
    PlanPropertySet clone();
}

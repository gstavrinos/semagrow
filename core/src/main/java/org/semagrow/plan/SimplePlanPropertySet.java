package org.semagrow.plan;


import org.semagrow.local.LocalSite;
import org.semagrow.selector.Site;

/**
 * A structure that contains the {@link Plan} properties needed
 * by the {@link PlanOptimizer}.
 *
 * @author acharal
 */
public class SimplePlanPropertySet implements PlanPropertySet {

    private Cost nodeCost;

    private Cost cumulativeCost;

    private long cardinality;

    private Site site;

    private StructureProperties structProps;

    @Override
    public long getCardinality() { return cardinality; }

    public void setCardinality(long card) { this.cardinality = card;}

    @Override
    public Cost getCost() { return nodeCost; }

    public void setCost(Cost cost) { this.nodeCost = cost; }

    @Override
    public Site getSite() { return site; }

    public void setSite(Site site) { this.site = site; }

    @Override
    public StructureProperties getStructureProperties() {
        return structProps;
    }

    @Override
    public void setStructureProperties(StructureProperties ordering) {
        structProps = ordering;
    }

    public static SimplePlanPropertySet defaultProperties() {
        SimplePlanPropertySet p = new SimplePlanPropertySet();
        p.setSite(LocalSite.getInstance());
        p.setCost(new Cost(0));
        return p;
    }

    public PlanPropertySet clone() {
        SimplePlanPropertySet p = new SimplePlanPropertySet();
        p.nodeCost = this.nodeCost;
        p.structProps = this.structProps;
        p.site = this.site;
        return p;
    }

    @Override
    public boolean isComparable(PlanPropertySet properties) {
        return (properties.getSite().equals(this.getSite()));
    }
}

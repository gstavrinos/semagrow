package org.semagrow.plan.queryblock;

import org.eclipse.rdf4j.query.BindingSet;
import org.semagrow.plan.Plan;
import org.semagrow.plan.PlanCollection;

import java.util.Set;

/**
 *
 * @author acharal
 */
public class BindingSetAssignmentBlock extends AbstractQueryBlock {


    private Set<String> bindingNames;

    private Iterable<BindingSet> bindingSets;

    public BindingSetAssignmentBlock(Set<String> bindingNames, Iterable<BindingSet> bindingSets) {
        assert bindingNames != null && bindingSets != null;
        this.bindingNames = bindingNames;
        this.bindingSets = bindingSets;
    }

    @Override
    public Set<String> getOutputVariables() { return bindingNames; }

    public boolean hasDuplicates() { return true; }

    public PlanCollection getPlans() { return null; }

    public Plan getBestPlan() { return null; }

}

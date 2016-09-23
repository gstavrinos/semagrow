package org.semagrow.plan.queryblock;

import org.eclipse.rdf4j.query.algebra.StatementPattern;
import org.eclipse.rdf4j.query.algebra.helpers.VarNameCollector;
import org.semagrow.plan.Plan;
import org.semagrow.plan.PlanCollection;

import java.util.Set;

/**
 * Created by angel on 6/9/2016.
 */
public class PatternBlock extends AbstractQueryBlock {

    private StatementPattern pattern;

    public PatternBlock(StatementPattern pattern) {
        assert pattern != null;
        this.pattern = pattern;
    }

    public Set<String> getOutputVariables() {
        return VarNameCollector.process(pattern);
    }

    public PlanCollection getPlans() { return null; }

    public Plan getBestPlan() { return null; }

    public boolean hasDuplicates() { return true; }

}
